import pandas as pd
import numpy as np
import os
import re
from django.views.decorators.csrf import csrf_exempt
from django.utils.decorators import method_decorator
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .serializers import SignupSerializer
from rest_framework.decorators import api_view
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny
from .models import EmailOTP, Profile
from .serializers import LoginSerializer
from rest_framework.authtoken.models import Token
from django.contrib.auth.models import User
from .models import Profile
from .serializers import ForgotPasswordSerializer
from rest_framework.permissions import IsAuthenticated
from django.conf import settings
from .models import FavouriteBike
from .serializers import FavouriteBikeSerializer
from .models import BikeReview
from .serializers import BikeReviewSerializer
from rest_framework.permissions import IsAuthenticatedOrReadOnly
from .models import AppReview
from .serializers import AppReviewSerializer
from .serializers import FeedbackSerializer
from .models import Feedback
from django.http import JsonResponse
from django.core.mail import send_mail
import random


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")




@api_view(['POST'])
@permission_classes([AllowAny])
def signup(request):
    username = request.data.get('username')
    email = request.data.get('email')
    password = request.data.get('password')
    confirm = request.data.get('confirm_password')

    if password != confirm:
        return Response(
            {"error": "Passwords do not match"},
            status=400
        )

    if User.objects.filter(email=email).exists():
        return Response(
            {"error": "Email already exists"},
            status=400
        )
    
    # CHECK VERIFICATION
    # For now, we allow signup if:
    # 1. EmailOTP exists AND is_verified=True
    # OR
    # 2. (Optional backup) if we decide to allow unverified signups but mark them unverified.
    # User Requirement: "Navigation Restriction: The user should not be able to proceed past the signup screen until their email is verified."
    # So we MUST enforce verification.


    is_verified = False
    
    # Check if this email has a verified OTP that hasn't been used yet
    try:
        otp_entry = EmailOTP.objects.get(email=email, is_verified=True)
        is_verified = True
    except EmailOTP.DoesNotExist:
        # If no verified OTP, return error
        # UNLESS this is a dev/test bypass (omitted for prod)
        return Response(
            {"error": "Please verify your email first"},
            status=400
        )

    # Create User
    # KEY CHANGE: Use email as the UNIQUE username.
    # Store the actual display name (which can be duplicate) in first_name.
    user = User.objects.create_user(
        username=email,  # Unique ID
        email=email,
        password=password,
        first_name=username # Display Name (allows duplicates)
    )

    # Create Profile & Mark Verified
    Profile.objects.update_or_create(
        user=user,
        defaults={'is_email_verified': True}
    )

    # Consume the OTP (Delete it so it can't be reused for another account)
    otp_entry.delete()

    return Response(
        {"message": "User created"},
        status=201
    )






@api_view(['POST'])
@permission_classes([AllowAny])
def login(request):
    email = request.data.get('email')
    password = request.data.get('password')

    user = User.objects.filter(email=email).first()

    if user is None or not user.check_password(password):
        return Response({"error": "Invalid credentials"}, status=400)

    token, _ = Token.objects.get_or_create(user=user)

    # Fetch profile image
    profile = Profile.objects.filter(user=user).first()
    image_url = ""
    if profile and profile.image:
        image_url = request.build_absolute_uri(profile.image.url)

    return Response({
        "token": token.key,
        "username": user.first_name, # Return Display Name instead of internal username
        "email": user.email,
        "profile_image": image_url
    })





@method_decorator(csrf_exempt, name='dispatch')
class ForgotPasswordView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        serializer = ForgotPasswordSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(
                {"message": "Password updated successfully"},
                status=status.HTTP_200_OK
            )
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def upload_profile_image(request):

    profile, _ = Profile.objects.get_or_create(user=request.user)

    profile.image = request.FILES.get('image')
    profile.save()

    return Response({
        "image_url": request.build_absolute_uri(profile.image.url)
    })


# --- HELPERS ---
def get_user_favs(request):
    if request.user.is_authenticated:
        return set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))
    return set()

def parse_price(raw_price):
    try:
        raw_str = str(raw_price).lower().strip()
        if "lakh" in raw_str:
            num = re.findall(r"[\d\.]+", raw_str)
            if num: return int(float(num[0]) * 100000)
        elif "cr" in raw_str:
             num = re.findall(r"[\d\.]+", raw_str)
             if num: return int(float(num[0]) * 10000000)
        else:
            clean_price = re.sub(r'[^\d\.]', '', raw_str)
            if clean_price: return int(float(clean_price))
    except: pass
    return 0

def resolve_bike_image_url(model_name, request=None):
    """
    Dynamically finds the image for a bike model.
    Checks media/bikes/ for a match.
    """
    if not model_name:
        return ""

    # Global Cache for performance
    global _IMAGE_CACHE
    media_bikes_path = os.path.join(settings.MEDIA_ROOT, 'bikes')

    def is_placeholder(filename):
        """Checks if a file is a known placeholder (usually 44421 bytes)."""
        if filename == "placeholder.png": return True
        path = os.path.join(media_bikes_path, filename)
        try:
            # We check size 44421 which is the specific size of the 'coming soon' AI image
            return os.path.getsize(path) == 44421
        except:
            return False
    
    if '_IMAGE_CACHE' not in globals() or _IMAGE_CACHE is None:
         if os.path.exists(media_bikes_path):
             files = os.listdir(media_bikes_path)
             # Cache stores both full names and squashed names (prefixed with SQ_)
             cache = {}
             for f in files:
                 if os.path.isdir(os.path.join(media_bikes_path, f)): continue
                 f_lower = f.lower()
                 cache[f_lower] = f
                 name_only = os.path.splitext(f_lower)[0]
                 squashed = re.sub(r'[^a-z0-9]', '', name_only)
                 if squashed:
                     sq_key = f"SQ_{squashed}"
                     # Prioritize NON-placeholder files for the squashed key
                     if sq_key not in cache or is_placeholder(cache[sq_key]):
                         cache[sq_key] = f
             _IMAGE_CACHE = cache
         else:
             _IMAGE_CACHE = {}
    
    available_files_lower = _IMAGE_CACHE


    # Remove BOM and special chars, lowercase
    raw_name = str(model_name).replace('\ufeff', '').lower().strip()
    clean_name = re.sub(r'[^a-z0-9]', ' ', raw_name).strip()
    squashed_name = re.sub(r'[^a-z0-9]', '', raw_name)
    
    # Ignore header/metadata items
    if clean_name in ["model", "commuter bikes", "sports bikes", "scooters", "cruisers", "adventure bikes", "touring bikes", "electric vehicles", "discontinued models", "commuter", "street/naked", "sports", "adventure/tourer", "scrambler", "cruiser", "scooter", "commuter/street", "sports/street", "supersports", "streetfighter/naked"]:
        return ""

    # 1. Try Exact and Squashed Matches first
    extensions = ['.png', '.webp', '.avif', '.jpg', '.jpeg']
    candidates = [
        squashed_name,
        clean_name,
        clean_name.replace(" ", "-"),
        clean_name.replace(" ", "_"),
    ]
    
    for cand in candidates:
        if not cand: continue
        # 1a. Try direct squashed match (handles Hornet 2.0 -> hornet20)
        sq_key = f"SQ_{re.sub(r'[^a-z0-9]', '', cand)}"
        if sq_key in available_files_lower:
            actual_filename = available_files_lower[sq_key]
            if not is_placeholder(actual_filename):
                relative_path = f"bikes/{actual_filename}"
                if request: return request.build_absolute_uri(settings.MEDIA_URL + relative_path)
                return settings.MEDIA_URL + relative_path

        # 1b. Try with extensions
        for ext in extensions:
            test_file = f"{cand}{ext}"
            if test_file in available_files_lower:
                actual_filename = available_files_lower[test_file]
                if not is_placeholder(actual_filename):
                    relative_path = f"bikes/{actual_filename}"
                    if request: return request.build_absolute_uri(settings.MEDIA_URL + relative_path)
                    return settings.MEDIA_URL + relative_path

    # 2. Try stripping brand names
    parts = clean_name.split()
    if len(parts) > 1:
        no_brand_candidates = []
        if len(parts) > 1: no_brand_candidates.append(" ".join(parts[1:]))
        if len(parts) > 2: no_brand_candidates.append(" ".join(parts[2:]))
        
        for nb_cand in no_brand_candidates:
            nb_sq_key = f"SQ_{re.sub(r'[^a-z0-9]', '', nb_cand)}"
            if nb_sq_key in available_files_lower:
                actual_filename = available_files_lower[nb_sq_key]
                if not is_placeholder(actual_filename):
                    relative_path = f"bikes/{actual_filename}"
                    if request: return request.build_absolute_uri(settings.MEDIA_URL + relative_path)
                    return settings.MEDIA_URL + relative_path

            for ext in extensions:
                test_file = f"{nb_cand}{ext}"
                if test_file in available_files_lower:
                    actual_filename = available_files_lower[test_file]
                    if not is_placeholder(actual_filename):
                        relative_path = f"bikes/{actual_filename}"
                        if request: return request.build_absolute_uri(settings.MEDIA_URL + relative_path)
                        return settings.MEDIA_URL + relative_path

    # 3. Fuzzy Match
    model_words = [w for w in clean_name.split() if len(w) > 1]
    if len(model_words) >= 1:
        for file_lower, actual_name in available_files_lower.items():
            if file_lower.startswith("sq_"): continue # Skip internal cache keys
            if is_placeholder(actual_name): continue
            
            file_clean = re.sub(r'[^a-z0-9]', ' ', file_lower)
            file_words = set(file_clean.split())
            
            if set(model_words).issubset(file_words):
                 relative_path = f"bikes/{actual_name}"
                 if request: return request.build_absolute_uri(settings.MEDIA_URL + relative_path)
                 return settings.MEDIA_URL + relative_path

    # 4. Fallback: return placeholder
    placeholder_path = "bikes/placeholder.png"
    if request: return request.build_absolute_uri(settings.MEDIA_URL + placeholder_path)
    return settings.MEDIA_URL + placeholder_path
    
def get_all_columns(df):
    def find_col(keywords):
        for k in keywords:
            if k in df.columns: return k
        for col in df.columns:
            for k in keywords:
                if k in col: return col
        return None

    return {
        'model': find_col(["model", "model name", "bike model"]),
        'price': find_col(["price", "ex-showroom price", "price (inr)", "cost", "mrp"]),
        'image': find_col(["image", "image url", "picture"]),
        'type': find_col(["body type", "type"]),
        'engine': find_col(["displacement", "engine"]),
        'mileage': find_col(["mileage"]),
        'brand': find_col(["brand"]),
        'category': find_col(["category", "segment"]), # description
        'description': find_col(["description", "summary"]),
        
        # Dynamic Specs
        'max_power': find_col(["max power", "power"]),
        'max_torque': find_col(["max torque", "torque"]),
        'kerb_weight': find_col(["kerb weight", "weight", "kerb"]),
        'transmission': find_col(["transmission", "gearbox", "no. of gears"]),
        'fuel_tank_capacity': find_col(["fuel tank", "tank capacity"]),
        'braking_system': find_col(["braking system", "abs", "safety"]),
        'top_speed': find_col(["top speed", "speed"]),
        
        # Full Specs
        'front_brake_type': find_col(["front brake type", "front brake"]),
        'rear_brake_type': find_col(["rear brake type", "rear brake"]),
        'front_suspension': find_col(["front suspension"]),
        'rear_suspension': find_col(["rear suspension"]),
        'tyre_type': find_col(["tyre type", "tire type"]),
        'headlight': find_col(["headlight", "head lamp"]),
        'tail_light': find_col(["tail light", "tail lamp"]),
        'battery_capacity': find_col(["battery capacity", "battery"]),
        'usage': find_col(["usage"]),
        'fuel_type': find_col(["fuel", "fuel type"]),
        
        # Dimensions
        'overall_length': find_col(["overall length", "length"]),
        'overall_width': find_col(["overall width", "width"]),
        'seat_height': find_col(["seat height", "saddle height"]),
        'ground_clearance': find_col(["ground clearance", "clearance"]),
        'instrument_cluster': find_col(["instrument cluster", "cluster", "console"])
    }

def build_bike_entry(row, cols, user_favs, request=None):
    # cols = {model, price, image, type, engine, mileage}
    model_name = row.get(cols['model'], "")
    price_val = parse_price(row.get(cols['price'], 0))
    
    # IMPROVED: Dynamic Image Resolution
    img_url = resolve_bike_image_url(model_name, request)
    
    # Fallback to excel value if our dynamic resolution fails
    if not img_url:
        img_url = str(row.get(cols['image'], "")).strip()
        if img_url.lower() == "nan": img_url = ""
        elif img_url and not img_url.startswith(('http', '/')):
            # If it's just a filename in excel, try to make it a media URL
            if not img_url.startswith('bikes/'):
                 img_url = f"bikes/{img_url}"
            if request:
                img_url = request.build_absolute_uri(settings.MEDIA_URL + img_url)
            else:
                img_url = settings.MEDIA_URL + img_url

    return {
        "name": model_name,
        "price": price_val,
        "imageUrl": img_url,
        "vehicleType": row.get(cols.get('type'), "Motorcycle"),
        "engine": row.get(cols.get('engine'), ""),
        "mileage": row.get(cols.get('mileage'), ""),
        "usage": "Daily use",
        "badge": "",
        "isFavorite": model_name in user_favs,
        "brand": row.get(cols.get('brand'), ""),
        "description": row.get(cols.get('category'), "") or row.get(cols.get('description'), ""),
        
        # Dynamic Specs
        "max_power": row.get(cols.get('max_power'), ""),
        "max_torque": row.get(cols.get('max_torque'), ""),
        "kerb_weight": row.get(cols.get('kerb_weight'), ""),
        "transmission": row.get(cols.get('transmission'), ""),
        "fuel_tank_capacity": row.get(cols.get('fuel_tank_capacity'), ""),
        "braking_system": row.get(cols.get('braking_system'), ""),
        "top_speed": row.get(cols.get('top_speed'), ""),
        "instrument_cluster": row.get(cols.get('instrument_cluster'), ""),
        
        # Full Specs
        "front_brake_type": row.get(cols.get('front_brake_type'), ""),
        "rear_brake_type": row.get(cols.get('rear_brake_type'), ""),
        "front_suspension": row.get(cols.get('front_suspension'), ""),
        "rear_suspension": row.get(cols.get('rear_suspension'), ""),
        "tyre_type": row.get(cols.get('tyre_type'), ""),
        "headlight": row.get(cols.get('headlight'), ""),
        "tail_light": row.get(cols.get('tail_light'), ""),
        "tail_light": row.get(cols.get('tail_light'), ""),
        "battery_capacity": row.get(cols.get('battery_capacity'), ""),
        
        # Dimensions
        "overall_length": row.get(cols.get('overall_length'), ""),
        "overall_width": row.get(cols.get('overall_width'), ""),
        "seat_height": row.get(cols.get('seat_height'), ""),
        "ground_clearance": row.get(cols.get('ground_clearance'), "")
    }

class AiSuggestAPIView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        try:
            data = request.data
            budget = int(data.get("budget", 0))
            vehicle_type = str(data.get("vehicle_type", "")).lower()
            ride_with = str(data.get("ride_with", "")).lower()
            usage = str(data.get("usage", "")).lower()
            distance_km = int(data.get("distance_km", 0))
            fuel_priority = str(data.get("fuel_priority", "")).lower()
            comfort = str(data.get("comfort_pref", "")).lower()
            category_pref = str(data.get("bike_category", "")).lower()
            experience = str(data.get("experience", "")).lower()

            # 2. Load Data from AI specific source
            AI_EXCEL_PATH = os.path.join(os.path.dirname(__file__), "ai", "Ai-bikedata.xlsx")
            if not os.path.exists(AI_EXCEL_PATH):
                 AI_EXCEL_PATH = EXCEL_PATH

            df = pd.read_excel(AI_EXCEL_PATH, header=0)
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            # 3. Column Identification
            def find_col(keywords):
                for k in keywords:
                    if k in df.columns: return k
                for col in df.columns:
                    for k in keywords:
                        if k in col: return col
                return None

            price_col = find_col(["price", "ex-showroom price", "cost"])
            model_col = find_col(["bike_name", "model", "name"])
            type_col = find_col(["type", "body type"])
            usage_col = find_col(["usage"])
            comfort_col = find_col(["comfort"])
            category_col = find_col(["category"])
            exp_col = find_col(["experience"])
            mileage_col = find_col(["mileage"])
            engine_col = find_col(["engine", "displacement", "cc"])

            # Load Search Bikes for full details
            df_search = pd.read_excel(EXCEL_PATH, header=1)
            df_search.columns = df_search.columns.str.lower().str.strip()
            df_search = df_search.fillna("")
            cols_search = get_all_columns(df_search)

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            results = []

            # 4. Scoring Logic (Weighted Match)
            
            def get_matches(strict_mode=True):
                local_results = []
                for _, row in df.iterrows():
                    score = 0
                    max_score = 100 
                    
                    # Extract Data
                    try: 
                        raw_price = str(row.get(price_col, 0)).replace(",", "")
                        price_val = float(re.search(r"(\d+)", raw_price).group(1)) if re.search(r"(\d+)", raw_price) else 0
                    except: price_val = 0

                    # 0. Price Filter (Critical)
                    # If price is 0 or missing, it is invalid for recommendation
                    if price_val <= 0: continue

                    row_type = str(row.get(type_col, "")).lower()
                    row_cat = str(row.get(category_col, "")).lower()
                    row_usage = str(row.get(usage_col, "")).lower()
                    row_comfort = str(row.get(comfort_col, "")).lower()
                    row_exp = str(row.get(exp_col, "")).lower()

                    # --- DATA ENRICHMENT (Infer missing values) ---
                    # 1. Infer Engine CC
                    row_cc = 0
                    try:
                        e_str = str(row.get(engine_col, 0))
                        match = re.search(r"(\d+)", e_str)
                        if match: row_cc = int(match.group(1))
                    except: row_cc = 0

                    # 2. Infer Usage if missing
                    if not row_usage or row_usage == "nan":
                        if "cruiser" in row_cat or "tour" in row_cat: row_usage = "long rides touring"
                        elif "adv" in row_cat or "off-road" in row_cat: row_usage = "off-road adventure"
                        elif "sport" in row_cat: row_usage = "performance track"
                        elif "commuter" in row_cat or "scooter" in row_type: row_usage = "daily commute city"
                        else: row_usage = "daily commute" # Default

                    # 3. Infer Experience if missing
                    if not row_exp or row_exp == "nan":
                        if row_cc > 600: row_exp = "expert"
                        elif row_cc > 250: row_exp = "intermediate"
                        else: row_exp = "beginner"

                    # 4. Infer Comfort if missing
                    if not row_comfort or row_comfort == "nan":
                       if "cruiser" in row_cat or "scooter" in row_type or "adv" in row_cat: row_comfort = "high comfort"
                       elif "sport" in row_cat: row_comfort = "aggressive"
                       else: row_comfort = "medium"

                    # --- 1. Vehicle Type (Critical) ---
                    # ALWAYS STRICT: Never show a Scooter when asking for a Bike (and vice-versa)
                    is_scooter_req = "scooter" in vehicle_type
                    is_scooter_row = "scooter" in row_type or "scooter" in row_cat
                    
                    if vehicle_type:
                        if is_scooter_req != is_scooter_row: continue # Reject mismatch ALWAYS

                    # --- 2. Category Filter ---
                    if category_pref:
                        cat_req = category_pref.lower()
                        is_match = (cat_req in row_cat) or (cat_req in row_type)
                        if "sport" in cat_req and "racing" in row_cat: is_match = True
                        if "adventure" in cat_req and ("off-road" in row_usage or "scrambler" in row_cat): is_match = True

                        if not is_match:
                             if strict_mode: continue 
                             else: score -= 45 # Heavy Relaxed penalty for category in fallback
                        else: score += 15

                    # --- 3. Usage Filter ---
                    if usage:
                        if "adventure" in usage or "off-road" in usage:
                            is_adv_bike = "adv" in row_cat or "adventure" in row_cat or "scrambler" in row_cat or "off-road" in row_usage
                            if not is_adv_bike:
                                 if strict_mode: continue
                                 else: score -= 45 # Heavy penalty

                    # --- 4. Budget ---
                    # max 25 points
                    if budget > 0 and price_val > 0:
                        if price_val <= budget: score += 25
                        elif price_val <= (budget * 1.15): score += 15
                        elif price_val <= (budget * 1.25): score += 5
                        else: score -= 15

                    # --- 5. Experience / CC ---
                    # max 15 points
                    row_cc = 0
                    try:
                        e_str = str(row.get(engine_col, 0))
                        match = re.search(r"(\d+)", e_str)
                        if match: row_cc = int(match.group(1))
                    except: row_cc = 0

                    if experience:
                        if "beginner" in experience:
                            if row_cc <= 200: score += 15
                            elif row_cc <= 350: score += 5
                            else: score -= 20 
                        elif "intermediate" in experience:
                            if 150 <= row_cc <= 650: score += 15
                            else: score += 5 
                        elif "expert" in experience:
                            if row_cc >= 400: score += 15
                            elif row_cc >= 250: score += 5

                    # --- 6. Mileage ---
                    # max 15 points
                    row_mileage = 0
                    try:
                        m_str = str(row.get(mileage_col, 0))
                        match = re.search(r"(\d+(\.\d+)?)", m_str)
                        if match: row_mileage = float(match.group(1))
                    except: row_mileage = 0

                    if fuel_priority:
                        if "high" in fuel_priority:
                            if row_mileage >= 55: score += 15
                            elif row_mileage >= 45: score += 10
                            elif row_mileage < 30: score -= 10
                        elif "medium" in fuel_priority:
                            if row_mileage >= 35: score += 15
                        elif "low" in fuel_priority:
                            if row_mileage < 40: score += 15 
                            else: score += 10
                    
                    # --- 7. Usage / Comfort ---
                    # max 10 points for usage, max 10 points for comfort
                    if usage:
                        if "daily" in usage or "commute" in usage:
                            if "commuter" in row_cat or "scooter" in row_type or row_mileage > 45: score += 10
                        elif "tour" in usage or "long" in usage:
                            if "cruiser" in row_cat or "adv" in row_cat or "tour" in row_cat: score += 10
                            elif row_cc > 200: score += 5
                        elif "track" in usage or "race" in usage:
                            if "sport" in row_cat: score += 10
                            elif row_cc > 200: score += 5

                    if comfort:
                        if "high" in comfort:
                            if "high" in row_comfort: score += 10
                            elif "medium" in row_comfort: score += 5
                            else: score -= 5
                        elif "medium" in comfort:
                            if "medium" in row_comfort or "high" in row_comfort: score += 10
                        elif "low" in comfort or "aggressive" in comfort:
                            if "aggressive" in row_comfort: score += 10
                            else: score += 5
                            
                    # --- 8. Ride With ---
                    # max 5 points
                    if ride_with and "family" in ride_with:
                        if "split seat" not in str(row.get("seat_type", "")).lower(): score += 5
                        elif "scooter" in row_type or "cruiser" in row_cat: score += 5
                    elif ride_with and ("solo" in ride_with or "friends" in ride_with):
                        score += 5
                    else:
                        score += 5 # Default if valid but unhandled input
                    
                    # --- 9. Distance KM ---
                    # max 5 points
                    if distance_km > 50:
                         if row_cc > 150 or "liquid" in str(row.get("cooling", "")).lower(): score += 5
                         elif row_mileage > 40: score += 2
                    else: 
                         score += 5 

                    # Normalize
                    if score < 0: score = 0
                    if score > 100: score = 100
                    
                    if strict_mode:
                        if score < 50: continue # Min threshold for strict matches
                    else:
                        if score < 20: continue # Relaxed threshold so fallback bikes still show up

                    bike_name = str(row.get(model_col, ""))
                    details_row = None
                    search_model_col = cols_search['model']
                    if search_model_col:
                        low_name = bike_name.lower().strip()
                        matches = df_search[df_search[search_model_col].astype(str).str.lower().str.contains(low_name, regex=False)]
                        if not matches.empty:
                            details_row = matches.iloc[0]

                    if details_row is not None:
                        entry = build_bike_entry(details_row, cols_search, user_favs, request)
                    else:
                        # Improved Fallback Entry with Image Resolution
                        e_disp = f"{row_cc} cc" if row_cc > 0 else "N/A"
                        img_url = resolve_bike_image_url(bike_name, request)
                        
                        entry = {
                            "name": bike_name,
                            "price": price_val,
                            "imageUrl": img_url,
                            "matchPercent": int(score),
                            "engine": e_disp,
                            "mileage": f"{int(row_mileage)} kmpl" if row_mileage > 0 else "N/A",
                            "isFavorite": bike_name in user_favs
                        }
                    
                    entry['matchPercent'] = int(score)
                    entry['usage'] = "AI Recommended"
                    local_results.append(entry)
                return local_results

            # Attempt 1: Strict Match
            results = get_matches(strict_mode=True)
            
            # Attempt 2: Relaxed Match (Fallback)
            if len(results) < 3: 
                 results = get_matches(strict_mode=False)

            results.sort(key=lambda x: x.get('matchPercent', 0), reverse=True)
            return Response(results[:20], status=status.HTTP_200_OK)

        except Exception as e:
            return Response({"error": str(e)}, status=500)






BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
EXCEL_PATH = os.path.join(os.path.dirname(__file__), "search_bikes.xlsx")


class SearchBikeAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        q = request.GET.get("q", "").lower()

        # Load Excel
        df = pd.read_excel(EXCEL_PATH, header=1)

        # Normalize columns
        df.columns = df.columns.astype(str).str.lower().str.strip()

        # Replace NaN with empty string
        df = df.replace({np.nan: ""})

        cols = get_all_columns(df)
        cols = get_all_columns(df)
        model_col = cols['model']
        brand_col = cols['brand']
        type_col = cols['type']
        fuel_col = cols['fuel_type']

        # User favs
        user_favs = set()
        if request.user.is_authenticated:
             user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

        if not model_col:
             return Response(
                 {"error": "Model column not found", "columns": df.columns.tolist()},
                 status=500
             )

        # Filter logic
        result_df = df[
            df[model_col].astype(str).str.contains(q, case=False) |
            (df[brand_col].astype(str).str.contains(q, case=False) if brand_col else False) |
            (df[type_col].astype(str).str.contains(q, case=False) if type_col else False) |
            (df[fuel_col].astype(str).str.contains(q, case=False) if fuel_col else False)
        ]

        mapped_results = []
        for _, row in result_df.iterrows():
             # Filter out junk rows (headers etc) by checking price
             price_val = parse_price(row.get(cols['price'], 0))
             if price_val <= 0: continue

             mapped_results.append(build_bike_entry(row, cols, user_favs, request))

        return Response(mapped_results)


class FavouriteBikeAPIView(APIView):
    permission_classes = [IsAuthenticated]

    # ❤️ GET – list favourites
    def get(self, request):
        favs = FavouriteBike.objects.filter(user=request.user)
        serializer = FavouriteBikeSerializer(favs, many=True)
        return Response(serializer.data)

    # ❤️ POST – add to favourites
    def post(self, request):
        data = request.data
        data["user"] = request.user.id

        serializer = FavouriteBikeSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            return Response({"message": "Added to favourites"})
        return Response(serializer.errors, status=400)

    # ❌ DELETE – remove from favourites
    def delete(self, request):
        bike_id = request.data.get("bike_id")
        FavouriteBike.objects.filter(
            user=request.user,
            bike_id=bike_id
        ).delete()
        return Response({"message": "Removed from favourites"})



class BikeReviewAPIView(APIView):
    permission_classes = [IsAuthenticated]

    # 🔹 Get all reviews for a bike
    def get(self, request):
        bike_id = request.GET.get("bike_id")

        if not bike_id:
            return Response(
                {"error": "bike_id required"},
                status=status.HTTP_400_BAD_REQUEST
            )

        reviews = BikeReview.objects.filter(bike_id=bike_id)
        serializer = BikeReviewSerializer(reviews, many=True)
        return Response(serializer.data)

    # 🔹 Post new review
    def post(self, request):
        data = request.data.copy()
        data["user"] = request.user.id

        serializer = BikeReviewSerializer(data=data)
        if serializer.is_valid():
            serializer.save(user=request.user)
            return Response(
                {"message": "Review added successfully"},
                status=status.HTTP_201_CREATED
            )

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)



class AppReviewAPIView(APIView):
    permission_classes = [IsAuthenticatedOrReadOnly]

    def get(self, request):
        reviews = AppReview.objects.all().order_by("-created_at")
        serializer = AppReviewSerializer(reviews, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = AppReviewSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save(user=request.user)
            return Response(
                {"message": "Review submitted successfully"},
                status=status.HTTP_201_CREATED
            )
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)



BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


class BikeCompareAPIView(APIView):
    permission_classes = [AllowAny]
    def post(self, request):

        bike_names = request.data.get("bikes", [])   # ✅ THIS WAS MISSING

        if len(bike_names) < 2:
            return Response(
                {"error": "Select at least 2 bikes"},
                status=status.HTTP_400_BAD_REQUEST
            )

        if len(bike_names) > 3:
            return Response(
                {"error": "Maximum 3 bikes allowed"},
                status=status.HTTP_400_BAD_REQUEST
            )

        df = pd.read_excel(EXCEL_PATH, header=1)

        df.columns = df.columns.str.lower().str.strip()
        df = df.replace({np.nan: ""})

        if "model" not in df.columns:
            return Response(
                {"error": "Model column not found", "columns": list(df.columns)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )

        df["model"] = df["model"].astype(str).str.lower().str.strip()

        results = []

        for name in bike_names:
            match = df[df["model"].str.contains(name.lower(), na=False)]
            if not match.empty:
                row = match.iloc[0].replace({np.nan: ""})
                results.append(row.to_dict())

        if len(results) < 2:
            return Response(
                {"error": "Invalid bike selection"},
                status=status.HTTP_404_NOT_FOUND
            )

        return Response({
            "count": len(results),
            "comparison": results
        })


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")

class AllBikesAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        category = request.GET.get("category", "").lower()

        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            category_col = cols['category']

            # optional filter
            if category and category != "all":
                if category_col:
                    df = df[df[category_col].astype(str).str.lower().str.contains(category, na=False)]

            # Fetch user favorites if authenticated
            user_favs = set()
            if request.user.is_authenticated:
                user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []
            for _, row in df.iterrows():
                # Filter out junk rows rule: must have valid price
                price_val = parse_price(row.get(cols['price'], 0))
                if price_val <= 0: continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")

class Below100ccBikesAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            engine_col = cols['engine']
            
            user_favs = get_user_favs(request) # Helper

            bikes = []
            for _, row in df.iterrows():
                # Check displacement < 100
                if engine_col:
                    disp_text = str(row.get(engine_col, "")).lower()
                    match = re.search(r"(\d+)", disp_text)
                    if match:
                        cc = int(match.group(1))
                        if cc >= 100: continue
                    else:
                        continue 
                else:
                    continue 
                
                # Filter out junk
                price_val = parse_price(row.get(cols['price'], 0))
                if price_val <= 0: continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )

class Bikes100to150ccAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})
            cols = get_all_columns(df)
            engine_col = cols['engine']
            user_favs = get_user_favs(request)
            bikes = []
            for _, row in df.iterrows():
                if engine_col:
                    disp_text = str(row.get(engine_col, "")).lower()
                    match = re.search(r"(\d+)", disp_text)
                    if match:
                        cc = int(match.group(1))
                        if not (100 <= cc <= 150): continue
                    else: continue
                else: continue

                # Filter out junk
                price_val = parse_price(row.get(cols['price'], 0))
                if price_val <= 0: continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))
            return Response(bikes, status=status.HTTP_200_OK)
        except Exception as e:
            return Response({"error": str(e)}, status=500)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


class Bikes150to200ccAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            engine_col = cols['engine']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []

            for _, row in df.iterrows():
                # Displacement Logic
                if engine_col:
                    disp_text = str(row.get(engine_col, "")).lower()
                    # extract number, handle decimal like 199.5
                    match = re.search(r"(\d+(\.\d+)?)", disp_text)
                    if match:
                        cc = float(match.group(1))
                        # 150 to 200 check
                        if not (150 <= cc <= 200):
                            continue
                    else:
                        continue
                else:
                    continue

                # Filter out junk
                price_val = parse_price(row.get(cols['price'], 0))
                if price_val <= 0: continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )




class Bikes200to350ccAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            engine_col = cols['engine']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []

            for _, row in df.iterrows():
                # Displacement Logic
                if engine_col:
                    disp_text = str(row.get(engine_col, "")).lower()
                    match = re.search(r"(\d+(\.\d+)?)", disp_text)
                    if match:
                        cc = float(match.group(1))
                        # 200 to 350 check
                        if not (200 <= cc <= 350):
                            continue
                    else:
                        continue
                else:
                    continue

                # Filter out junk
                price_val = parse_price(row.get(cols['price'], 0))
                if price_val <= 0: continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )



class Bikes350to500ccAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            engine_col = cols['engine']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []

            for _, row in df.iterrows():
                # Displacement Logic
                if engine_col:
                    disp_text = str(row.get(engine_col, "")).lower()
                    match = re.search(r"(\d+(\.\d+)?)", disp_text)
                    if match:
                        cc = float(match.group(1))
                        # 350 to 500 check
                        if not (350 <= cc <= 500):
                            continue
                    else:
                        continue
                else:
                    continue

                # Filter out junk
                price_val = parse_price(row.get(cols['price'], 0))
                if price_val <= 0: continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )



BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")

class Bikes500to750ccAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            engine_col = cols['engine']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []

            for _, row in df.iterrows():
                # Displacement Logic
                if engine_col:
                    disp_text = str(row.get(engine_col, "")).lower()
                    match = re.search(r"(\d+(\.\d+)?)", disp_text)
                    if match:
                        cc = float(match.group(1))
                        # 500 to 750 check
                        if not (500 <= cc <= 750):
                            continue
                    else:
                        continue
                else:
                    continue

                # Filter out junk
                price_val = parse_price(row.get(cols['price'], 0))
                if price_val <= 0: continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )


class Bikes750to1000ccAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            engine_col = cols['engine']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []

            for _, row in df.iterrows():
                # Displacement Logic
                if engine_col:
                    disp_text = str(row.get(engine_col, "")).lower()
                    match = re.search(r"(\d+(\.\d+)?)", disp_text)
                    if match:
                        cc = float(match.group(1))
                        # 750 to 1000 check
                        if not (750 <= cc <= 1000):
                            continue
                    else:
                        continue
                else:
                    continue

                # Filter out junk
                price_val = parse_price(row.get(cols['price'], 0))
                if price_val <= 0: continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )



class BikesAbove1000ccAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            engine_col = cols['engine']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []

            for _, row in df.iterrows():
                # Displacement Logic
                if engine_col:
                    disp_text = str(row.get(engine_col, "")).replace(",", "").lower()
                    match = re.search(r"(\d+(\.\d+)?)", disp_text)
                    if match:
                        cc = float(match.group(1))
                        # 1000cc & Above check (inclusive)
                        if not (cc >= 1000):
                            continue
                    else:
                        continue
                else:
                    continue

                # Filter out junk
                price_val = parse_price(row.get(cols['price'], 0))
                if price_val <= 0: continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )

class Bikes30kto80kAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            price_col = cols['price']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []

            for _, row in df.iterrows():
                # Price parsing
                raw_price = row.get(price_col, 0)
                price_val = 0
                try:
                    raw_str = str(raw_price).lower().strip()
                    if "lakh" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 100000)
                    elif "cr" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 10000000)
                    else:
                        clean_price = re.sub(r'[^\d\.]', '', raw_str)
                        if clean_price: price_val = int(float(clean_price))
                except: price_val = 0

                # 30k - 80k Filter
                if not (30000 <= price_val <= 80000):
                    continue
                
                # Filter out junk
                if price_val <= 0: continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )

class Bikes80kto150kAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            price_col = cols['price']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []

            for _, row in df.iterrows():
                # Price parsing
                raw_price = row.get(price_col, 0)
                price_val = 0
                try:
                    raw_str = str(raw_price).lower().strip()
                    if "lakh" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 100000)
                    elif "cr" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 10000000)
                    else:
                        clean_price = re.sub(r'[^\d\.]', '', raw_str)
                        if clean_price: price_val = int(float(clean_price))
                except: price_val = 0

                # 80k - 1.5 Lakh Filter
                if not (80000 <= price_val <= 150000):
                    continue
                
                # Filter out junk
                if price_val <= 0: continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )

class Bikes150kto300kAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            price_col = cols['price']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []

            for _, row in df.iterrows():
                # Price parsing
                raw_price = row.get(price_col, 0)
                price_val = 0
                try:
                    raw_str = str(raw_price).lower().strip()
                    if "lakh" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 100000)
                    elif "cr" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 10000000)
                    else:
                        clean_price = re.sub(r'[^\d\.]', '', raw_str)
                        if clean_price: price_val = int(float(clean_price))
                except: price_val = 0

                # 1.5 Lakh - 3 Lakh Filter
                if not (150000 <= price_val <= 300000):
                    continue
                
                # Filter out junk
                if price_val <= 0: continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )

class Bikes300kto500kAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            price_col = cols['price']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []

            for _, row in df.iterrows():
                # Price parsing
                raw_price = row.get(price_col, 0)
                price_val = 0
                try:
                    raw_str = str(raw_price).lower().strip()
                    if "lakh" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 100000)
                    elif "cr" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 10000000)
                    else:
                        clean_price = re.sub(r'[^\d\.]', '', raw_str)
                        if clean_price: price_val = int(float(clean_price))
                except: price_val = 0

                # 3 Lakh - 5 Lakh Filter
                if not (300000 <= price_val <= 500000):
                    continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )

class Bikes5Lto10LAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            price_col = cols['price']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []

            for _, row in df.iterrows():
                # Price parsing
                raw_price = row.get(price_col, 0)
                price_val = 0
                try:
                    raw_str = str(raw_price).lower().strip()
                    if "lakh" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 100000)
                    elif "cr" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 10000000)
                    else:
                        clean_price = re.sub(r'[^\d\.]', '', raw_str)
                        if clean_price: price_val = int(float(clean_price))
                except: price_val = 0

                # 5 Lakh - 10 Lakh Filter
                if not (500000 <= price_val <= 1000000):
                    continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )

class Bikes10Lto30LAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            price_col = cols['price']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []

            for _, row in df.iterrows():
                # Price parsing
                raw_price = row.get(price_col, 0)
                price_val = 0
                try:
                    raw_str = str(raw_price).lower().strip()
                    if "lakh" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 100000)
                    elif "cr" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 10000000)
                    else:
                        clean_price = re.sub(r'[^\d\.]', '', raw_str)
                        if clean_price: price_val = int(float(clean_price))
                except: price_val = 0

                # 10 Lakh - 30 Lakh Filter
                if not (1000000 <= price_val <= 3000000):
                    continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )

class BikesAbove30LAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            price_col = cols['price']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            bikes = []

            for _, row in df.iterrows():
                # Price parsing
                raw_price = row.get(price_col, 0)
                price_val = 0
                try:
                    raw_str = str(raw_price).lower().strip()
                    if "lakh" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 100000)
                    elif "cr" in raw_str:
                        num = re.findall(r"[\d\.]+", raw_str)
                        if num: price_val = int(float(num[0]) * 10000000)
                    else:
                        clean_price = re.sub(r'[^\d\.]', '', raw_str)
                        if clean_price: price_val = int(float(clean_price))
                except: price_val = 0

                # Above 30 Lakh Filter
                if not (price_val > 3000000):
                    continue

                bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )



def extract_Price(value):
    """
    Converts price text like:
    '₹1.93 Lakh', '1,93,000', '193000'
    → integer rupees
    """
    if not value:
        return 0

    text = str(value).lower().replace(",", "").replace("₹", "").strip()

    if "lakh" in text:
        num = re.findall(r"[\d.]+", text)
        return int(float(num[0]) * 100000) if num else 0

    if "crore" in text:
        num = re.findall(r"[\d.]+", text)
        return int(float(num[0]) * 10000000) if num else 0

    digits = re.findall(r"\d+", text)
    return int(digits[0]) if digits else 0


class BikesByBudgetAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        min_Price = int(request.GET.get("min", 0))
        max_Price = request.GET.get("max")

        max_Price = int(max_Price) if max_Price else None

        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            if "price" not in df.columns:
                return Response(
                    {"error": "price column not found"},
                    status=status.HTTP_500_INTERNAL_SERVER_ERROR
                )

            bikes = []

            for _, row in df.iterrows():
                Price_value = extract_Price(row.get("Price", ""))
                if Price_value == 0:
                    continue

                if Price_value < min_Price:
                    continue

                if max_Price and Price_value > max_Price:
                    continue

                bikes.append({
                    "id": row.get("id", ""),
                    "name": row.get("model", ""),
                    "engine": row.get("displacement", ""),
                    "mileage": row.get("mileage", ""),
                    "Price": Price_value,
                    "imageUrl": row.get("image", ""),
                    "badge": row.get("badge", ""),
                    "vehicleType": "Motorcycle",
                    "usage": row.get("category", "")
                })

            return Response(bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


def normalize(text):
    return str(text).lower().strip()


class SportsBikesAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # normalize columns
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            type_col = cols['type']
            category_col = cols['category']
            usage_col = cols['usage']
            model_col = cols['model']

            # User favs
            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            sports_bikes = []

            for _, row in df.iterrows():
                # Helper for safe string
                def safe_str(val):
                    return str(val).lower().strip() if pd.notna(val) else ""

                bike_type = safe_str(row.get(type_col, ""))
                category = safe_str(row.get(category_col, ""))
                usage = safe_str(row.get(usage_col, ""))
                model_name = row.get(model_col, "")
                
                # Check for sports keywords
                is_sport = False
                keywords = ["sport", "racing", "superbike", "track"]
                
                if any(k in bike_type for k in keywords): is_sport = True
                if any(k in category for k in keywords): is_sport = True
                if any(k in usage for k in keywords): is_sport = True
                if "r15" in str(model_name).lower(): is_sport = True 

                if not is_sport:
                    continue

                sports_bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(sports_bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


def norm(val):
    return str(val).lower().strip()


class ScooterBikesAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)
            
            # 🔹 Normalize columns for better matching
            df.columns = df.columns.astype(str).str.lower().str.strip()
            df = df.replace({np.nan: ""})
            
            cols = get_all_columns(df)
            type_col = cols['type']
            category_col = cols['category'] 
            fuel_col = cols['fuel_type']
            usage_col = cols['usage']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            scooters = []

            for _, row in df.iterrows():
                # Helper for safe string
                def safe_str(val):
                    return str(val).lower().strip() if pd.notna(val) else ""

                bike_type = safe_str(row.get(type_col, ""))
                category = safe_str(row.get(category_col, ""))
                usage = safe_str(row.get(usage_col, ""))
                fuel = safe_str(row.get(fuel_col, ""))

                if (
                    "scooter" in bike_type
                    or "scooter" in category
                    or "scooter" in usage
                    or "electric" in fuel
                ):
                    # Filter out junk
                    price_val = parse_price(row.get(cols['price'], 0))
                    if price_val <= 0: continue
                    scooters.append(build_bike_entry(row, cols, user_favs, request))

            return Response(scooters, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


def norm(val):
    return str(val).lower().strip()


class CruiserBikesAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)
            
            # 🔹 Normalize columns
            df.columns = df.columns.astype(str).str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            type_col = cols['type']
            category_col = cols['category']
            usage_col = cols['usage']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            cruisers = []

            for _, row in df.iterrows():
                # Helper for safe string
                def safe_str(val):
                    return str(val).lower().strip() if pd.notna(val) else ""

                bike_type = safe_str(row.get(type_col, ""))
                category = safe_str(row.get(category_col, ""))
                usage = safe_str(row.get(usage_col, ""))
                model_name = str(row.get(cols['model'], "")).lower().strip()

                # Robust Cruiser Keywords
                cruiser_keywords = [
                    "cruiser", "cruise", 
                    "avenger", "meteor", "bullet", "classic", "hunter", 
                    "jawa", "yezdi", "ronin", "highness", "cb350", 
                    "maverick", "imperiale", "vulcan", "gold wing", 
                    "shotgun", "eliminator", "intruder"
                ]

                is_cruiser = False
                
                # Check explicit structure
                if "cruiser" in bike_type or "cruiser" in category or "cruise" in usage:
                    is_cruiser = True
                
                # Check model name for known cruisers
                if any(k in model_name for k in cruiser_keywords):
                    is_cruiser = True

                if is_cruiser:
                    # Filter out junk
                    price_val = parse_price(row.get(cols['price'], 0))
                    if price_val <= 0: continue
                    cruisers.append(build_bike_entry(row, cols, user_favs, request))

            return Response(cruisers, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


def norm(val):
    return str(val).lower().strip()


class CommuterBikesAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)
            
            # 🔹 Normalize columns
            df.columns = df.columns.astype(str).str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            type_col = cols['type']
            category_col = cols['category']
            usage_col = cols['usage']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            commuters = []

            for _, row in df.iterrows():
                # Helper for safe string
                def safe_str(val):
                    return str(val).lower().strip() if pd.notna(val) else ""

                bike_type = safe_str(row.get(type_col, ""))
                category = safe_str(row.get(category_col, ""))
                usage = safe_str(row.get(usage_col, ""))

                model_name = str(row.get(cols['model'], "")).lower().strip()

                # Robust Commuter Keywords
                commuter_keywords = [
                    "splendor", "passion", "hf deluxe", "glamour", "super splendor",
                    "shine", "sp 125", "livo", "cd 110", "dream", "unicorn",
                    "platina", "ct 100", "ct 110", "ct 125", 
                    "pulsar 125", "pulsar 150",
                    "radeon", "star city", "victor", "tvs sport",
                    "saluto", "sz-rr", "rx 100"
                ]

                is_commuter = False

                if (
                    "commuter" in bike_type
                    or "commuter" in category
                    or "daily" in usage
                    or "commute" in usage
                ):
                    is_commuter = True
                
                # Check model keywords
                if any(k in model_name for k in commuter_keywords):
                    is_commuter = True
                    
                # Exclude high-end "Sport" bikes if caught by generic logic
                if "tiger" in model_name or "ducati" in model_name:
                    is_commuter = False

                if is_commuter:
                    # Filter out junk
                    price_val = parse_price(row.get(cols['price'], 0))
                    if price_val <= 0: continue
                    commuters.append(build_bike_entry(row, cols, user_favs, request))

            return Response(commuters, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


def norm(val):
    return str(val).lower().strip()


class StreetBikesAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)
            
            # 🔹 Normalize columns
            df.columns = df.columns.astype(str).str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            type_col = cols['type']
            category_col = cols['category']
            usage_col = cols['usage']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            street_bikes = []

            for _, row in df.iterrows():
                # Helper for safe string
                def safe_str(val):
                    return str(val).lower().strip() if pd.notna(val) else ""

                bike_type = safe_str(row.get(type_col, ""))
                category = safe_str(row.get(category_col, ""))
                usage = safe_str(row.get(usage_col, ""))

                model_name = str(row.get(cols['model'], "")).lower().strip()

                # Robust Street Keywords
                street_keywords = [
                    "street", "naked",
                    "duke", "mt-15", "mt 15", "mt-03", "mt 03", "mt-09", 
                    "fz", "fz-s", "fzs", "gixxer", 
                    "apache", "rtr", "rr 310",
                    "pulsar ns", "pulsar n250", "pulsar n160", "pulsar n150",
                    "dominar", "hornet", "xblade", "xtreme", 
                    "z650", "z900", "z H2", "monster", 
                    "speed 400", "scrambler", "streetfighter", "ronin",
                    "cb300", "cb300r", "cb300f", "cb650r", "cb1000r",
                    "bmw g 310 r", "f 900 r", "s 1000 r"
                ]

                # Explicit Exclusions (Cruisers, Scooters, etc with 'Street' in name)
                exclusions = [
                    "scooter", "burgman", "electric", # Scooters
                    "street 750", "street rod", "street glide", "street bob", # Cruisers
                    "avenger", "intruder", "vulcan"
                ]

                is_street = False

                # 1. Broad Check (Type/Category)
                if (
                    "street" in bike_type or "naked" in bike_type or
                    "street" in category or "naked" in category or "fighter" in category
                ):
                    is_street = True
                
                # 2. Specific Model Check
                if any(k in model_name for k in street_keywords):
                    is_street = True
                    
                # 3. SAFETY EXCLUSIONS
                if any(ex in model_name for ex in exclusions) or any(ex in bike_type for ex in exclusions):
                    is_street = False

                if is_street:
                    # Filter out junk
                    price_val = parse_price(row.get(cols['price'], 0))
                    if price_val <= 0: continue
                    street_bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(street_bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


def norm(val):
    return str(val).lower().strip()


def extract_cc(text):
    match = re.search(r"(\d+)", str(text))
    return int(match.group(1)) if match else 0


class SuperBikesAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)
            df.columns = df.columns.str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            type_col = cols['type']
            category_col = cols['category']
            usage_col = cols['usage']
            engine_col = cols['engine']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            super_bikes = []

            for _, row in df.iterrows():
                # Helper for safe string
                def safe_str(val):
                    return str(val).lower().strip() if pd.notna(val) else ""

                bike_type = safe_str(row.get(type_col, ""))
                category = safe_str(row.get(category_col, ""))
                usage = safe_str(row.get(usage_col, ""))
                displacement_str = safe_str(row.get(engine_col, ""))
                
                # Extract cc
                try:
                    cc_val = float(re.findall(r"[\d\.]+", displacement_str)[0])
                except:
                    cc_val = 0

                if (
                    "super" in bike_type
                    or "superbike" in bike_type
                    or "hyper" in bike_type
                    or "super" in category
                    or "race" in category
                    or "track" in usage
                    or cc_val >= 750
                ):
                    super_bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(super_bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


def norm(val):
    return str(val).lower().strip()


class ScramblerBikesAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)
            
            # 🔹 Normalize columns
            df.columns = df.columns.astype(str).str.lower().str.strip()
            df = df.replace({np.nan: ""})
            
            cols = get_all_columns(df)
            type_col = cols['type']
            category_col = cols['category']
            usage_col = cols['usage']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            scramblers = []

            for _, row in df.iterrows():
                # Helper for safe string
                def safe_str(val):
                    return str(val).lower().strip() if pd.notna(val) else ""

                bike_type = safe_str(row.get(type_col, ""))
                category = safe_str(row.get(category_col, ""))
                usage = safe_str(row.get(usage_col, ""))

                model_name = str(row.get(cols['model'], "")).lower().strip()

                # Strict Scrambler Keywords (No pure Adventure bikes)
                scrambler_keywords = [
                    "scrambler", "desert sled",
                    "scram 411", "yezdi scrambler",
                    "ronin", "hunter", 
                    "svartpilen", "caballero",
                    "cl-x" 
                ]
                # Note: Himalayan 450, XPulse, V-Strom, Tiger etc are excluded (they go to Adventure view)

                is_scrambler = False

                if (
                    "scrambler" in bike_type
                    or "scrambler" in category
                    or "scrambler" in usage
                ):
                    is_scrambler = True
                
                # Check model keywords
                if any(k in model_name for k in scrambler_keywords):
                    is_scrambler = True
                    
                # Explicit Safety: Ensure 'Adventure' models don't sneak in via generic 'scrambler' category if they aren't named scrambler
                # (e.g. Yezdi Adventure should not appear here)
                if "adventure" in model_name and "scrambler" not in model_name:
                    is_scrambler = False

                if is_scrambler:
                    # Filter out junk
                    price_val = parse_price(row.get(cols['price'], 0))
                    if price_val <= 0: continue
                    scramblers.append(build_bike_entry(row, cols, user_favs, request))

            return Response(scramblers, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


def normalize(val):
    return str(val).lower().strip()


class AdventureBikesAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)

            # 🔹 Normalize columns
            df.columns = df.columns.astype(str).str.lower().str.strip()
            df = df.replace({np.nan: ""})
            
            cols = get_all_columns(df)
            type_col = cols['type'] # bike type
            category_col = cols['category']
            usage_col = cols['usage']
            
            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            adventure_bikes = []

            for _, row in df.iterrows():
                category = normalize(row.get(category_col, ""))
                usage = normalize(row.get(usage_col, ""))
                bike_type = normalize(row.get(type_col, ""))
                
                model_name = str(row.get(cols['model'], "")).lower().strip()

                # Strict Adventure/Dual-Sport Keywords
                adv_keywords = [
                    "himalayan", "xpulse", 
                    "yezdi adventure", "v-strom", "v strom", "versys",
                    "gs 310", "g 310 gs", "r 1250 gs", "r 1300 gs",
                    "390 adventure", "250 adventure", "390 adventure x",
                    "tiger", "africa twin", "multistrada", "desert x",
                    "pan america", "transalp", "nx500", "tenere", "tuareg"
                ]
                
                # Exclude Scramblers from Adventure view
                scrambler_exclusions = [
                    "scrambler", "scram 411", "hunter", "ronin", "desert sled"
                ]

                is_adv = False

                if (
                    "adventure" in category
                    or "adventure" in usage
                    or "touring" in usage
                    or "off-road" in usage
                    or "adv" in bike_type
                    or "dual" in category
                ):
                    is_adv = True
                
                # Check model keywords
                if any(k in model_name for k in adv_keywords):
                    is_adv = True
                    
                # Explicit Safety: Remove Scramblers
                if any(ex in model_name for ex in scrambler_exclusions):
                     is_adv = False

                if is_adv:
                    # Filter out junk
                    price_val = parse_price(row.get(cols['price'], 0))
                    if price_val <= 0: continue
                    adventure_bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(adventure_bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


def norm(val):
    return str(val).lower().strip()


class TourerBikesAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        try:
            df = pd.read_excel(EXCEL_PATH, header=1)
            
            # 🔹 Normalize columns
            df.columns = df.columns.astype(str).str.lower().str.strip()
            df = df.replace({np.nan: ""})

            cols = get_all_columns(df)
            type_col = cols['type']
            category_col = cols['category']
            usage_col = cols['usage']

            user_favs = set()
            if request.user.is_authenticated:
                 user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

            tourer_bikes = []

            for _, row in df.iterrows():
                category = norm(row.get(category_col, ""))
                usage = norm(row.get(usage_col, ""))
                bike_type = norm(row.get(type_col, ""))

                model_name = str(row.get(cols['model'], "")).lower().strip()

                # Strict Tourer Keywords (Grand Tourers, Sports Tourers, Power Cruisers)
                tourer_keywords = [
                    "gold wing", "goldwing",
                    "k 1600", "r 1250 rt", "r 1250 rs", "k 1600 gtl", "k 1600 b",
                    "hayabusa", "ninja 1000", "ninja h2 sx", "versys 1000", "versys 650",
                    "super meteor", "dominar 400", "dominar 250",
                    "diavel", "xdiavel", "rocket 3", 
                    "road glide", "street glide", "road king", "heritage classic",
                    "m 1000 xr", "s 1000 xr",
                    "tracer", "turismo veloce", "fjr1300", "concours"
                ]
                
                # Exclude strictly off-road/ADV concentrated bikes (that are in Adventure view)
                # Note: Versys is debatable but often considered Sport Tourer so kept in both or just Tourer.
                # Here we exclude pure ADVs if they slip in.
                adv_exclusions = [
                    "himalayan", "xpulse", "tenere", "tuareg", "off-road", "rally"
                ]

                is_tourer = False

                if (
                    "tourer" in category
                    or "touring" in category
                    or "bagger" in category
                    or "grand tourer" in category
                ):
                    is_tourer = True
                
                # Check model keywords
                if any(k in model_name for k in tourer_keywords):
                    is_tourer = True
                    
                # Explicit Safety: Remove pure ADVs
                if any(ex in model_name for ex in adv_exclusions):
                     is_tourer = False
                     
                if is_tourer:
                    # Filter out junk
                    price_val = parse_price(row.get(cols['price'], 0))
                    if price_val <= 0: continue
                    tourer_bikes.append(build_bike_entry(row, cols, user_favs, request))

            return Response(tourer_bikes, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {"error": str(e)},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )




BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


class BikesByBrandAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        brand = request.GET.get("brand")

        if not brand:
            return Response({"error": "brand query param required"}, status=400)

        # Read excel
        df = pd.read_excel(EXCEL_PATH, header=1)

        # 🔑 Normalize column names
        df.columns = df.columns.astype(str).str.lower().str.strip()
        df = df.replace({np.nan: ""})

        cols = get_all_columns(df)
        brand_col = cols['brand']

        if not brand_col:
             return Response({
                 "error": "brand column not found"
             }, status=500)

        user_favs = set()
        if request.user.is_authenticated:
             user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))
             
        bikes = []
        target = brand.lower().strip()
        
        for _, row in df.iterrows():
            curr_brand = str(row.get(brand_col, "")).lower().strip()
            if curr_brand == target:
                bikes.append(build_bike_entry(row, cols, user_favs, request))

        return Response(bikes, status=status.HTTP_200_OK)


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


class BikeDetailsAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        model_query = request.GET.get("model")

        if not model_query:
            return Response({"error": "model query param required"}, status=400)

        df = pd.read_excel(EXCEL_PATH, header=1)

        # Use get_all_columns to be consistent
        df.columns = df.columns.astype(str).str.lower().str.strip()
        df = df.replace({np.nan: ""})
        
        cols = get_all_columns(df)
        model_col = cols['model']
        
        if not model_col:
             return Response({"error": "Model column not found"}, status=500)
             
        search = model_query.lower().strip()
        
        # Find partial match manually to be safe or use pandas
        # Using build_bike_entry for consistency
        
        user_favs = set()
        if request.user.is_authenticated:
             user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

        target_row = None
        for _, row in df.iterrows():
            curr_model = str(row.get(model_col, "")).lower().strip()
            if search in curr_model:
                target_row = row 
                break # First match
        
        if target_row is None:
            return Response({"error": "Bike not found"}, status=404)

        return Response(build_bike_entry(target_row, cols, user_favs, request))


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")


class BikeFullSpecificationsAPIView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        model_query = request.GET.get("model")

        if not model_query:
            return Response({"error": "model query param required"}, status=400)

        # Load Excel
        df = pd.read_excel(EXCEL_PATH, header=1)

        # Normalize columns
        df.columns = (
            df.columns
            .str.replace("\xa0", " ", regex=False)
            .str.replace("\n", " ", regex=False)
            .str.strip()
            .str.lower()
        )

        # Normalize model values
        df["model"] = df["model"].astype(str).str.lower().str.strip()

        search = model_query.lower().strip()

        # Partial match (important)
        bike = df[df["model"].str.contains(search, na=False)]

        if bike.empty:
            return Response({"error": "Bike not found"}, status=404)

        row = bike.iloc[0]

        # FULL SPEC RESPONSE
        return Response({
            "basic_info": {
                "model": row.get("model"),
                "brand": row.get("brand"),
                "price": row.get("price (₹)"),
                "bike_type": row.get("bike type")
            },

            "engine_performance": {
                "displacement": row.get("displacement"),
                "max_power": row.get("max power"),
                "max_torque": row.get("max torque"),
                "mileage": row.get("mileage"),
                "transmission": row.get("transmission"),
                "fuel_type": row.get("fuel type"),
                "cooling": row.get("cooling"),
                "cylinders": row.get("cylinders"),
                "valves_per_cylinder": row.get("valves/cyl")
            },

            "dimensions_weight": {
                "kerb_weight": row.get("kerb weight"),
                "ground_clearance": row.get("ground clearance"),
                "fuel_tank": row.get("fuel tank"),
                "reserve": row.get("reserve")
            },

            "brakes_suspension": {
                "brake_types": row.get("brake types"),
                "suspension": row.get("suspension"),
                "chassis": row.get("chassis"),
                "wheel_type": row.get("wheel type"),
                "tyre_type": row.get("tyre type")
            },

            "emission": {
                "emission": row.get("emission"),
                "clutch": row.get("clutch")
            }
        })


class FeedbackAPIView(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        data = request.data.copy()
        # Use simple string user_id as defined in model, or username
        data["user_id"] = request.user.username 
        
        serializer = FeedbackSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            return Response({"message": "Feedback submitted successfully"}, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


EXCEL_PATH = os.path.join(settings.BASE_DIR, "bikepaar", "search_bikes.xlsx")

def bike_details(request):
    model = request.GET.get("model")

    if not model:
        return JsonResponse({"error": "model query param required"}, status=400)

    df = pd.read_excel(EXCEL_PATH,header=1)

    # column names clean
    df.columns = df.columns.str.strip().str.lower()

    for _, row in df.iterrows():   # ✅ row defined HERE
        if row["model"].strip().lower() == model.strip().lower():

            image_path = row.get("image")  # ✅ NOW VALID

            image_url = (
                request.build_absolute_uri(settings.MEDIA_URL + image_path)
                if pd.notna(image_path) else None
            )

            return JsonResponse({
                "model": row["model"],
                "brand": row["brand"],
                "price": row["price (₹)"],
                "displacement": row["displacement"],
                "max_power": row["max power"],
                "max_torque": row["max torque"],
                "transmission": row["transmission"],
                "mileage": row["mileage"],
                "kerb_weight": row["kerb weight"],
                "fuel_tank": row["fuel tank"],
                "image_url": image_url
            })

    return JsonResponse({"error": "Bike not found"}, status=404)


# -------------------------------------------------------------------------
# EMAIL OTP VERIFICATION VIEWS
# -------------------------------------------------------------------------

class SendEmailOTPView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        email = request.data.get('email')
        if not email:
            return Response({"error": "Email is required"}, status=status.HTTP_400_BAD_REQUEST)

        # Generate 6-digit OTP
        otp = str(random.randint(100000, 999999))

        # Save to DB
        EmailOTP.objects.update_or_create(
            email=email,
            defaults={'otp': otp}
        )

        # Send Email
        subject = "Your BikePaar Verification Code"
        message = f"Your verification code is: {otp}"
        
        try:
            send_mail(
                subject,
                message,
                settings.DEFAULT_FROM_EMAIL,
                [email],
                fail_silently=False,
            )
            return Response({"message": "OTP sent successfully"})
        except Exception as e:
            return Response({"error": f"Failed to send email: {str(e)}"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

class VerifyEmailOTPView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        email = request.data.get('email')
        otp = request.data.get('otp')

        if not email or not otp:
            return Response({"error": "Email and OTP are required"}, status=status.HTTP_400_BAD_REQUEST)

        try:
            # Check OTP
            otp_entry = EmailOTP.objects.get(email=email, otp=otp)
            
            # OTP Correct: Verification Successful
            
            # 1. Update Profile (if user exists - e.g. password reset or late verification)
            if User.objects.filter(email=email).exists():
                user = User.objects.get(email=email)
                profile, created = Profile.objects.get_or_create(user=user)
                profile.is_email_verified = True
                profile.save()

            # 2. Mark OTP as verified (IMPORTANT for Signup)
            otp_entry.is_verified = True
            otp_entry.save()
            # Do NOT delete otp_entry yet. We need it for signup check.

            return Response({"message": "Email verified successfully", "verified": True})

        except EmailOTP.DoesNotExist:
            return Response({"error": "Invalid OTP"}, status=status.HTTP_400_BAD_REQUEST)


class PopularBikesAPIView(APIView):
    permission_classes = [AllowAny]
    def get(self, request):
        popular_terms = ["r15", "classic 350", "duke 390", "pulsar", "apache", "splendor", "mt 15", "bullet", "himalayan", "hunter"]
        df = pd.read_excel(EXCEL_PATH, header=1)
        df.columns = df.columns.astype(str).str.lower().str.strip()
        df = df.replace({np.nan: ""})
        cols = get_all_columns(df)
        model_col = cols['model']
        
        user_favs = set()
        if request.user.is_authenticated:
             user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

        if not model_col:
             return Response({"error": "Model column not found"}, status=500)

        mask = df[model_col].astype(str).str.lower().apply(lambda x: any(term in x for term in popular_terms))
        result_df = df[mask]
        result_df = result_df.head(15)

        mapped_results = []
        for _, row in result_df.iterrows():
             price_val = parse_price(row.get(cols['price'], 0))
             if price_val <= 0: continue
             mapped_results.append(build_bike_entry(row, cols, user_favs, request))

        return Response(mapped_results)


class RecentLaunchesAPIView(APIView):
    permission_classes = [AllowAny]
    def get(self, request):
        recent_bikes_by_brand = {
            "Royal Enfield": ["himalayan 450", "shotgun 650", "guerrilla 450", "bear 650"],
            "KTM": ["duke 390", "duke 250"],
            "TVS": ["apache rtr 310", "x", "ronin"],
            "Bajaj": ["pulsar ns400z", "freedom 125"],
            "Hero": ["mavrick 440", "karizma xmr"],
            "Honda": ["nx500", "cb350"],
            "Yamaha": ["r3", "mt-03"]
        }
        all_recent_names = [bike for bikes in recent_bikes_by_brand.values() for bike in bikes]
        
        df = pd.read_excel(EXCEL_PATH, header=1)
        df.columns = df.columns.astype(str).str.lower().str.strip()
        df = df.replace({np.nan: ""})
        cols = get_all_columns(df)
        model_col = cols['model']
        brand_col = cols.get('brand')
        
        user_favs = set()
        if request.user.is_authenticated:
             user_favs = set(FavouriteBike.objects.filter(user=request.user).values_list('model', flat=True))

        if not model_col:
             return Response({"error": "Model column not found"}, status=500)

        mask = df[model_col].astype(str).str.lower().apply(lambda x: any(term in x for term in all_recent_names))
        result_df = df[mask]
        
        if brand_col:
            result_df = result_df.sort_values(by=brand_col)
        else:
            result_df = result_df.sort_values(by=model_col)

        mapped_results = []
        for _, row in result_df.iterrows():
             price_val = parse_price(row.get(cols['price'], 0))
             if price_val <= 0: continue
             entry = build_bike_entry(row, cols, user_favs, request)
             entry['usage'] = "Launched 2024-2025"
             mapped_results.append(entry)

        return Response(mapped_results)

from .models import Notification
from .serializers import NotificationSerializer

class NotificationListView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        notifications = Notification.objects.all().order_by('-created_at')
        serializer = NotificationSerializer(notifications, many=True)
        return Response(serializer.data)

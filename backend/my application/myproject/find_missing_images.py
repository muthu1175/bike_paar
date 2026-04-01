import pandas as pd
import os
import re

# Paths
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "bikepaar", "search_bikes.xlsx")
MEDIA_BIKES_PATH = os.path.join(BASE_DIR, "media", "bikes")

# Mocking the resolve function from views.py
def resolve_bike_image_url(model_name, available_files_lower):
    if not model_name:
        return ""

    raw_name = str(model_name).replace('\ufeff', '').lower().strip()
    clean_name = re.sub(r'[^a-z0-9]', ' ', raw_name).strip()
    
    # Ignore header/metadata items
    if clean_name in ["model", "commuter bikes", "sports bikes", "scooters", "cruisers", "adventure bikes", "touring bikes", "electric vehicles", "discontinued models"]:
        return "IGNORE"

    candidates = [
        raw_name,                # Exact (lowercased)
        clean_name,              # Spaces only
        clean_name.replace(" ", "-"), # Dashes
        clean_name.replace(" ", "_"), # Underscores
        re.sub(r'[^a-z0-9]', '', raw_name), # Squashed
    ]
    
    # Fuzzy candidates
    fuzzy_name = re.sub(r'\s(bs[46]|abs|fi|edition|disc|drum|hybrid|connected|pro|plus|gen\s?2|4kwh|special|classic)$', '', clean_name)
    if fuzzy_name != clean_name:
        candidates.extend([
            fuzzy_name,
            fuzzy_name.replace(" ", "-"),
            re.sub(r'[^a-z0-9]', '', fuzzy_name)
        ])

    extensions = ['.jpg', '.jpeg', '.png', '.webp', '.avif']
    
    # Priority 1: Exact matches or variants
    for cand in candidates:
        if not cand: continue
        for ext in extensions:
            test_file = f"{cand}{ext}"
            if test_file in available_files_lower:
                return available_files_lower[test_file]

    # Priority 2: Substring matching (Stronger)
    for cand in candidates:
        if len(cand) < 4: continue
        cand_squashed = cand.replace(' ', '')
        for f_lower, f_original in available_files_lower.items():
            f_base = os.path.splitext(f_lower)[0]
            f_base_squashed = f_base.replace(' ', '').replace('-', '').replace('_', '')
            
            if cand_squashed in f_base_squashed or f_base_squashed in cand_squashed:
                 return f_original

    return ""

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

def main():
    print(f"Reading excel from: {EXCEL_PATH}")
    try:
        df = pd.read_excel(EXCEL_PATH, header=1) # Header is row 1
    except Exception as e:
        print(f"Error reading excel: {e}")
        return

    # Normalize columns
    df.columns = df.columns.astype(str).str.lower().str.strip()
    
    # Find columns
    model_col = None
    price_col = None
    
    for col in df.columns:
        if "model" in col or "bike model" in col:
            model_col = col
        if "price" in col or "cost" in col:
            price_col = col

    if not model_col:
        print("Model column not found!")
        return

    print(f"Model col: {model_col}, Price col: {str(price_col).encode('ascii', 'ignore').decode()}")

    # Load images
    if os.path.exists(MEDIA_BIKES_PATH):
        files = os.listdir(MEDIA_BIKES_PATH)
        available_files_lower = {f.replace('\ufeff', '').lower(): f for f in files if not os.path.isdir(os.path.join(MEDIA_BIKES_PATH, f))}
        print(f"Found {len(available_files_lower)} images in {MEDIA_BIKES_PATH}")
    else:
        print(f"Media path not found: {MEDIA_BIKES_PATH}")
        return

    missing_bikes = []
    
    for _, row in df.iterrows():
        model_name = row[model_col]
        price_raw = row.get(price_col, 0)
        
        # Filter by price like views.py
        if parse_price(price_raw) <= 0:
            continue
            
        if pd.isna(model_name) or str(model_name).strip() == "":
            continue
            
        img = resolve_bike_image_url(model_name, available_files_lower)
        if img == "":
            missing_bikes.append(str(model_name).strip())

    print("\n--- MISSING BIKES (Price > 0) ---")
    for bike in missing_bikes:
        print(bike)
    print(f"\nTotal missing: {len(missing_bikes)}")

if __name__ == "__main__":
    main()

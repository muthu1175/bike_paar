import pandas as pd
import os
import re
import sys

# Mock settings for local run
MEDIA_ROOT = r"f:\bikepaar\backend\my application\myproject\media"
EXCEL_PATH = r"f:\bikepaar\backend\my application\myproject\bikepaar\search_bikes.xlsx"

def resolve_bike_image_url_mock(model_name):
    if not model_name:
        return ""

    media_bikes_path = os.path.join(MEDIA_ROOT, 'bikes')
    if not os.path.exists(media_bikes_path):
        return "DIR_NOT_FOUND"

    files = os.listdir(media_bikes_path)
    available_files_lower = {f.lower(): f for f in files if not os.path.isdir(os.path.join(media_bikes_path, f))}

    def is_placeholder(filename):
        if filename == "placeholder.png": return True
        path = os.path.join(media_bikes_path, filename)
        try:
            return os.path.getsize(path) == 44421
        except:
            return False

    raw_name = str(model_name).replace('\ufeff', '').lower().strip()
    clean_name = re.sub(r'[^a-z0-9]', ' ', raw_name).strip()
    squashed_name = re.sub(r'[^a-z0-9]', '', raw_name)
    
    if clean_name in ["model", "commuter bikes", "sports bikes", "scooters", "cruisers", "adventure bikes", "touring bikes", "electric vehicles", "discontinued models", "commuter", "street/naked", "sports", "adventure/tourer", "scrambler", "cruiser", "scooter", "commuter/street", "sports/street", "supersports", "streetfighter/naked"]:
        return "METADATA_HEADER"

    extensions = ['.png', '.webp', '.avif', '.jpg', '.jpeg']

    candidates = [
        squashed_name,
        clean_name,
        clean_name.replace(" ", "-"),
        clean_name.replace(" ", "_"),
    ]
    
    for cand in candidates:
        if not cand: continue
        for ext in extensions:
            test_file = f"{cand}{ext}"
            if test_file in available_files_lower:
                actual_filename = available_files_lower[test_file]
                if not is_placeholder(actual_filename):
                    return actual_filename

    parts = clean_name.split()
    if len(parts) > 1:
        no_brand_candidates = []
        if len(parts) > 1: no_brand_candidates.append(" ".join(parts[1:]))
        if len(parts) > 2: no_brand_candidates.append(" ".join(parts[2:]))
        
        for nb_cand in no_brand_candidates:
            nb_squashed = re.sub(r'[^a-z0-9]', '', nb_cand)
            for ext in extensions:
                for test in [nb_squashed, nb_cand]:
                    test_file = f"{test}{ext}"
                    if test_file in available_files_lower:
                        actual_filename = available_files_lower[test_file]
                        if not is_placeholder(actual_filename):
                            return actual_filename

    model_words = [w for w in clean_name.split() if len(w) > 1]
    if len(model_words) >= 1:
        for file_lower, actual_name in available_files_lower.items():
            if is_placeholder(actual_name): continue
            
            file_clean = re.sub(r'[^a-z0-9]', ' ', file_lower)
            file_words = set(file_clean.split())
            
            if set(model_words).issubset(file_words):
                 return actual_name

    return "PLACEHOLDER"

def main():
    print(f"Loading Excel from: {EXCEL_PATH}")
    df = pd.read_excel(EXCEL_PATH, header=1)
    model_col = df.columns[0] # Assuming first column is model
    
    results = []
    for model in df[model_col]:
        if pd.isna(model): continue
        res = resolve_bike_image_url_mock(model)
        results.append((model, res))
    
    placeholders = [r for r in results if r[1] == "PLACEHOLDER"]
    print(f"\nTotal Bikes checked: {len(results)}")
    print(f"Bikes with placeholders: {len(placeholders)}")
    
    print("\n--- BIKES WITH PLACEHOLDERS ---")
    for m, r in placeholders:
        print(f"{m}")

if __name__ == "__main__":
    main()

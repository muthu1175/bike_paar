
import pandas as pd
import os

BASE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "bikepaar")
SEARCH_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")
AI_PATH = os.path.join(BASE_DIR, "ai", "Ai-bikedata.xlsx")

def check_file(path, name, header_row):
    print(f"--- Checking {name} ---")
    try:
        df = pd.read_excel(path, header=header_row)
        cols = [str(c).strip().lower() for c in df.columns]
        
        has_usage = any("usage" in c for c in cols)
        has_comfort = any("comfort" in c for c in cols)
        has_exp = any("experience" in c for c in cols)
        has_cat = any("category" in c for c in cols)
        
        print(f"Columns found: {len(cols)}")
        print(f"Has Usage: {has_usage}")
        print(f"Has Comfort: {has_comfort}")
        print(f"Has Experience: {has_exp}")
        print(f"Has Category: {has_cat}")
        
    except Exception as e:
        print(f"Error reading {name}: {e}")

check_file(SEARCH_PATH, "Search Data", 1)
check_file(AI_PATH, "AI Data", 0)

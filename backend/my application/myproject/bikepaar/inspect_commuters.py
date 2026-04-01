
import pandas as pd
import os
import sys
import io

# Force utf-8 for stdout
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

def get_all_columns(df):
    def find_col(keywords):
        for k in keywords:
            if k in df.columns: return k
        for col in df.columns:
            for k in keywords:
                if k in col: return col
        return None

    return {
        'type': find_col(["body type", "type"]),
        'category': find_col(["category", "segment"]), 
        'usage': find_col(["usage"]),
        'model': find_col(["model", "model name"]),
    }

file_path = "c:\\Users\\Muthusamy\\OneDrive\\Desktop\\back 1 zip\\my application\\myproject\\bikepaar\\search_bikes.xlsx"
print(f"Loading {file_path}...")

try:
    df = pd.read_excel(file_path, header=1)
    df.columns = df.columns.astype(str).str.lower().str.strip()
    
    cols = get_all_columns(df)
    
    # Potential Commuter Keywords (Initial Guesses)
    commuter_keywords = [
        "commuter", "commute", "daily",
        "splendor", "passion", "hero", "hf deluxe", "glamour", "super splendor",
        "shine", "cb shine", "livo", "cd 110", "dream", "sp 125", "unicorn",
        "platina", "ct 100", "ct 110", "ct 125", "pulsar 125", "pulsar 150",
        "tvs", "sport", "radeon", "star city", "victor",
        "yamaha", "sz-rr", "saluto"
    ]
    
    found_commuters = []
    
    for _, row in df.iterrows():
        bike_type = str(row.get(cols['type'], "")).lower().strip()
        category = str(row.get(cols['category'], "")).lower().strip()
        usage = str(row.get(cols['usage'], "")).lower().strip()
        model_name = str(row.get(cols['model'], "")).lower().strip()
        
        is_commuter = False
        
        # Check current weak logic
        if "commuter" in bike_type or "commuter" in category or "daily" in usage:
            is_commuter = True
            
        # Check strong keyword matching
        if any(k in model_name for k in commuter_keywords):
            # Exclude higher CC bikes that might match brand names incorrectly (e.g., Hero Karizma)
            # But for now just list them to see what we catch
             print(f"Potential Commuter: {row.get(cols['model'], 'Unknown')} (Matched by model)")
             found_commuters.append(row.get(cols['model'], 'Unknown'))

    print(f"\nTotal Potential Commuters Found: {len(found_commuters)}")

except Exception as e:
    print(f"Error: {e}")

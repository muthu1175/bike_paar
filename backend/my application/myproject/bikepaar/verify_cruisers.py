
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
    
    cruiser_keywords = [
        "cruiser", "cruise", 
        "avenger", "meteor", "bullet", "classic", "hunter", 
        "jawa", "yezdi", "ronin", "highness", "cb350", 
        "maverick", "imperiale", "vulcan", "gold wing", 
        "shotgun", "eliminator", "intruder"
    ]
    
    found_cruisers = []
    
    for _, row in df.iterrows():
        bike_type = str(row.get(cols['type'], "")).lower().strip()
        category = str(row.get(cols['category'], "")).lower().strip()
        usage = str(row.get(cols['usage'], "")).lower().strip()
        model_name = str(row.get(cols['model'], "")).lower().strip()
        
        is_cruiser = False
        
        if "cruiser" in bike_type or "cruiser" in category or "cruise" in usage:
            is_cruiser = True
        
        if any(k in model_name for k in cruiser_keywords):
            is_cruiser = True
            
        if is_cruiser:
            found_cruisers.append(model_name)

    print(f"\nFound {len(found_cruisers)} Cruiser Bikes:")
    for bike in found_cruisers[:20]: # Show first 20
        print(f"- {bike}")

except Exception as e:
    print(f"Error: {e}")

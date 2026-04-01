
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
    
    # Potential Street Keywords
    street_keywords = [
        "street", "naked",
        "duke", "mt", "fz", "fz-s", "fzs", "gixxer", "apache", "rtr", "pulsar ns", "pulsar n",
        "hornet", "xblade", "xtreme", "dominar", "himalayan 450", 
        "z900", "z650", "monster", "speed 400", "scrambler"
    ]
    
    found_streets = []
    
    for _, row in df.iterrows():
        bike_type = str(row.get(cols['type'], "")).lower().strip()
        category = str(row.get(cols['category'], "")).lower().strip()
        usage = str(row.get(cols['usage'], "")).lower().strip()
        model_name = str(row.get(cols['model'], "")).lower().strip()
        
        is_street = False
        
        # Check current logic
        if "street" in bike_type or "street" in category:
            is_street = True
            
        # Check keywords
        if any(k in model_name for k in street_keywords):
             print(f"Potential Street: {row.get(cols['model'], 'Unknown')}")
             found_streets.append(row.get(cols['model'], 'Unknown'))

    print(f"\nTotal Potential Street Bikes Found: {len(found_streets)}")

except Exception as e:
    print(f"Error: {e}")

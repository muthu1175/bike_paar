
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
    
    print("\n--- Unique Values ---")
    
    if cols['type']:
        print(f"\nType Column: {cols['type']}")
        print(df[cols['type']].astype(str).str.lower().unique())
        
    if cols['category']:
        print(f"\nCategory Column: {cols['category']}")
        print(df[cols['category']].astype(str).str.lower().unique())
        
    if cols['usage']:
        print(f"\nUsage Column: {cols['usage']}")
        print(df[cols['usage']].astype(str).str.lower().unique())
        
    # Check for potential cruisers manually
    print("\n--- Potential Cruisers Check ---")
    keywords = ["cruiser", "cruise", "avenger", "meteor", "bullet", "classic"]
    
    for _, row in df.iterrows():
        text = (str(row.get(cols['type'], "")) + " " + 
                str(row.get(cols['category'], "")) + " " + 
                str(row.get(cols['usage'], "")) + " " + 
                str(row.get(cols['model'], ""))).lower()
                
        if any(k in text for k in keywords):
            print(f"Found match: {row.get(cols['model'], 'Unknown')} | Type: {row.get(cols['type'], '')} | Cat: {row.get(cols['category'], '')}")

except Exception as e:
    print(f"Error: {e}")

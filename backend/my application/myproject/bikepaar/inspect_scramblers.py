
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
    
    # Potential Scrambler/Adventure Keywords
    scrambler_keywords = [
        "scrambler", "adventure", "off-road",
        "himalayan", "xpulse", "v-strom", "v strom", 
        "yezdi scrambler", "yezdi adventure", "ronin", 
        "hunter", "triumph scrambler", "ktm 390 adventure", 
        "ktm 250 adventure", "gs 310", "g 310 gs", "versys",
        "desert sled", "klx", "crf"
    ]
    
    found_scramblers = []
    
    for _, row in df.iterrows():
        model_name = str(row.get(cols['model'], "")).lower().strip()
        
        if any(k in model_name for k in scrambler_keywords):
             print(f"Potential Scrambler: {row.get(cols['model'], 'Unknown')}")
             found_scramblers.append(row.get(cols['model'], 'Unknown'))

    print(f"\nTotal Potential Scrambler Bikes Found: {len(found_scramblers)}")

except Exception as e:
    print(f"Error: {e}")

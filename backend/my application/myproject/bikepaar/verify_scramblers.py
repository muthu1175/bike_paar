
import pandas as pd
import os
import sys
import re
import io

# Force utf-8 for stdout
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

file_path = "c:\\Users\\Muthusamy\\OneDrive\\Desktop\\back 1 zip\\my application\\myproject\\bikepaar\\search_bikes.xlsx"
print(f"Loading {file_path}...")

try:
    df = pd.read_excel(file_path, header=1)
    df.columns = df.columns.astype(str).str.lower().str.strip()
    
    # Simple col finder
    def find_col(keywords):
        for k in keywords:
            if k in df.columns: return k
        for col in df.columns:
            for k in keywords:
                if k in col: return col
        return None
    
    model_col = find_col(["model", "model name"])
    type_col = find_col(["body type", "type"])
    category_col = find_col(["category", "segment"])
    usage_col = find_col(["usage"])
    
    scrambler_keywords = [
        "scrambler", "desert sled",
        "himalayan", "scram 411", "xpulse", 
        "yezdi scrambler", "yezdi adventure",
        "v-strom", "v strom", "versys",
        "ronin", "hunter", 
        "gs 310", "g 310 gs", "390 adventure", "250 adventure",
        "klx", "crf", "tiger", "africa twin", "multistrada",
        "pan america", "transalp", "nx500"
    ]
    
    found_scramblers = []
    
    for _, row in df.iterrows():
        model_name = str(row.get(model_col, "")).lower().strip()
        bike_type = str(row.get(type_col, "")).lower().strip()
        category = str(row.get(category_col, "")).lower().strip()
        usage = str(row.get(usage_col, "")).lower().strip()
        
        is_scrambler = False
        
        # 1. Broad Check
        if (
            "scrambler" in bike_type
            or "scrambler" in category
            or "scrambler" in usage
            or "adventure" in category
            or "off-road" in category
        ):
            is_scrambler = True
            
        # 2. Specific Model Check
        if any(k in model_name for k in scrambler_keywords):
            is_scrambler = True
            
        if is_scrambler:
            found_scramblers.append(model_name)

    print(f"\nFound {len(found_scramblers)} Scrambler/Adv Bikes:")
    for bike in found_scramblers[:30]:
        print(f"- {bike}")

except Exception as e:
    print(f"Error: {e}")

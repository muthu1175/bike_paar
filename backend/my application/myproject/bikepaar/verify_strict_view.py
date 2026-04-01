
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
    
    # Strict Scrambler logic replicated from view
    scrambler_keywords = [
        "scrambler", "desert sled",
        "scram 411", "yezdi scrambler",
        "ronin", "hunter", 
        "svartpilen", "caballero", "cl-x" 
    ]
    
    found_scramblers = []
    
    for _, row in df.iterrows():
        model_name = str(row.get(model_col, "")).lower().strip()
        bike_type = str(row.get(type_col, "")).lower().strip()
        category = str(row.get(category_col, "")).lower().strip()
        usage = str(row.get(usage_col, "")).lower().strip()
        
        is_scrambler = False
        
        if (
            "scrambler" in bike_type
            or "scrambler" in category
            or "scrambler" in usage
        ):
            is_scrambler = True
            
        if any(k in model_name for k in scrambler_keywords):
            is_scrambler = True
            
        # Explicit Safety
        if "adventure" in model_name and "scrambler" not in model_name:
            is_scrambler = False
            
        if is_scrambler:
            found_scramblers.append(model_name)

    print(f"\nFound {len(found_scramblers)} Strict Scrambler Bikes:")
    for bike in found_scramblers[:30]:
        print(f"- {bike}")

except Exception as e:
    print(f"Error: {e}")

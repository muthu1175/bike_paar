
import pandas as pd
import os
import sys
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
    
    # Strict Scrambler list
    strict_scrambler_keywords = [
        "scrambler", "desert sled", "scram 411", "yezdi scrambler", 
        "ronin", "hunter", "svartpilen", "caballero"
    ]
    
    # Excluded Adv bikes that were previously included
    adv_exclusions = [
        "himalayan 450", "himalayan 411", # Adventure, not scrambler (except Scram 411)
        "xpulse", "adventure", "v-strom", "versys", "gs 310", "tiger", "africa twin"
    ]
    
    found_scramblers = []
    
    for _, row in df.iterrows():
        model_name = str(row.get(model_col, "")).lower().strip()
        
        is_scrambler = False
        
        # Check strict keywords
        if any(k in model_name for k in strict_scrambler_keywords):
            is_scrambler = True
            
        # Ensure exclusions work (though keywords above generally avoid them, safe check)
        if any(ex in model_name for ex in adv_exclusions):
            # Only exclude if it doesn't have "scrambler" in name explicitly
            if "scrambler" not in model_name: 
                 is_scrambler = False
            
        if is_scrambler:
            found_scramblers.append(model_name)

    print(f"\nFound {len(found_scramblers)} Strict Scrambler Bikes:")
    for bike in found_scramblers:
        print(f"- {bike}")

except Exception as e:
    print(f"Error: {e}")

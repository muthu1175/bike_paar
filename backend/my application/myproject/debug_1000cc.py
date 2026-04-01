import pandas as pd
import numpy as np
import os
import re
import sys

# Force UTF-8 for Windows console
sys.stdout.reconfigure(encoding='utf-8')

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "bikepaar", "search_bikes.xlsx")

print(f"Checking file at: {EXCEL_PATH}")

try:
    if not os.path.exists(EXCEL_PATH):
        EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")
        print(f"Trying alternate path: {EXCEL_PATH}")

    df = pd.read_excel(EXCEL_PATH, header=1)
    df.columns = df.columns.str.lower().str.strip()
    df = df.replace({np.nan: ""})
    
    print(f"Columns found: {list(df.columns)}")

    def find_col(keywords):
        for k in keywords:
            if k in df.columns: return k
        for col in df.columns:
            for k in keywords:
                if k in col: return col
        return None

    engine_col = find_col(["displacement", "engine"])
    print(f"Engine Column: {engine_col}")

    count = 0
    if engine_col:
        for index, row in df.iterrows():
            # Robust logic: remove commas, lower case
            disp_text = str(row.get(engine_col, "")).replace(",", "").lower()
            match = re.search(r"(\d+(\.\d+)?)", disp_text)
            if match:
                cc = float(match.group(1))
                if cc >= 1000:
                    model = str(row.get('model', 'Unknown'))
                    print(f"MATCH: {model} - {cc}cc")
                    count += 1
    
    print(f"\nTotal Bikes Found >= 1000cc: {count}")

except Exception as e:
    print(f"Error: {e}")

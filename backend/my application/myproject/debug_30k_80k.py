import pandas as pd
import numpy as np
import os
import re
import sys

# Force UTF-8 output
sys.stdout.reconfigure(encoding='utf-8')

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "bikepaar", "search_bikes.xlsx")

def debug_30k_80k():
    print(f"Checking data in: {EXCEL_PATH}")
    
    try:
        df = pd.read_excel(EXCEL_PATH, header=1)
        df.columns = df.columns.astype(str).str.lower().str.strip()
        df = df.replace({np.nan: ""})
        
        print("Columns found:", df.columns.tolist())
        
        def find_col(keywords):
            for k in keywords:
                if k in df.columns: return k
            for col in df.columns:
                for k in keywords:
                    if k in col: return col
            return None

        price_col = find_col(["price", "ex-showroom price", "price (inr)", "cost", "mrp"])
        model_col = find_col(["model", "model name", "bike model"])
        
        print(f"Price Column: {price_col}")
        print(f"Model Column: {model_col}")
        
        if not price_col:
            print("❌ PRICE COLUMN NOT FOUND")
            return

        count = 0
        
        for index, row in df.iterrows():
            raw_price = row.get(price_col, 0)
            model = row.get(model_col, "Unknown")
            
            price_val = 0
            try:
                raw_str = str(raw_price).lower().strip()
                if "lakh" in raw_str:
                    num = re.findall(r"[\d\.]+", raw_str)
                    if num: price_val = int(float(num[0]) * 100000)
                elif "cr" in raw_str:
                    num = re.findall(r"[\d\.]+", raw_str)
                    if num: price_val = int(float(num[0]) * 10000000)
                else:
                    # Handle "1,200", "95000", "Rs. 45000"
                    clean_price = re.sub(r'[^\d\.]', '', raw_str)
                    if clean_price: price_val = int(float(clean_price))
            except: 
                price_val = 0
            
            # Check 30k - 80k range
            if 30000 <= price_val <= 80000:
                print(f"MATCH: {model} - ₹{price_val}")
                count += 1
            # Debug some misses
            elif index < 5: 
                print(f"SKIP: {model} - ₹{price_val} (Raw: {raw_price})")

        print(f"\nTotal Bikes Found (30k - 80k): {count}")

    except Exception as e:
        print(f"ERROR: {e}")

if __name__ == "__main__":
    debug_30k_80k()

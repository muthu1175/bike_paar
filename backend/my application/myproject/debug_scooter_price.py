import pandas as pd
import numpy as np
import os
import re

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "bikepaar", "search_bikes.xlsx")

def debug_scooter_price():
    try:
        print(f"Reading {EXCEL_PATH}...")
        df = pd.read_excel(EXCEL_PATH, header=1)
        
        # normalize columns just like in views.py
        # Store original columns to see what they were
        orig_cols = list(df.columns)
        print("Original Columns:", orig_cols[:10], "...")

        def find_col(df_cols, keywords):
            for k in keywords:
                for col in df_cols:
                    if k in str(col).lower(): return col
            return None

        # Simulate the view logic
        # Note: in views.py I used `find_col` with a list of keywords and iterating nicely.
        # Let's reproduce the exact logic from views.py
        
        # normalize df columns first as in views.py?
        # In views.py: df.columns = df.columns.str.lower().str.strip()
        df.columns = df.columns.astype(str).str.lower().str.strip()
        
        def find_col_exact(keywords):
            for k in keywords:
                if k in df.columns: return k
            for col in df.columns:
                for k in keywords:
                    if k in col: return col
            return None

        price_col = find_col_exact(["price", "ex-showroom price", "price (inr)", "cost", "mrp"])
        model_col = find_col_exact(["model", "model name", "bike model"])
        category_col = find_col_exact(["category", "segment"])
        type_col = find_col_exact(["body type", "type", "bike type"])
        usage_col = find_col_exact(["usage"])
        fuel_col = find_col_exact(["fuel", "fuel type"])

        print(f"Found Price Col: '{price_col}'")
        print(f"Found Model Col: '{model_col}'")
        
        count = 0
        for idx, row in df.iterrows():
            def safe_str(val):
                return str(val).lower().strip() if pd.notna(val) else ""

            bike_type = safe_str(row.get(type_col, ""))
            category = safe_str(row.get(category_col, ""))
            usage = safe_str(row.get(usage_col, ""))
            fuel = safe_str(row.get(fuel_col, ""))
            
            if (
                "scooter" in bike_type
                or "scooter" in category
                or "scooter" in usage
                or "electric" in fuel
            ):
                raw_price = row.get(price_col, 0)
                print(f"--- Row {idx} [Model: {row.get(model_col)}] ---")
                print(f"Raw Price: '{raw_price}' (Type: {type(raw_price)})")
                
                # Test Parsing
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
                        clean_price = re.sub(r'[^\d\.]', '', raw_str)
                        if clean_price: price_val = int(float(clean_price))
                except Exception as e:
                     print(f"Parse Error: {e}")
                
                print(f"Parsed Price: {price_val}")
                
                count += 1
                if count >= 5: break

    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    debug_scooter_price()

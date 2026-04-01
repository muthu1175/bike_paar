import os
import pandas as pd
import re
import numpy as np

# Simulate Django Environment Paths
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
# Check both potential paths
EXCEL_PATH_1 = os.path.join(BASE_DIR, "bikepaar", "search_bikes.xlsx")
EXCEL_PATH_2 = os.path.join(BASE_DIR, "search_bikes.xlsx")

if os.path.exists(EXCEL_PATH_1):
    EXCEL_PATH = EXCEL_PATH_1
else:
    EXCEL_PATH = EXCEL_PATH_2

print(f"Using Excel Path: {EXCEL_PATH}")

try:
    df = pd.read_excel(EXCEL_PATH, header=1)
    df.columns = df.columns.str.lower().str.strip()
    df = df.replace({np.nan: ""})

    def find_col(keywords):
        for k in keywords:
            if k in df.columns: return k
        for col in df.columns:
            for k in keywords:
                if k in col: return col
        return None

    price_col = find_col(["price", "ex-showroom price", "price (inr)", "cost", "mrp"])
    print(f"Price Column Found: {price_col}")

    ranges = [
        (80000, 150000, "80k-1.5L"),
        (150000, 300000, "1.5L-3L"),
        (300000, 500000, "3L-5L"),
        (500000, 1000000, "5L-10L")
    ]

    for min_p, max_p, name in ranges:
        count = 0
        samples = []
        for _, row in df.iterrows():
            raw_price = row.get(price_col, 0)
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
            except: price_val = 0

            if min_p <= price_val <= max_p:
                count += 1
                if len(samples) < 3:
                    samples.append(f"{row.get('model name', 'Unknown')} ({price_val})")
        
        print(f"\nRange {name}: Found {count} bikes.")
        if count > 0:
            print(f"Samples: {', '.join(samples)}")
        else:
            print("WARNING: No bikes found in this range!")

except Exception as e:
    print(f"CRASH: {e}")

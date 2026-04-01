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

        # Above 30 Lakh Filter
        if price_val > 3000000:
            count += 1
            samples.append(f"{row.get('model name', 'Unknown')} ({price_val})")
    
    print(f"\nFound {count} bikes above 30 Lakhs.")
    if count > 0:
        print("Samples:")
        for s in samples[:10]:
            print(f" - {s}")
    else:
        print("WARNING: No bikes found > 30L!")

except Exception as e:
    print(f"CRASH: {e}")

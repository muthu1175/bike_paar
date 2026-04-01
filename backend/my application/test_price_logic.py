import pandas as pd
import os
import numpy as np
import re

BASE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "myproject", "bikepaar")
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")

def normalize(val):
    return str(val).lower().strip()

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
    category_col = find_col(["category", "segment"])
    usage_col = find_col(["usage"])
    type_col = find_col(["body type", "type", "bike type"])
    model_col = find_col(["model", "model name", "bike model"])
    
    print(f"Price column found: {price_col}")

    adventure_bikes = []
    tourer_bikes = []

    for _, row in df.iterrows():
        category = normalize(row.get(category_col, ""))
        usage = normalize(row.get(usage_col, ""))
        bike_type = normalize(row.get(type_col, ""))
        
        # Parse Price
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
        
        model = row.get(model_col, "")
        
        if (
            "adventure" in category
            or "adventure" in usage
            or "touring" in usage
            or "off-road" in usage
            or "adv" in bike_type
            or "dual" in category
        ):
            adventure_bikes.append((model, price_val))

        if (
            "tourer" in category
            or "touring" in category
            or "tour" in usage
            or "touring" in usage
            or "highway" in usage
            or "gt" in bike_type
            or "tourer" in bike_type
        ):
            tourer_bikes.append((model, price_val))

    print(f"\nAdventure Bikes: {len(adventure_bikes)}")
    for name, price in adventure_bikes[:5]:
        print(f"  {str(name).encode('ascii', 'ignore').decode()}: {price}")

    print(f"\nTourer Bikes: {len(tourer_bikes)}")
    for name, price in tourer_bikes[:5]:
        print(f"  {str(name).encode('ascii', 'ignore').decode()}: {price}")

except Exception as e:
    print(f"Error: {e}")

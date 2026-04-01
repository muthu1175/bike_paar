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
    model_col = find_col(["model", "model name", "bike model"])
    image_col = find_col(["image", "image url", "picture"])
    brand_col = find_col(["brand"])
    engine_col = find_col(["displacement", "engine"])
    category_col = find_col(["category", "segment"])

    bikes = []
    print(f"Price col: {str(price_col).encode('ascii', 'ignore').decode()}")
    
    for _, row in df.iterrows():
        # Robust Price Parsing
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
        
        # Image
        img_url = str(row.get(image_col, "")).strip()
        if img_url.lower() == "nan": img_url = ""

        bikes.append({
            "name": row.get(model_col, ""),
            "brand": row.get(brand_col, ""),
            "engine": row.get(engine_col, ""),
            "description": row.get(category_col, ""),
            "price": price_val,
            "imageUrl": img_url,
        })
    
    print(f"Total bikes processed: {len(bikes)}")
    print("Sample 5 bikes (Name : Price):")
    for b in bikes[:5]:
        print(f"  {b['name']} : {b['price']}")


except Exception as e:
    print(f"Error: {e}")

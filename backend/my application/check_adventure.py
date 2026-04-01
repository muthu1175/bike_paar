import pandas as pd
import os
import numpy as np

BASE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "myproject", "bikepaar")
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")

try:
    df = pd.read_excel(EXCEL_PATH, header=1)
    df.columns = df.columns.astype(str).str.lower().str.strip()
    df = df.replace({np.nan: ""})
    
    def safe_str(val):
        return str(val).lower().strip() if pd.notna(val) else ""

    # Find columns
    type_col = next((col for col in df.columns if "type" in col), "bike type")
    category_col = next((col for col in df.columns if "category" in col), "category")
    usage_col = next((col for col in df.columns if "usage" in col), "usage")
    model_col = next((col for col in df.columns if "model" in col), "model")

    print(f"Columns: {[str(c).encode('ascii', 'ignore').decode() for c in df.columns]}")

    adventure_bikes = []
    
    for _, row in df.iterrows():
        bike_type = safe_str(row.get(type_col, ""))
        category = safe_str(row.get(category_col, ""))
        usage = safe_str(row.get(usage_col, ""))
        model = row.get(model_col, "")
        
        if (
            "adventure" in category
            or "adventure" in usage
            or "touring" in usage
            or "off-road" in usage
            or "adv" in bike_type
            or "dual" in category
        ):
            adventure_bikes.append(model)

    print(f"Found {len(adventure_bikes)} adventure bikes:")
    for bike in adventure_bikes:
        print(f"- {str(bike).encode('ascii', 'ignore').decode()}")

except Exception as e:
    print(f"Error: {e}")

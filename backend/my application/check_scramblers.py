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

    scramblers = []
    
    for _, row in df.iterrows():
        bike_type = safe_str(row.get(type_col, ""))
        category = safe_str(row.get(category_col, ""))
        usage = safe_str(row.get(usage_col, ""))
        model = row.get(model_col, "")
        
        if (
            "scrambler" in bike_type
            or "scrambler" in category
            or "scrambler" in usage
            or "neo" in category
            or "retro" in category
        ):
            scramblers.append(model)

    print(f"Found {len(scramblers)} scrambler bikes:")
    for bike in scramblers:
        print(f"- {str(bike).encode('ascii', 'ignore').decode()}")

except Exception as e:
    print(f"Error: {e}")

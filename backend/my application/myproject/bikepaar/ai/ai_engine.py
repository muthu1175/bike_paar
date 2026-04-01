import pandas as pd
import os

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
EXCEL_PATH = os.path.join(BASE_DIR, "Ai-bikedata.xlsx")

def normalize_text(val):
    return str(val).lower().strip()

def suggest_bikes(filters):
    df = pd.read_excel(EXCEL_PATH, header=1)

    # normalize column names
    df.columns = df.columns.str.lower().str.strip()

    # rename bike name column
    if "bike name" in df.columns:
        df = df.rename(columns={"bike name": "bike_name"})

    # numeric safe conversion
    df["price"] = pd.to_numeric(df["price"], errors="coerce").fillna(0)
    df["mileage"] = pd.to_numeric(df["mileage"], errors="coerce").fillna(0)

    # normalize text columns
    text_cols = ["bike_name", "type", "usage", "comfort", "category", "experience"]
    for col in text_cols:
        if col in df.columns:
            df[col] = df[col].apply(normalize_text)

    # ================= FILTERS ================= #

    # ✅ EXACT bike match (MAIN FIX)
    if "bike_name" in filters and filters["bike_name"]:
        bike = normalize_text(filters["bike_name"])
        df = df[df["bike_name"] == bike]

    if "type" in filters:
        df = df[df["type"] == normalize_text(filters["type"])]

    if "usage" in filters:
        df = df[df["usage"] == normalize_text(filters["usage"])]

    if "comfort" in filters:
        df = df[df["comfort"] == normalize_text(filters["comfort"])]

    if "category" in filters:
        df = df[df["category"] == normalize_text(filters["category"])]

    if "experience" in filters:
        df = df[df["experience"] == normalize_text(filters["experience"])]

    if "mileage" in filters:
        df = df[df["mileage"] >= int(filters["mileage"])]

    # budget range support
    if "min_price" in filters:
        df = df[df["price"] >= int(filters["min_price"])]

    if "max_price" in filters:
        df = df[df["price"] <= int(filters["max_price"])]

    # ================= RESULT ================= #
    results = []
    for _, row in df.iterrows():
        results.append({
            "bike_name": row["bike_name"],
            "price": int(row["price"]),
            "type": row["type"],
            "usage": row["usage"],
            "comfort": row["comfort"],
            "category": row["category"],
            "experience": row["experience"],
            "mileage": int(row["mileage"])
        })

    return results

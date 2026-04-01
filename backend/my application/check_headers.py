import pandas as pd
import os
BASE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "myproject", "bikepaar")
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")

try:
    df0 = pd.read_excel(EXCEL_PATH)
    print("Header=0 Columns:", df0.columns.tolist()[:5])
    
    df1 = pd.read_excel(EXCEL_PATH, header=1)
    print("Header=1 Columns:", df1.columns.tolist()[:5])
except Exception as e:
    print(f"Error: {e}")

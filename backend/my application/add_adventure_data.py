import pandas as pd
import os
import numpy as np

BASE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "myproject", "bikepaar")
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")

new_bikes = [
    {
        "Model": "KTM 390 Adventure",
        "Brand": "KTM",
        "Price (₹)": "3.60 Lakh",
        "Displacement": "373.2 cc",
        "Mileage": "28 kmpl",
        "Bike Type": "Adventure Tourer",
        "Category": "Adventure",
        "Usage": "Off-Road & Touring",
        "Max Power": "43.5 PS",
        "Max Torque": "37 Nm",
        "Image": "/media/bikes/ktm_390_adv.jpg",
        "Fuel Type": "Petrol"
    },
    {
        "Model": "Royal Enfield Himalayan 450",
        "Brand": "Royal Enfield",
        "Price (₹)": "2.85 Lakh",
        "Displacement": "452 cc",
        "Mileage": "30 kmpl",
        "Bike Type": "Adventure",
        "Category": "Adventure",
        "Usage": "All-Terrain",
        "Max Power": "40.02 PS",
        "Max Torque": "40 Nm",
        "Image": "/media/bikes/himalayan_450.jpg",
        "Fuel Type": "Petrol"
    },
    {
        "Model": "Hero Xpulse 200 4V",
        "Brand": "Hero",
        "Price (₹)": "1.45 Lakh",
        "Displacement": "199.6 cc",
        "Mileage": "40 kmpl",
        "Bike Type": "Adventure",
        "Category": "Adventure",
        "Usage": "Off-Road",
        "Max Power": "19.1 PS",
        "Max Torque": "17.35 Nm",
        "Image": "/media/bikes/xpulse_200.jpg",
        "Fuel Type": "Petrol"
    },
    {
        "Model": "BMW G 310 GS",
        "Brand": "BMW",
        "Price (₹)": "3.30 Lakh",
        "Displacement": "313 cc",
        "Mileage": "30 kmpl",
        "Bike Type": "Adventure Tourer",
        "Category": "Adventure",
        "Usage": "Touring",
        "Max Power": "34 PS",
        "Max Torque": "28 Nm",
        "Image": "/media/bikes/bmw_g310gs.jpg",
        "Fuel Type": "Petrol"
    },
    {
        "Model": "Yezdi Adventure",
        "Brand": "Yezdi",
        "Price (₹)": "2.19 Lakh",
        "Displacement": "334 cc",
        "Mileage": "30 kmpl",
        "Bike Type": "Adventure",
        "Category": "Adventure",
        "Usage": "Touring",
        "Max Power": "30.2 PS",
        "Max Torque": "29.9 Nm",
        "Image": "/media/bikes/yezdi_adventure.jpg",
        "Fuel Type": "Petrol"
    }
]

try:
    df = pd.read_excel(EXCEL_PATH, header=1)
    
    # Store original columns to match format
    original_columns = df.columns.tolist()
    
    mapped_bikes = []
    for bike in new_bikes:
        row = {}
        for col in original_columns:
            col_lower = str(col).lower().strip()
            val = ""
            if "model" in col_lower: val = bike["Model"]
            elif "brand" in col_lower: val = bike["Brand"]
            elif "price" in col_lower: val = bike["Price (₹)"]
            elif "displacement" in col_lower: val = bike["Displacement"]
            elif "mileage" in col_lower: val = bike["Mileage"]
            elif "type" in col_lower: val = bike["Bike Type"]
            elif "category" in col_lower: val = bike["Category"]
            elif "usage" in col_lower: val = bike["Usage"]
            elif "power" in col_lower: val = bike["Max Power"]
            elif "torque" in col_lower: val = bike["Max Torque"]
            elif "image" in col_lower: val = bike["Image"]
            elif "fuel" in col_lower and "tank" not in col_lower: val = bike["Fuel Type"]
            
            row[col] = val
        mapped_bikes.append(row)

    new_df = pd.DataFrame(mapped_bikes)
    
    # Append
    start_row = len(pd.read_excel(EXCEL_PATH, header=None))
    
    with pd.ExcelWriter(EXCEL_PATH, mode='a', if_sheet_exists='overlay') as writer:
        new_df.to_excel(writer, index=False, header=False, startrow=start_row)
        
    print(f"Successfully added {len(new_bikes)} adventure bikes.")

except Exception as e:
    print(f"Error: {e}")

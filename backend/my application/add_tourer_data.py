import pandas as pd
import os
import numpy as np

BASE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "myproject", "bikepaar")
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")

new_bikes = [
    {
        "Model": "Honda Gold Wing Tour",
        "Brand": "Honda",
        "Price (₹)": "39.20 Lakh",
        "Displacement": "1833 cc",
        "Mileage": "14 kmpl",
        "Bike Type": "Tourer",
        "Category": "Tourer",
        "Usage": "Long Distance Touring",
        "Max Power": "126 PS",
        "Max Torque": "170 Nm",
        "Image": "/media/bikes/goldwing.jpg",
        "Fuel Type": "Petrol"
    },
    {
        "Model": "Harley-Davidson Road King",
        "Brand": "Harley-Davidson",
        "Price (₹)": "26.99 Lakh",
        "Displacement": "1746 cc",
        "Mileage": "18 kmpl",
        "Bike Type": "Tourer",
        "Category": "Tourer",
        "Usage": "Highway Touring",
        "Max Power": "84 HP",
        "Max Torque": "147 Nm",
        "Image": "/media/bikes/road_king.jpg",
        "Fuel Type": "Petrol"
    },
    {
        "Model": "BMW K 1600 GTL",
        "Brand": "BMW",
        "Price (₹)": "33.00 Lakh",
        "Displacement": "1649 cc",
        "Mileage": "17 kmpl",
        "Bike Type": "Tourer",
        "Category": "Tourer",
        "Usage": "Luxury Touring",
        "Max Power": "160 BHP",
        "Max Torque": "180 Nm",
        "Image": "/media/bikes/k1600gtl.jpg",
        "Fuel Type": "Petrol"
    },
    {
        "Model": "Indian Roadmaster",
        "Brand": "Indian",
        "Price (₹)": "43.21 Lakh",
        "Displacement": "1890 cc",
        "Mileage": "15 kmpl",
        "Bike Type": "Tourer",
        "Category": "Tourer",
        "Usage": "Luxury Touring",
        "Max Power": "116 HP",
        "Max Torque": "171 Nm",
        "Image": "/media/bikes/roadmaster.jpg",
        "Fuel Type": "Petrol"
    },
    {
        "Model": "Kawasaki Versys 1000",
        "Brand": "Kawasaki",
        "Price (₹)": "12.19 Lakh",
        "Displacement": "1043 cc",
        "Mileage": "20 kmpl",
        "Bike Type": "Adventure Tourer",
        "Category": "Tourer",
        "Usage": "Sports Touring",
        "Max Power": "120 PS",
        "Max Torque": "102 Nm",
        "Image": "/media/bikes/versys1000.jpg",
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
        
    print(f"Successfully added {len(new_bikes)} tourer bikes.")

except Exception as e:
    print(f"Error: {e}")

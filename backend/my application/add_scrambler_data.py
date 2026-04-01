import pandas as pd
import os
import numpy as np

BASE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "myproject", "bikepaar")
EXCEL_PATH = os.path.join(BASE_DIR, "search_bikes.xlsx")

new_bikes = [
    {
        "Model": "Triumph Scrambler 400 X",
        "Brand": "Triumph",
        "Price (₹)": "2.63 Lakh",
        "Displacement": "398.15 cc",
        "Mileage": "28 kmpl",
        "Bike Type": "Scrambler",
        "Category": "Scrambler",
        "Usage": "On and Off Road",
        "Max Power": "40 PS",
        "Max Torque": "37.5 Nm",
        "Image": "/media/bikes/triumph_scrambler_400x.jpg",
        "Fuel Type": "Petrol"
    },
    {
        "Model": "Yezdi Scrambler",
        "Brand": "Yezdi",
        "Price (₹)": "2.10 Lakh",
        "Displacement": "334 cc",
        "Mileage": "32 kmpl",
        "Bike Type": "Scrambler",
        "Category": "Scrambler",
        "Usage": "Off Road",
        "Max Power": "29.1 kW",
        "Max Torque": "28.2 Nm",
        "Image": "/media/bikes/yezdi_scrambler.jpg", 
        "Fuel Type": "Petrol"
    },
    {
        "Model": "Ducati Scrambler Icon",
        "Brand": "Ducati",
        "Price (₹)": "10.39 Lakh",
        "Displacement": "803 cc",
        "Mileage": "19 kmpl",
        "Bike Type": "Scrambler",
        "Category": "Scrambler",
        "Usage": "Lifestyle",
        "Max Power": "73 hp",
        "Max Torque": "65.2 Nm",
        "Image": "/media/bikes/ducati_scrambler.jpg",
        "Fuel Type": "Petrol"
    },
    {
        "Model": "Royal Enfield Scram 411",
        "Brand": "Royal Enfield",
        "Price (₹)": "2.06 Lakh",
        "Displacement": "411 cc",
        "Mileage": "33 kmpl",
        "Bike Type": "Scrambler",
        "Category": "Scrambler",
        "Usage": "Adventure",
        "Max Power": "24.3 bhp",
        "Max Torque": "32 Nm",
        "Image": "/media/bikes/scram_411.jpg",
        "Fuel Type": "Petrol"
    }
]

try:
    df = pd.read_excel(EXCEL_PATH, header=1)
    
    # Store original columns to match format
    original_columns = df.columns.tolist()
    
    # Create rows for new bikes (mapping keys to match existing rough column names)
    # Since we cleaned columns in check script, here we need to be careful.
    # We will just append to the dataframe. Pandas will handle alignment if columns match.
    
    # Let's align keys to what pandas read roughly
    mapped_bikes = []
    for bike in new_bikes:
        row = {}
        for col in original_columns:
            # Simple heuristic mapping
            col_lower = str(col).lower().strip()
            val = ""
            if "model" in col_lower: val = bike["Model"]
            elif "brand" in col_lower: val = bike["Brand"]
            elif "price" in col_lower: val = bike["Price (₹)"]
            elif "displacement" in col_lower: val = bike["Displacement"]
            elif "mileage" in col_lower: val = bike["Mileage"]
            elif "type" in col_lower: val = bike["Bike Type"] # Bike Type
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
    combined_df = pd.concat([df, new_df], ignore_index=True)
    
    # Save back (Need to preserve header row 0 which might be empty or title)
    # The read_excel used header=1, so row 0 was skipped.
    # We should probably write back carefully.
    
    # Simpler approach: Just read normally to see structure
    full_df = pd.read_excel(EXCEL_PATH, header=None)
    
    # We really just want to append to the bottom.
    start_row = len(full_df)
    
    with pd.ExcelWriter(EXCEL_PATH, mode='a', if_sheet_exists='overlay') as writer:
        new_df.to_excel(writer, index=False, header=False, startrow=start_row)
        
    print(f"Successfully added {len(new_bikes)} scrambler bikes.")

except Exception as e:
    print(f"Error: {e}")

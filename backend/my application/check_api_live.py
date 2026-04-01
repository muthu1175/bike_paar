import urllib.request
import json

try:
    with urllib.request.urlopen("http://127.0.0.1:8000/api/bikes/") as url:
        data = json.loads(url.read().decode())
        print(f"Total items: {len(data)}")
        print(f"Total items: {len(data)}")
        valid_count = 0
        for i, item in enumerate(data):
            if item.get('name') and item.get('price') and item.get('price') != 0:
                valid_count += 1
                if valid_count <= 5:
                    print(f"Valid Item {i}: {item['name']} - ₹{item['price']}")
        
        print(f"Total valid items: {valid_count}")
        if valid_count == 0:
             print("ALL ITEMS ARE INVALID/EMPTY!")
except Exception as e:
    print(f"Error checking API: {e}")
    print("Ensure server is running on port 8000")

import urllib.request
import json

BASE_URL = "http://127.0.0.1:8000/api/bikes"

ENDPOINTS = [
    f"{BASE_URL}/sports/",
    f"{BASE_URL}/scooters/",
    f"{BASE_URL}/cruisers/",
    f"{BASE_URL}/commuters/",
    f"{BASE_URL}/street/",
    f"{BASE_URL}/super/",
    f"{BASE_URL}/scramblers/",
    f"{BASE_URL}/adventure/",
    f"{BASE_URL}/tourer/",
]

for url in ENDPOINTS:
    print(f"Checking: {url}")
    try:
        with urllib.request.urlopen(url) as response:
            if response.getcode() == 200:
                data = json.loads(response.read().decode())
                print(f"  -> Success! Found {len(data)} bikes.")
                if len(data) == 0:
                    print(f"  -> WARNING: No bikes found for {url}")
            else:
                print(f"  -> FAILED: Status {response.getcode()}")
    except Exception as e:
        print(f"  -> ERROR: {e}")

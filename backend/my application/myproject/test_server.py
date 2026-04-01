import requests
import sys

# Try localhost
URL = "http://127.0.0.1:8000/api/bikes/above-30l/"

print(f"Testing URL: {URL}")

try:
    response = requests.get(URL)
    print(f"Status Code: {response.status_code}")
    
    if response.status_code == 200:
        data = response.json()
        print(f"Success! Found {len(data)} bikes.")
        if len(data) > 0:
            print(f"Sample: {data[0]['name']} - {data[0]['price']}")
    else:
        print("Failed!")
        print(response.text)

except requests.exceptions.ConnectionError:
    print("ERROR: Could not connect to server. Is it running?")
except Exception as e:
    print(f"ERROR: {e}")

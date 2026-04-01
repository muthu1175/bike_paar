import urllib.request
import urllib.error
import json

try:
    print("Testing API endpoint...")
    url = "http://127.0.0.1:8000/api/bikes/above-30l/"
    req = urllib.request.Request(url)
    with urllib.request.urlopen(req) as response:
        print(f"Status Code: {response.status}")
        data = response.read()
        print("Success! Data received.")
        print(str(data)[:100] + "...")
except urllib.error.HTTPError as e:
    print(f"Failed. HTTP Error: {e.code} {e.reason}")
    print(e.read().decode('utf-8'))
except Exception as e:
    print(f"Error: {e}")

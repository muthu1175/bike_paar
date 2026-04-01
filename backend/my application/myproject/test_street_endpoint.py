import urllib.request
import urllib.error
import json

try:
    print("Testing Street Bikes API endpoint...")
    url = "http://127.0.0.1:8000/api/bikes/street/"
    req = urllib.request.Request(url)
    with urllib.request.urlopen(req) as response:
        print(f"Status Code: {response.status}")
        data = response.read()
        print("Success! Data received.")
        try:
            json_data = json.loads(data)
            print(f"Count: {len(json_data)}")
            if len(json_data) > 0:
                print("First item sample:", json_data[0])
        except:
            print("Could not parse JSON")
            print(str(data)[:100] + "...")
except urllib.error.HTTPError as e:
    print(f"Failed. HTTP Error: {e.code} {e.reason}")
    print(e.read().decode('utf-8'))
except Exception as e:
    print(f"Error: {e}")

import urllib.request
import json

URLS = [
    "http://127.0.0.1:8000/api/bikes/above-30l/",
    "http://127.0.0.1:8000/api/search/?q=yamaha"
]

for URL in URLS:
    print(f"\nTesting URL: {URL}")
    try:
        with urllib.request.urlopen(URL) as response:
            status = response.getcode()
            print(f"Status Code: {status}")
            
            if status == 200:
                data = json.loads(response.read().decode())
                print(f"Success! Found {len(data)} bikes.")
                if len(data) > 0:
                    bike = data[0]
                    print(f"Sample: {bike.get('name')} - {bike.get('price')}")
                    
                    # Verify Dynamic Specs
                    print("Verifying Specs:")
                    specs = [
                        'max_power', 'kerb_weight', 'max_torque', 
                        'transmission', 'fuel_tank_capacity', 
                        'braking_system', 'top_speed'
                    ]
                    for spec in specs:
                        val = bike.get(spec)
                        print(f"  - {spec}: {val}")
                        if val is None:
                            print(f"    WARNING: {spec} is missing!")
            else:
                print("Failed with non-200 status.")

    except Exception as e:
        print(f"Error: {e}")

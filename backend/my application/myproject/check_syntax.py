import sys
import os
import django
from django.conf import settings

# Setup minimal Django settings to allow import
sys.path.append(os.path.dirname(os.path.abspath(__file__)))
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')

try:
    import bikepaar.views
    print("Syntax check passed: bikepaar.views imported successfully.")
except Exception as e:
    print(f"Syntax check failed: {e}")

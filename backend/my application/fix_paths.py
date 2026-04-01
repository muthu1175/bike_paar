import os
import glob
import re

app_root = r"f:\bikepaar\backend\my application"
py_files = glob.glob(os.path.join(app_root, "**", "*.py"), recursive=True)

pattern = re.compile(r'BASE_DIR\s*=\s*r"c:\\Users\\Muthusamy[^"]+"', re.IGNORECASE)
sys_path_pattern = re.compile(r"sys\.path\.append\(r'c:\\Users\\Muthusamy[^']+'\)", re.IGNORECASE)

for filepath in py_files:
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    new_content = content
    
    # Handle BASE_DIR = r"..."
    if pattern.search(new_content):
        # We need `import os` if it's not there. Most have it.
        if "import os" not in new_content:
            new_content = "import os\n" + new_content
            
        # If the file is in myproject, BASE_DIR should be os.path.join(os.path.dirname(__file__), "bikepaar")
        # If in my application, BASE_DIR should be os.path.join(os.path.dirname(__file__), "myproject", "bikepaar")
        if "myproject" in filepath:
            replacement = 'BASE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "bikepaar")'
        else:
            replacement = 'BASE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "myproject", "bikepaar")'
            
        new_content = pattern.sub(replacement, new_content)
        
    # Handle sys.path.append(r'...')
    if sys_path_pattern.search(new_content):
        if "import os" not in new_content:
            new_content = "import os\n" + new_content
        replacement = 'sys.path.append(os.path.dirname(os.path.abspath(__file__)))'
        new_content = sys_path_pattern.sub(replacement, new_content)

    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Fixed: {filepath}")

print("Done fixing paths.")

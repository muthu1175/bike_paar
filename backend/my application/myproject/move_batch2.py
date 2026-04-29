import shutil
import os

target_dir = r"f:\bikepaar\backend\my application\myproject\media\bikes"
source_dir = r"C:\Users\Muthusamy\.gemini\antigravity\brain\08069e4d-7579-4b3a-86ff-e0dc0d74b9c2"

mapping = {
    "re_bullet_500_1776490056633.png": "bullet 500.png",
    "re_bullet_650_1776490074522.png": "bullet 650.png",
    "honda_cb500f_1776490090150.png": "cb500f.png",
    "honda_cb500x_1776490108066.png": "cb500x.png",
    "honda_cbr150r_1776490124486.png": "cbr150r.png",
    "honda_cbr250r_1776490142557.png": "cbr250r.png"
}

for src, dst in mapping.items():
    src_path = os.path.join(source_dir, src)
    dst_path = os.path.join(target_dir, dst)
    if os.path.exists(src_path):
        print(f"Moving {src} to {dst}")
        shutil.copy2(src_path, dst_path)
    else:
        print(f"Source file not found: {src_path}")

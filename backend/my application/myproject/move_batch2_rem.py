import shutil
import os

target_dir = r"f:\bikepaar\backend\my application\myproject\media\bikes"
source_dir = r"C:\Users\Muthusamy\.gemini\antigravity\brain\08069e4d-7579-4b3a-86ff-e0dc0d74b9c2"

mapping = {
    "honda_cbr500r_v2_1776518228909.png": "cbr500r.png",
    "honda_cbr650f_v2_1776518247290.png": "cbr650f.png",
    "re_classic_500_es_v2_1776518267230.png": "classic 500 es.png",
    "re_classic_650_v2_1776518285307.png": "classic 650.png"
}

for src, dst in mapping.items():
    src_path = os.path.join(source_dir, src)
    dst_path = os.path.join(target_dir, dst)
    if os.path.exists(src_path):
        print(f"Moving {src} to {dst}")
        shutil.copy2(src_path, dst_path)
    else:
        print(f"Source file not found: {src_path}")

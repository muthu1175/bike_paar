import shutil
import os

target_dir = r"f:\bikepaar\backend\my application\myproject\media\bikes"
source_dir = r"C:\Users\Muthusamy\.gemini\antigravity\brain\2036d2e9-cf77-4a81-9333-d4b7ff2b082c"

mapping = {
    "himalayan_sleet_v1_1776658327075.png": "himalayan sleet.png",
    "honda_cb200x_v1_1776658355300.png": "honda cb200x.png",
    "honda_cbr650r_v1_1776658375041.png": "honda cbr650r.png",
    "honda_crf300l_v1_1776658404464.png": "honda crf300l.png",
    "honda_dream_neo_v1_1776658431311.png": "honda dream neo.png",
    "honda_hornet_2_0_v1_1776658471637.png": "honda hornet 20.png",
    "honda_sp_125_v1_1776658506831.png": "honda sp 125.png",
    "kawasaki_klx_230_v1_1776658531369.png": "kawasaki klx 230.png",
    "kawasaki_ninja_300_v1_1776658559838.png": "kawasaki ninja 300.png",
    "kawasaki_ninja_e_1_v1_1776658591260.png": "kawasaki ninja e1.png"
}

for src, dst in mapping.items():
    src_path = os.path.join(source_dir, src)
    dst_path = os.path.join(target_dir, dst)
    if os.path.exists(src_path):
        print(f"Moving {src} to {dst}")
        # Use copy2 then remove to simulate move across drives if necessary, 
        # but here we can just copy and then the user has them in media.
        shutil.copy2(src_path, dst_path)
    else:
        print(f"Source file not found: {src_path}")

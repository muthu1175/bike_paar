import shutil
import os

target_dir = r"f:\bikepaar\backend\my application\myproject\media\bikes"
source_dir = r"C:\Users\Muthusamy\.gemini\antigravity\brain\2036d2e9-cf77-4a81-9333-d4b7ff2b082c"

mapping = {
    "fascino_125_fi_v1_1776659215117.png": "fascino 125 fi hybrid.png",
    "rayzr_125_fi_v1_1776659235170.png": "rayzr 125 fi hybrid.png",
    "ntorq_race_edition_v1_1776659263473.png": "ntorq 125 race edition.png",
    "tvs_raider_125_v1_1776659300278.png": "raider 125 disc.png",
    "jawa_42_bobber_v1_1776659333911.png": "jawa bobber 300.png",
    "road_king_special_v1_1776659368386.png": "road king special.png",
    "suzuki_vstrom_650_v1_1776659404597.png": "suzuki vstrom 650.png"
}

for src, dst in mapping.items():
    src_path = os.path.join(source_dir, src)
    dst_path = os.path.join(target_dir, dst)
    if os.path.exists(src_path):
        print(f"Moving {src} to {dst}")
        shutil.copy2(src_path, dst_path)
    else:
        print(f"Source file not found: {src_path}")

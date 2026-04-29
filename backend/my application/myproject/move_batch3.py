import shutil
import os

target_dir = r"f:\bikepaar\backend\my application\myproject\media\bikes"
source_dir = r"C:\Users\Muthusamy\.gemini\antigravity\brain\08069e4d-7579-4b3a-86ff-e0dc0d74b9c2"

mapping = {
    "re_continental_gt_650_brg_1776518323896.png": "continental gt 650 brg.png",
    "ducati_panigale_v2_1776518345541.png": "ducati panigale v2.png",
    "electric_re_concept_1776518365190.png": "electric re.png",
    "yamaha_fz_25_1776518386461.png": "fz 25.png",
    "harley_davidson_fat_bob_v2_1776518407492.png": "harley davidson fat bob.png",
    "harley_livewire_v2_1776518427165.png": "harley livewire.png",
    "hero_glamour_v2_1776518443801.png": "hero glamour.png",
    "hero_impulse_v2_1776518459882.png": "hero impulse.png",
    "hero_splendor_plus_v2_1776518477751.png": "hero splendor plus.png",
    "hero_vida_v1_v2_1776518493920.png": "hero vida v1.png"
}

for src, dst in mapping.items():
    src_path = os.path.join(source_dir, src)
    dst_path = os.path.join(target_dir, dst)
    if os.path.exists(src_path):
        print(f"Moving {src} to {dst}")
        shutil.copy2(src_path, dst_path)
    else:
        print(f"Source file not found: {src_path}")

import shutil
import os

target_dir = r"f:\bikepaar\backend\my application\myproject\media\bikes"
source_dir = r"C:\Users\Muthusamy\.gemini\antigravity\brain\08069e4d-7579-4b3a-86ff-e0dc0d74b9c2"

mapping = {
    "aprilia_sr_160_1776489856777.png": "aprilia sr 160.png",
    "bajaj_avenger_220_1776489873286.png": "bajaj avenger 220.png",
    "bajaj_chetak_ev_1776489890032.png": "bajaj chetak ev.png",
    "bajaj_discover_125_1776489912270.png": "bajaj discover 125.png",
    "bajaj_platina_110_1776489928549.png": "bajaj platina 110.png",
    "bajaj_pulsar_150_1776489947589.png": "bajaj pulsar 150.png",
    "bajaj_pulsar_ns200_1776489965738.png": "bajaj pulsar ns200.png",
    "bmw_ce_04_1776489980150.png": "bmw ce 04.png",
    "bmw_g_310_gs_1776489998997.png": "bmw g 310 gs.png",
    "royal_enfield_classic_350_1776490014594.png": "royal enfield classic 350.png"
}

for src, dst in mapping.items():
    src_path = os.path.join(source_dir, src)
    dst_path = os.path.join(target_dir, dst)
    if os.path.exists(src_path):
        print(f"Moving {src} to {dst}")
        shutil.copy2(src_path, dst_path)
    else:
        print(f"Source file not found: {src_path}")

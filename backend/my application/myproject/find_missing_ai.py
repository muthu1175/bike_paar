import pandas as pd
import os

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
AI_EXCEL_PATH = os.path.join(BASE_DIR, "bikepaar", "ai", "Ai-bikedata.xlsx")
SEARCH_EXCEL_PATH = os.path.join(BASE_DIR, "bikepaar", "search_bikes.xlsx")
OUT_PATH = os.path.join(BASE_DIR, "missing_ai_bikes.txt")

def check_missing_data():
    df_ai = pd.read_excel(AI_EXCEL_PATH)
    df_ai.columns = df_ai.columns.str.lower().str.strip()
    
    df_search = pd.read_excel(SEARCH_EXCEL_PATH, header=1)
    df_search.columns = df_search.columns.str.lower().str.strip()
    
    ai_models = [str(x).lower().strip() for x in df_ai.get('bike_name', df_ai.get('model', []))]
    search_models = [str(x).lower().strip() for x in df_search.get('model', df_search.get('bike model', []))]
    
    missing_from_search = []
    
    for ai_model in ai_models:
        if ai_model == 'nan' or not ai_model:
            continue
        found = False
        for s_model in search_models:
            if ai_model in s_model:
                found = True
                break
        
        if not found:
            missing_from_search.append(ai_model)
            
    with open(OUT_PATH, 'w', encoding='utf-8') as f:
        f.write(f"Missing from search_bikes (Total {len(set(missing_from_search))}):\n\n")
        f.write("\n".join(sorted(list(set(missing_from_search)))))

check_missing_data()

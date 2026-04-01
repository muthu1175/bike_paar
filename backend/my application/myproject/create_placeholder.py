from PIL import Image, ImageDraw, ImageFont
import os

def create_placeholder(path):
    # Create a clean, modern placeholder
    width, height = 800, 600
    color_bg = (240, 240, 240) # Light Gray
    color_text = (100, 100, 100) # Dark Gray
    
    img = Image.new('RGB', (width, height), color=color_bg)
    d = ImageDraw.Draw(img)
    
    # Draw a border
    d.rectangle([10, 10, width-10, height-10], outline=color_text, width=5)
    
    # Try to load a font, fallback to default
    try:
        # Try a standard font
        font = ImageFont.truetype("arial.ttf", 60)
    except IOError:
        font = ImageFont.load_default()

    text_top = "IMAGE"
    text_bottom = "COMING SOON"
    
    # Calculate text size using textbbox (for newer Pillow) or textsize (older)
    try:
        left, top, right, bottom = d.textbbox((0, 0), text_top, font=font)
        w_top = right - left
        h_top = bottom - top
        
        left, top, right, bottom = d.textbbox((0, 0), text_bottom, font=font)
        w_bottom = right - left
        h_bottom = bottom - top
    except AttributeError:
        # Fallback for older Pillow versions
        w_top, h_top = d.textsize(text_top, font=font)
        w_bottom, h_bottom = d.textsize(text_bottom, font=font)

    # Center text
    x_top = (width - w_top) / 2
    y_top = (height - h_top) / 2 - 40
    
    x_bottom = (width - w_bottom) / 2
    y_bottom = (height - h_bottom) / 2 + 40

    d.text((x_top, y_top), text_top, fill=color_text, font=font)
    d.text((x_bottom, y_bottom), text_bottom, fill=color_text, font=font)
    
    # Ensure directory exists
    os.makedirs(os.path.dirname(path), exist_ok=True)
    
    img.save(path)
    print(f"Placeholder saved to {path}")

if __name__ == "__main__":
    BASE_DIR = os.path.dirname(os.path.abspath(__file__))
    SAVE_PATH = os.path.join(BASE_DIR, "media", "bikes", "placeholder.png")
    create_placeholder(SAVE_PATH)

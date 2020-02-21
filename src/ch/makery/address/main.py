try:
    from PIL import Image
except ImportError:
    import Image
import pytesseract

# If you don't have tesseract executable in your PATH, include the following:
# pytesseract.pytesseract.tesseract_cmd = r'<full_path_to_your_tesseract_executable>'
# Example tesseract_cmd = r'C:\Program Files (x86)\Tesseract-OCR\tesseract'
pytesseract.pytesseract.tesseract_cmd = r'C:\\Users\\techn\\eclipse-workspace\\TextCap5\\src\\ch\\makery\\address\\Tesseract-OCR\\tesseract'

# Simple image to string
result = pytesseract.image_to_string(Image.open('../../../../image.jpg'))

open("file.txt", "w").close()

textfile = open("file.txt", "w")

textfile.write(result)
textfile.close()
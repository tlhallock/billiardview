
from gopro import take_raw_photo
import cv2

'''
export PYTHONPATH="/home/thallock/Documents/pool/gopro-py-api"
'''

def test_take_a_picture():
  raw_path, persp_path, raw_image = take_raw_photo()
  print(raw_path)
  print(persp_path)
  cv2.imshow('the image', raw_image)
  cv2.waitKey(0)
  cv2.destroyAllWindows()

def test_calibration():
  pass

def test_reset():
  pass

def test_practice():
  pass


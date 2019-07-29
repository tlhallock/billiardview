
import cv2
import shutil
import os
import time

from undistort import undistort_image

from goprocam import GoProCamera
from goprocam import constants

'''
export PYTHONPATH="/home/thallock/Documents/pool/gopro-py-api"
'''

IMAGES_DIRECTORY = './saved_images/'


LAST_PHOTO = None

def format_current_time():
  current_time = time.localtime()
  return time.strftime('_%Y_%b_%d_%H_%M_%S', current_time)

def get_paths(original_filename):
  original = IMAGES_DIRECTORY + original_filename
  base = original[:-len('.png')] + format_current_time()
  return (base + '_raw.png', base + '_undistorted.png')


GOPRO = None
def take_raw_photo():
  global GOPRO
  if GOPRO is None:
    GOPRO = GoProCamera.GoPro()
    GOPRO.mode(constants.Mode.PhotoMode)

  photo_url = GOPRO.take_photo()
  photo_info = GOPRO.getInfoFromURL(photo_url)
  GOPRO.downloadMedia(folder=photo_info[0], file=photo_info[1])
  filename = photo_info[1]
  
  #######################################################################################################
  # Replace this
  # some_image = cv2.imread('/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/gopro/testing_4.png')
  # filename = 'tmp.png'
  # cv2.imwrite(filename, some_image)
  ########################################################################################################
  
  raw_path, persp_path = get_paths(filename)
  shutil.move(filename, raw_path)
  raw_image = cv2.imread(raw_path)
  return raw_path, persp_path, raw_image

def take_photo():
  global LAST_PHOTO
  _, persp_path, raw_image = take_raw_photo()
  undistorted = undistort_image(raw_image)
  cv2.imwrite(persp_path, undistorted)
  LAST_PHOTO = persp_path
  return persp_path, undistorted

def take_tmp_photo():
  raw_path, persp_path, raw_image = take_raw_photo()
  undistorted = undistort_image(raw_image)
  os.remove(raw_path)
  return undistorted

def get_photo(filepath):
  return cv2.imread(filepath)

def get_last_photo():
  if LAST_PHOTO is None:
    raise Exception('No last image currently available.')
  return get_photo(LAST_PHOTO)

def turn_off():
  pass

def turn_on():
  pass



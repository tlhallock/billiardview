
import cv2
import shutil
import os
import time
import numpy as np

from undistort import undistort_image

from goprocam import GoProCamera
from goprocam import constants

IMAGES_DIRECTORY = './saved_images/'


def _get_random_string(l=50):
  return ''.join([y for y in np.random.choice([x for x in 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'], l)])

def _format_current_time():
  current_time = time.localtime()
  return time.strftime('_%Y_%b_%d_%H_%M_%S', current_time)

def _get_paths(original_filename):
  original = IMAGES_DIRECTORY + original_filename
  base = original[:-len('.png')] + _format_current_time()
  return (base + '_raw.png', base + '_undistorted.png')

def _get_photo(filepath):
  return cv2.imread(filepath)


class GoProBase:
  def __init__(self):
    self.last_photo = None

  def take_raw_photo(self):
    filename = self.sub_take_raw_photo()
    raw_path, persp_path = _get_paths(filename)
    shutil.move(filename, raw_path)
    raw_image = cv2.imread(raw_path)
    return raw_path, persp_path, raw_image

  def take_photo(self):
    _, persp_path, raw_image = self.take_raw_photo()
    undistorted = undistort_image(raw_image)
    cv2.imwrite(persp_path, undistorted)
    self.last_photo = persp_path
    return persp_path, undistorted

  def take_tmp_photo(self):
    raw_path, persp_path, raw_image = self.take_raw_photo()
    undistorted = undistort_image(raw_image)
    os.remove(raw_path)
    return undistorted

  def get_last_photo(self):
    if self.last_photo is None:
      raise Exception('No last image currently available.')
    return _get_photo(self.last_photo)


class MockGoPro(GoProBase):
  def __init__(self, mock_image_paths):
    GoProBase.__init__(self)
    self.mock_image_paths = mock_image_paths
    self.mock_image_index = 0
  
  def sub_take_raw_photo(self):
    image_path = self.mock_image_paths[self.mock_image_index]
    self.mock_image_index = (self.mock_image_index + 1) % len(self.mock_image_paths)
    filename = _get_random_string() + '.png'
    cv2.imwrite(filename, cv2.imread(image_path))
    return filename

  def turn_off(self):
    pass

  def turn_on(self):
    pass


class RealGoPro(GoProBase):
  def __init__(self, gopro_ref):
    GoProBase.__init__(self)
    self.gopro = gopro_ref
  
  def sub_take_raw_photo(self):
    photo_url = self.gopro.take_photo()
    photo_info = self.gopro.getInfoFromURL(photo_url)
    self.gopro.downloadMedia(folder=photo_info[0], file=photo_info[1])
    filename = photo_info[1]
    return filename

  def turn_off(self):
    pass

  def turn_on(self):
    pass


def create_gopro(mock_image_paths):
  if mock_image_paths is None or len(mock_image_paths) == 0:
    gopro = GoProCamera.GoPro()
    gopro.mode(constants.Mode.PhotoMode)
    return RealGoPro(gopro)
  else:
    return MockGoPro(mock_image_paths)

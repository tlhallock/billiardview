
import time

from gopro import get_raw_photo

from audio import play_audio
from audio import AudioKeys


NUM_CALIBRATION_PHOTOS = 10

def calibrate():
  calibration_photos = []
  for i in range(NUM_CALIBRATION_PHOTOS):
    time.sleep(1)
    photo = get_raw_photo()
    calibration_photos.append(photo)
    play_audio(AudioKeys.CONFIRM_CALIBRATION_STEP)
  
  
      

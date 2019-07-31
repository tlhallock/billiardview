
import time

from audio import play_audio
from audio import AudioKeys

from calibration import save_calibration

NUM_CALIBRATION_PHOTOS = 1


def calibrate(gopro):
  calibration_photos = []
  for i in range(NUM_CALIBRATION_PHOTOS):
    time.sleep(1)
    _, _, photo = gopro.take_raw_photo()
    calibration_photos.append(photo)
    play_audio(AudioKeys.CONFIRM_CALIBRATION_STEP)
  
  save_calibration(calibration_photos[0], 'calibration_info.pickle')


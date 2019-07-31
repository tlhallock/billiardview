
from network import send_image
import time

from network import UiCommands


CURRENTLY_RESETTING = False

def save_checkpoint(gopro):
  gopro.take_photo()

def stop_resetting():
  global CURRENTLY_RESETTING
  CURRENTLY_RESETTING = False

def begin_reset(gopro):
  global CURRENTLY_RESETTING
  CURRENTLY_RESETTING = True
  
  checkpoint_image = gopro.get_last_photo()
  send_image(UiCommands.SEND_BASE_IMAGE, checkpoint_image)

  reset_count = 0
  while CURRENTLY_RESETTING and reset_count < 1000:
    next_image = gopro.take_tmp_photo()
    send_image(UiCommands.SEND_UPDATE_IMAGE, next_image)
    print('sent image')
    time.sleep(1)
    reset_count += 1

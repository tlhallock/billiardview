
from network import send_image
import time

from gopro import take_photo
from gopro import take_tmp_photo
from gopro import get_last_photo

from network import UiCommands


CURRENTLY_RESETTING = False

def save_checkpoint():
  take_photo()

def stop_resetting():
  global CURRENTLY_RESETTING
  CURRENTLY_RESETTING = False

def begin_reset():
  global CURRENTLY_RESETTING
  CURRENTLY_RESETTING = True
  
  checkpoint_image = get_last_photo()
  send_image(UiCommands.SEND_BASE_IMAGE, checkpoint_image)

  while CURRENTLY_RESETTING:
    next_image = take_tmp_photo()
    send_image(UiCommands.SEND_UPDATE_IMAGE, next_image)
    print('sent image')
    time.sleep(1)

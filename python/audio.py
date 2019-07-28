
import os

QUIET = True

AUDIO_DIRECTORY = './audio/'

class AudioKeys:
  COMMAND_PROMPT = 'prompt'
  MISUNDERSTOOD = 'idk'
  CONFIRM_RESET_BEGIN = 'begin_reset'
  CONFIRM_RESET_END = 'end_reset'
  CONFIRM_CHECKPOINT = 'checkpoint'
  CONFIRM_CALIBRATING_BEGIN = 'calibrate_begin'
  CONFIRM_CALIBRATION_STEP = 'calibrate_step'
  CONFIRM_CALIBRATION_END = 'calibrate_end'
  CONFIRM_QUIT = 'quiting'
  OK = 'ok'


def play_audio(audio_name):
  if QUIET:
    print(audio_name)
  else:
    path = AUDIO_DIRECTORY + audio_name + '.mp3'
    os.system('mpg321 ' + path)


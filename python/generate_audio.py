
from gtts import gTTS

from audio import AUDIO_DIRECTORY
from audio import AudioKeys


def save(path, text):
  print('writing "' + text + '"')
  tts = gTTS(text=text, lang='en')
  tts.save(AUDIO_DIRECTORY + path + '.mp3')
  
def generate_audio():
  save(AudioKeys.COMMAND_PROMPT, 'How can I help you?')
  save(AudioKeys.MISUNDERSTOOD, 'I am sorry, I did not understand that.')
  save(AudioKeys.CONFIRM_RESET_BEGIN, 'Ok, beginning reset.')
  save(AudioKeys.CONFIRM_RESET_END, 'Ending the reset.')
  save(AudioKeys.CONFIRM_CHECKPOINT, 'Ok, taking a checkpoint image.')
  save(AudioKeys.CONFIRM_CALIBRATING_BEGIN, 'Starting calibration.')
  save(AudioKeys.CONFIRM_CALIBRATION_STEP, 'Calibrating...')
  save(AudioKeys.CONFIRM_CALIBRATION_END, 'Done calibrating.')
  save(AudioKeys.CONFIRM_QUIT, 'Bye!')
  save(AudioKeys.OK, 'Ok.')

generate_audio()

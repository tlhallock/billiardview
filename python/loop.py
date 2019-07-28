import speech_recognition as sr

from dfs import Dfs
import time

MOCK_COMMANDS = [
  ('apothecary', 1),
  ('ubiquitous', 1),
  ('apothecary', 1),
  ('lackadaisical', 5),
  ('done', 4),
]


class MockMicrophone:
  def __init__(self, cmds):
    self.cmds = cmds
    self.idx = 0
  
  def next_cmd(self):
    cmd, wait_time = self.cmds[self.idx]
    time.sleep(wait_time)
    self.idx += 1
    return cmd

  def has_next(self):
    return self.idx < len(self.cmds)


def get_next_text(r, source):
  def l(p):
    while True:
      try:
        value = '{}'.format(r.recognize_google(r.listen(source)))
        if p is None:
          return value
        if p.search(value):
          return value
      except sr.UnknownValueError:
        print("Try again")
      except sr.RequestError as e:
        print("Try again")
  return l

def listen_for_commands():
  r = sr.Recognizer()
  m = sr.Microphone(device_index=8)
  
  dfs = Dfs()
  
  if MOCK_COMMANDS is not None:
    mic = MockMicrophone(MOCK_COMMANDS)
    while mic.has_next():
      mock_command = mic.next_cmd()
      print(mock_command)
      dfs.receive(mock_command, mic.next_cmd)
    return

  for index, name in enumerate(sr.Microphone.list_microphone_names()):
    print("Microphone with name \"{1}\" found for `Microphone(device_index={0})`".format(index, name))

  try:
    print("A moment of silence, please...")
    with m as source:
      r.adjust_for_ambient_noise(source)
      print("Set minimum energy threshold to {}".format(r.energy_threshold))
    while True:
      print("Listening...")
      with m as source:
        audio = r.listen(source)
        print("Processing...")
        try:
          value = '{}'.format(r.recognize_google(audio))
          print(value)
          if not dfs.receive(value, get_next_text(r, source)):
            break
        except sr.UnknownValueError:
          print("Oops! Didn't catch that")
        except sr.RequestError as e:
          print("Uh oh! Couldn't request results from Google Speech Recognition service; {0}".format(e))
  except KeyboardInterrupt:
    pass


'''

java:
	collect features options
	classify options
	show position
	
	
	
	
	gopro:
		record
		calibrate






implement paint where to leave it



implement pocket wall
implement clock
implement balls from spot

fix bug of not detecting some shots
fix bug of ghost shot off the board


detect balls



select shot
message shot result
turn try again on
turn try again off



calibrate table



implement pandora



'''

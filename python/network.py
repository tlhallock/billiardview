
import cv2
import io
import glob
import socket
import numpy as np
import time
from struct import pack

PORT_NUMBER = 8096

class UiCommands:
  UPDATE_BALL_LOCATIONS = b'\x01'
  
  SHOW_RESET_FRAME = b'\x02'
  SEND_BASE_IMAGE = b'\x03'
  SEND_UPDATE_IMAGE = b'\x04'
  HIDE_RESET_VIEW = b'\x05'
  
  BEGIN_PRACTICE = b'\x06'
  SHOW_POOL_TABLE = b'\x07'
  SHOW_SHOT_AIM = b'\x08'
  END_PRACTICE = b'\x09'
  
  SELECT_SHOT = b'\x0a'
  SELECT_SHOT_RESULT = b'\x0b'
  
  TURN_ON_TRY_AGAIN = b'\x0c'
  TURN_OFF_TRY_AGAIN = b'\x0d'


class ShotResult:
  POSSIBLE_RESULTS = [
    (0, 'perfect', 2),
    (1, 'ok', 2),
    (2, 'impossible', 2),
    (3, 'missed', 2),
    (4, 'perfect', 3),
    (5, 'barely', 3),
    (6, 'fluke', 3),
    (7, 'scratch', 3),
    (8, 'missed', 3),
    (9, 'horrible', 3),
  ]
  

def send_image(b, image):
  status, array = cv2.imencode('.png', image)
  try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  except socket.error as err:
    print("Unable to create socket")
  s.connect(("127.0.0.1", PORT_NUMBER))
  s.send(b)
  s.send(array.tostring())
  s.close()

def send_command(command):
  try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  except socket.error as err:
    print("Unable to create socket")
  s.connect(("127.0.0.1", PORT_NUMBER))
  s.send(command)
  s.close()

def send_locations(array):
  d = pack('>' + ' '.join(['d'] * array.shape[0] * array.shape[1]), *(arrayij for arrayi in array for arrayij in arrayi))
  try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  except socket.error as err:
    print("Unable to create socket")
  s.connect(("127.0.0.1", PORT_NUMBER))
  s.send(UiCommands.UPDATE_BALL_LOCATIONS)
  s.send(d)
  s.close()

def send_random_locations():
  balls = np.random.random((16, 2))@np.array([[46, 0], [0, 92]])
  send_locations(balls)

def send_shot_selection(ballNumber, pocketNumber):
  d = pack('>i i', ballNumber, pocketNumber)
  try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  except socket.error as err:
    print("Unable to create socket")
  s.connect(("127.0.0.1", PORT_NUMBER))
  s.send(UiCommands.SELECT_SHOT)
  s.send(d)
  s.close()

def send_shot_result(result):
  d = pack('>i', result)
  try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  except socket.error as err:
    print("Unable to create socket")
  s.connect(("127.0.0.1", PORT_NUMBER))
  s.send(UiCommands.SELECT_SHOT_RESULT)
  s.send(d)
  s.close()

def send_start_practice(typeNumber, numBalls, includeWalls):
  d = pack('>i i ?', typeNumber, numBalls, includeWalls)
  try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  except socket.error as err:
    print("Unable to create socket")
  s.connect(("127.0.0.1", PORT_NUMBER))
  s.send(UiCommands.BEGIN_PRACTICE)
  s.send(d)
  s.close()
  
  



import cv2

from loop import listen_for_commands
from dfs import Commands
import glob

'''

export PYTHONPATH="/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/billiardview/libs/gopro-py-api"


export PYTHONPATH="/home/thallock/Documents/pool/gopro-py-api"


'''


'''

  ('apothecary', 1),
  ('ubiquitous', 1),
  ('apothecary', 1),
  ('lackadaisical', 5),
  ('done', 4),
]

'''


#def test_take_a_picture():
  #raw_path, persp_path, raw_image = take_raw_photo()
  #print(raw_path)
  #print(persp_path)
  #cv2.imshow('the image', raw_image)
  #cv2.waitKey(0)
  #cv2.destroyAllWindows()


def test_calibration():
  to_undistort = glob.glob('/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/gopro/newpics/linear/GOPR[0-9]*.JPG')
  mock_image_paths = [
    '/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/gopro/newpics/calibration/GOPR0052.JPG',
  ] + to_undistort
  mock_commands = [
    (Commands.INIATE, 1),
    (Commands.CALIBRATE, 1),
  ]
  for _ in to_undistort:
    mock_commands.append((Commands.INIATE, 1))
    mock_commands.append((Commands.TAKE_CHECKPOINT, 1))
  mock_commands.append((Commands.INIATE, 1))
  mock_commands.append((Commands.QUIT, 1))

  listen_for_commands(mock_commands, mock_image_paths)
  

def test_reset():
  pass

def test_practice():
  pass



test_calibration()


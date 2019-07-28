
import numpy as np
import cv2
import pickle
import glob

def read():
  with open('calibration_info.pickle', 'rb') as info_file:
    lobj = pickle.load(info_file)

    return (
      lobj['h'],
      lobj['map1'],
      lobj['map2'],
      lobj['W'],
      lobj['H'],
    )


CALIBRATION_RESET_REQUIRED = True

h = None
map1 = None
map2 = None
W = None
H = None

def undistort_image(image):
  global CALIBRATION_RESET_REQUIRED
  global h
  global map1
  global map2
  global W
  global H
  if CALIBRATION_RESET_REQUIRED is None:
    raise Exception("Not calibrated")
  if h is None or CALIBRATION_RESET_REQUIRED:
    h, map1, map2, W, H = read()
  undistorted_img = cv2.remap(frame, map1, map2, interpolation=cv2.INTER_LINEAR, borderMode=cv2.BORDER_CONSTANT)
  fixed_image = cv2.warpPerspective(undistorted_img, h, (W, H))
  return fixed_image



'''
cap = cv2.VideoCapture('/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/raw/usable/GX010027.MP4')
total_frames = cap.get(cv2.CAP_PROP_FRAME_COUNT)
if (not cap.isOpened()):
	raise Exception("Unable to open file.")
print(total_frames)


index = 0
while cap.isOpened():
  ret, frame = cap.read()
  if not ret:
    continue
  print(index, index / total_frames)
  index += 1
  undistorted_img = cv2.remap(frame, map1, map2, interpolation=cv2.INTER_LINEAR, borderMode=cv2.BORDER_CONSTANT)
  fixed_image = cv2.warpPerspective(undistorted_img, h, (W, H))
  cv2.imwrite('./corrected/fixed_' + str(index) + '.png', fixed_image)

cap.release()


for idx, original_file in enumerate(glob.glob('/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/raw/usable/images/video_image_[0-9]*.png')):
  image = cv2.imread(original_file)
  undistorted_img = cv2.remap(image, map1, map2, interpolation=cv2.INTER_LINEAR, borderMode=cv2.BORDER_CONSTANT)
  fixed_image = cv2.warpPerspective(undistorted_img, h, (W, H))
  cv2.imwrite('./corrected/fixed_' + str(idx) + '.png', fixed_image)
  
  #output = fixed_image.copy()
  #gray = cv2.cvtColor(output, cv2.COLOR_BGR2GRAY)
  #circles = cv2.HoughCircles(gray,cv2.HOUGH_GRADIENT,1,20,param1=50,param2=30,minRadius=0,maxRadius=0)
  #if circles is None:
  #  continue
  #circles = np.round(circles[0, :]).astype("int")
  #circles = np.uint16(np.around(circles))
  #for i in circles[0,:]:
  #  # draw the outer circle
  #  cv2.circle(output,(i[0],i[1]),i[2],(0,255,0),2)
  #  # draw the center of the circle
  #  cv2.circle(output,(i[0],i[1]),2,(0,0,255),3)
  #cv2.imwrite("output/circled_" + str(idx) + ".png", output)
'''


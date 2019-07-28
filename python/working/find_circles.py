import numpy as np
import argparse
import cv2
import glob


def rev(a):
  return np.array([a[2], a[1], a[0]])

save_index = 0
for idx, original_file in enumerate(glob.glob("/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/keep/corrected/fixed_[0-9]*.png")):
  image = cv2.imread(original_file)
  mask = cv2.inRange(image, rev([0, 0, 0]), rev([67, 67, 67]))
  mask = cv2.blur(mask, (15, 15))
  cv2.imwrite('testing_' + str(idx) + '.png', mask)
  output = image.copy()
  cv2.imshow("mask", mask)
  cv2.waitKey(0)
  # gray = cv2.cvtColor(res, cv2.COLOR_BGR2GRAY)
  # make the minimum radius bigger....
  circles = cv2.HoughCircles(mask, cv2.HOUGH_GRADIENT, dp=0.1, minDist=2*35, param2=10, minRadius=35, maxRadius=45)
  if circles is not None:
    circles = np.round(circles[0, :]).astype("int")
    for (x, y, r) in circles:
      cv2.circle(output, (x, y), r, (0, 255, 0), 4)
      cv2.rectangle(output, (x - 5, y - 5), (x + 5, y + 5), (0, 128, 255), -1)
      
      cv2.imwrite('/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/gopro/some_balls/' + str(save_index) + '.png', image[y-r:y+r,x-r:x+r])
      save_index += 1
 
    cv2.imshow("output", output)
    cv2.waitKey(0)




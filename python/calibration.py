
import cv2
import numpy as np
import glob
import itertools
import pickle
from undistort import CALIBRATION_RESET_REQUIRED

dots = [
  cv2.imread('/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/gopro/undistorted/dots/' + str(i) + '.png')
  for i in range(14)
]

stickers = [
  cv2.imread('/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/gopro/undistorted/stickers/' + str(i) + '.png')
  for i in range(14)
]

def union(stickers1, stickers2):
  if stickers1 is None or len(stickers1) == 0:
    return stickers2
  elif stickers2 is None or len(stickers2) == 0:
    return stickers1
  ret = []
  for s in stickers1:
    if len(ret) > 0 and min(np.linalg.norm(np.array(ret) - s, axis=1)) < 50:
      continue
    ret.append(s)
  for s in stickers2:
    if len(ret) > 0 and min(np.linalg.norm(np.array(ret) - s, axis=1)) < 10:
      continue
    ret.append(s)
  return np.array(ret)

cart_to_homo = lambda img_coord: np.hstack((img_coord, np.ones((img_coord.shape[0], 1))))

homo_to_cart = lambda homo_coord: homo_coord[:, 0:2] / homo_coord[:, 2, None]

def create_homeography(fromCoord, toCoord):
  if fromCoord.shape[1] != 2 or toCoord.shape[1] != 2:
    raise Exception("Wrong dimension")
  if fromCoord.shape[0] != toCoord.shape[0]:
    raise Exception("Different number of points")
  h = np.linalg.lstsq(
    np.vstack([
      np.array([
        [p1[0], p1[1], 1, 0, 0, 0, -p1[0]*p2[0], -p1[1]*p2[0]],
        [0, 0, 0, p1[0], p1[1], 1, -p1[0]*p2[1], -p1[1]*p2[1]],
      ])
      for p1, p2 in zip(fromCoord, toCoord)
    ]),
    toCoord.reshape(2 * toCoord.shape[0]),
    rcond=None
  )[0]
  hmat = np.array([
    [h[0], h[1], h[2]],
    [h[3], h[4], h[5]],
    [h[6], h[7],    1],
  ])
  return lambda x: homo_to_cart((hmat@cart_to_homo(x).T).T)


original_file = '/media/thallock/1f0ab4b3-c472-49e1-92d8-c0b5664f7fdb/pool/raw/usable/images/video_image_0.png'
def save_calibration(original_file):
  DIM=(3840, 2160)
  K=np.array([[1941.662775454801, 0.0, 1928.2671227947174], [0.0, 1953.7483581582524, 1078.328660235385], [0.0, 0.0, 1.0]])
  D=np.array([[0.028583033212133114], [0.022017145724581813], [0.12299608165041478], [-0.17721206322389202]])

  dim2 = None
  dim3 = None
  balance = 0.8

  img = cv2.imread(original_file)
  dim1 = img.shape[:2][::-1]  #dim1 is the dimension of input image to un-distort
  assert dim1[0]/dim1[1] == DIM[0]/DIM[1], "Image to undistort needs to have same aspect ratio as the ones used in calibration"
  if not dim2:
    dim2 = dim1
  
  if not dim3:
    dim3 = dim1

  scaled_K = K * dim1[0] / DIM[0]  # The values of K is to scale with image dimension.
  scaled_K[2][2] = 1.0  # Except that K[2][2] is always 1.0
  # This is how scaled_K, dim2 and balance are used to determine the final K used to un-distort image.
  # OpenCV document failed to make this clear!
  new_K = cv2.fisheye.estimateNewCameraMatrixForUndistortRectify(scaled_K, D, dim2, np.eye(3), balance=balance)
  map1, map2 = cv2.fisheye.initUndistortRectifyMap(scaled_K, D, np.eye(3), new_K, dim3, cv2.CV_16SC2)
  undistorted_img = cv2.remap(img, map1, map2, interpolation=cv2.INTER_LINEAR, borderMode=cv2.BORDER_CONSTANT)

  boxed = undistorted_img

  current_stickers = None

  threshold = 0.8

  current_stickers = None
  for template in stickers:
    w, h = template.shape[1], template.shape[0]
    res = cv2.matchTemplate(boxed, template, cv2.TM_CCOEFF_NORMED)
    loc = np.where(res >= threshold)
      current_stickers = union(np.array([
      [pt[0] + w / 2, pt[1] + h / 2]
      for pt in zip(*loc[::-1])
    ]), current_stickers)

  corner_directions = np.array([
    [+1, +1, 0],
    [+1, -1, 0],
    [-1, +1, 0],
    [-1, -1, 0],
  ]) / np.sqrt(2)

  corner_locations = np.array([
    [1, 1],
    [1, 0],
    [0, 1],
    [0, 0],
  ])

  expected_locations = np.array([
    [0/6, 0/2],
    [1/6, 0/2],
    [5/6, 0/2],
    [6/6, 0/2],
    #
    [0/6, 1/2],
    [1/6, 1/2],
    [2/6, 1/2],
    [3/6, 1/2],
    [4/6, 1/2],
    [5/6, 1/2],
    [6/6, 1/2],
    #
    [0/6, 2/2],
    [1/6, 2/2],
    [5/6, 2/2],
    [6/6, 2/2],
  ])

  hs = cart_to_homo(current_stickers)
  corners = homo_to_cart(hs[np.argmax(hs@corner_directions.T, axis=0)])
  homo = create_homeography(corner_locations, corners)
  image_expected_locations = homo(expected_locations)
  closest_locations = np.array([
    current_stickers[np.argmin(np.linalg.norm(current_stickers - expected_sticker_location, axis=1))]
    for expected_sticker_location in image_expected_locations
  ])

  M = 30
  W = 99 * M
  H = 54 * M

  wb = 0.71
  hb = 0.48
  a1 = (np.array([[wb, 0],[0, hb]])@expected_locations.T).T + np.array([(1-wb) / 2, (1-hb) / 2])
  a2 = (np.array([[W, 0],[0, H]])@a1.T).T

  h, status = cv2.findHomography(closest_locations, a2)

 CALIBRATION_RESET_REQUIRED = True
  with open('calibration_info.pickle', 'wb') as info_file:
    pickle.dump(
      {
        'h': h,
        'map1': map1,
        'map2': map2,
        'W': W,
        'H': H,
      },
      info_file
    )

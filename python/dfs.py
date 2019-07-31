import threading
import sys

import speech_recognition as sr

import re

from audio import AudioKeys
from audio import play_audio

from reset_table import save_checkpoint
from reset_table import begin_reset
from reset_table import stop_resetting

from network import send_command
from network import ShotResult
from network import UiCommands

from calibrate import calibrate


# Rename to audio commands...
class Commands:
  INIATE = 'apothecary'
  RESET_BEGIN = 'lackadaisical'
  RESET_END = 'done'
  TAKE_CHECKPOINT = 'ubiquitous'
  NEVERMIND = 'nevermind'
  UPDATE_LOCATION = 'locations'
  
  SHOW_SHOT = 'shot'
  SHOW_BOARD = 'bored'
  SHOW_RESETTER = 'loquacious'
  
  CALIBRATE = 'calibrate'
  QUIT = 'corpulent'
  
  PRACTICE = 'practice'
  
  '''lackadaisical'''
  
  SELECT_SHOT = 'aaaaaaaaaaaaaaaaaaaaa'
  SET_RESULT = 'aaaaaaaaaaaaaaaaaaaaa'
  
  SET_TRY_AGAIN_OFF = 'aaaaaaaaaaaaaaaaaaaaa'
  SET_TRY_AGAIN_ON = 'aaaaaaaaaaaaaaaaaaaaa'


class States:
  LISTENING = 0
  TAKING_COMMAND = 1
  RESETING = 2
  PRACTICING = 3
  CALIBRATING = 4
  
  # SELECTING_SHOT
  # 


class Dfs:
  def __init__(self, gopro):
    self.state = States.LISTENING
    self.num_practice_balls = -1
    self.gopro = gopro
  
  def receive(self, text, get_next_text):
    if self.state == States.LISTENING:
      if Commands.INIATE in text:
        play_audio(AudioKeys.COMMAND_PROMPT)
        self.state = States.TAKING_COMMAND
      else:
        play_audio(AudioKeys.MISUNDERSTOOD)
        self.state = States.LISTENING
    elif self.state == States.TAKING_COMMAND:
      if Commands.RESET_BEGIN in text:
        play_audio(AudioKeys.CONFIRM_RESET_BEGIN)
        self.state = States.RESETING
        threading.Thread(target=begin_reset, args=(self.gopro,)).start()
      elif Commands.TAKE_CHECKPOINT in text:
        play_audio(AudioKeys.CONFIRM_CHECKPOINT)
        self.state = States.LISTENING
        save_checkpoint(self.gopro)
      elif Commands.NEVERMIND in text:
        play_audio(AudioKeys.OK)
        self.state = States.LISTENING
      elif Commands.SHOW_SHOT in text:
        play_audio(AudioKeys.OK)
        self.state = States.LISTENING
        send_command(UiCommands.SHOW_SHOT_AIM)
      elif Commands.SHOW_BOARD in text:
        play_audio(AudioKeys.OK)
        self.state = States.LISTENING
        send_command(UiCommands.SHOW_POOL_TABLE)
      elif Commands.SHOW_RESETTER in text:
        play_audio(AudioKeys.OK)
        self.state = States.LISTENING
        send_command(UiCommands.SHOW_RESET_FRAME)
      elif Commands.CALIBRATE in text:
        self.state = States.CALIBRATING
        play_audio(AudioKeys.CONFIRM_CALIBRATING_BEGIN)
        calibrate(self.gopro)
        play_audio(AudioKeys.CONFIRM_CALIBRATION_END)
        self.state = States.LISTENING
      elif Commands.PRACTICE in text:
        play_audio(AudioKeys.CONFIRM_PRACTICE)
        
        play_audio(AudioKeys.PROMPT_PRACTICE_TYPE)
        practice_type = get_next_text(re.compile('(random|spot|learning)'))
        
        include_walls = None
        num_practice_balls = None
        practice_type_num = -1
        
        if practice_type == 'random':
          practice_type_num = 0
          self.num_practice_balls = int(get_next_text(re.compile('\\d')))
        elif practice_type == 'spot':
          practice_type_num = 1
          include_walls = bool(get_next_text(re.compile('(true|false)')))
          self.num_practice_balls = int(get_next_text(re.compile('\\d')))
        elif practice_type == 'learning':
          practice_type_num = 2
        else:
          raise Exception('This should not be possible.')
        
        
        self.state = States.PRACTICE
      elif Commands.QUIT in text:
        play_audio(AudioKeys.CONFIRM_QUIT)
        self.state = States.LISTENING
        return False
      else:
        play_audio(AudioKeys.MISUNDERSTOOD)
        self.state = States.LISTENING
    elif self.state == States.RESETING:
      if Commands.RESET_END in text:
        play_audio(AudioKeys.CONFIRM_RESET_END)
        self.state = States.LISTENING
        stop_resetting()
      else:
        play_audio(AudioKeys.MISUNDERSTOOD)
        # self.state = States.LISTENING
    elif self.state == States.PRACTICING:
      if Commands.SELECT_SHOT in text:
        play_audio(AudioKeys.CONFIRM_SELECT_SHOT)
        ballNumber = int(get_next_text(re.compile('\\d')))
        pocketNumber = int(get_next_text(re.compile('\\d')))
        send_shot_selection(ballNumber, pocketNumber)
      elif Commands.SET_RESULT in text:
        play_audio(AudioKeys.CONFIRM_SET_RESULT)
        result = get_next_text("(" + "|".join([
          cmdText for cmd, cmdText, numBalls in ShotResult.POSSIBLE_RESULTS
          if numBalls == self.num_practice_balls
        ]) + ")")
        shotResultNums = [
          cmd for cmd, cmdText, numBalls in ShotResult.POSSIBLE_RESULTS
          if numBalls == self.num_practice_balls and cmdText == result.group()
        ]
        if len(shotResultNums) != 1:
          raise Exception("This should not happen")
        send_shot_result(shotResultNums[0])
      elif Commands.SET_TRY_AGAIN_OFF in text:
        play_audio(AudioKeys.CONFIRM_TRY_AGAIN_OFF)
        send_command(UiCommands.TURN_OFF_TRY_AGAIN)
      elif Commands.SET_TRY_AGAIN_ON in text:
        play_audio(AudioKeys.CONFIRM_TRY_AGAIN_ON)
        send_command(UiCommands.TURN_ON_TRY_AGAIN)
      elif Commands.QUIT_PRACTICE in text:
        play_audio(AudioKeys.CONFIRM_END_PRACTICE)
        send_command(UiCommands.END_PRACTICE)
        self.state = States.LISTENING
      else:
        play_audio(AudioKeys.MISUNDERSTOOD)
    else:
      raise Exception('Illegal state.')
    return True
      

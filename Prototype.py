"""
MDS - Multidisciplinary Capstone

Reach Services Communication Device

Beata Barati, Anne Boxeth, Jan Manganwang, Nick Samra
"""

import tkinter as tk
import vlc
from PIL import Image, ImageTk
import time
import alsaaudio
import RPi.GPIO as GPIO
import os.path
import sys
import threading

import subprocess
import shlex
import os
import signal
import io
import pygame

from collections import defaultdict
from bluetooth import *

# Server for BT service
global server

# Set up pin variables
global fan_pin
global motor_pin
fan_pin = 2
motor_pin = 4
motor_pin_2 = 5

# Ignore warnings cause they are annoying
GPIO.setwarnings(False) 

# Set pin layout, set pin 4 (vibration motor) to output
GPIO.setmode(GPIO.BCM)
GPIO.setup(motor_pin, GPIO.OUT)
GPIO.setup(motor_pin_2, GPIO.OUT)

# Set pin 2 (fan) to output
GPIO.setup(fan_pin, GPIO.OUT)

# Turn fan on
GPIO.output(fan_pin, 1)

# path for sounds
path = '/home/pi/python/sound'

# pygame to wait for click release
pygame.init()

# click events
def on_click_0(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '0.wav')
    sound.play()
# end def on_click_0

def on_click_1(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '1.wav')
    sound.play()
# end def on)click_1

def on_click_2(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '2.wav')
    sound.play()
# end def on_click_2

def on_click_3(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '3.wav')
    sound.play()
# end def on_click_3

def on_click_4(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '4.wav')
    sound.play()
# end def on_click_4

def on_click_5(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '5.wav')
    sound.play()
# end def on_click_5

def on_click_6(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '6.wav')
    sound.play()
# end def on_click_6

def on_click_7(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '7.wav')
    sound.play()
# end def on_click_7

def on_click_8(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '8.wav')
    sound.play()
# end def on_click_8

def on_click_9(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '9.wav')
    sound.play()
# end def on_click_9

def on_click_10(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '10.wav')
    sound.play()
# end def on_click_10

def on_click_11(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '11.wav')
    sound.play()
# end def on_click_11

def on_click_12(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '12.wav')
    sound.play()
# end def on_click_12

def on_click_13(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '13.wav')
    sound.play()
# end def on_click_13

def on_click_14(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '14.wav')
    sound.play()
# end def on_click_14

def on_click_15(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '15.wav')
    sound.play()
# end def on_click_15

def on_click_16(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '16.wav')
    sound.play()
# end def on_click_16

def on_click_17(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '17.wav')
    sound.play()
# end def on_click_17

def on_click_18(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '18.wav')
    sound.play()
# end def on_click_18

def on_click_19(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '19.wav')
    sound.play()
# end def on_click_19

def on_click_20(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '20.wav')
    sound.play()
# end def on_click_20

def on_click_21(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '21.wav')
    sound.play()
# end def on_click_21

def on_click_22(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '22.wav')
    sound.play()
# end def on_click_22

def on_click_23(event=None):
    ReachWindow.opt_features(app)
    sound = vlc.MediaPlayer(path + '23.wav')
    sound.play()
# end def on_click_23


# image dictionary
imgpath = '/home/pi/python/'
img_dict = {
    'bathroom' : {'img' : imgpath + 'bathroom.jpg', 'event' : on_click_0},
    'bed'      : {'img' : imgpath + 'bed.jpg', 'event' : on_click_1},
    'hotdog'   : {'img' : imgpath + 'hotdog.jpg', 'event' : on_click_2},
    'toy'      : {'img' : imgpath + 'toy.jpg', 'event' : on_click_3},
    'pens'     : {'img' : imgpath + 'pens.jpg', 'event' : on_click_4},
    'puppy'    : {'img' : imgpath + 'puppy.jpg', 'event' : on_click_5},
    'yard'     : {'img' : imgpath + 'yard.jpg', 'event' : on_click_6},
    'burger'   : {'img' : imgpath + 'burger.jpg', 'event' : on_click_7}
    }

class ReachWindow:
    def __init__(self, master):
        self.master = master
        self.frame = tk.Frame(self.master)
        self.frame.grid()
        
        # Empty dictionary for tiles
        self.Tiles = defaultdict(list)
        
        # Vibration & Music feedback
        self.vibrate = 1
        self.vibration_length = 1.5
        self.music = 0
        
        # Size
        self.current_size = 8
        
        # Configure the GUI
        self.configure_window(master)
        
        # Fullscreen setup
        self.master.attributes('-fullscreen', True)
        self.master.bind('<Escape>', self.fullscreen_off)
        self.master.bind('f', self.fullscreen_on)
        self.fullscreen_state = True
    # end def __init__
          
    def fullscreen_off(self, event = None):
        self.fullscreen_state = not self.fullscreen_state
        self.master.attributes('-fullscreen', self.fullscreen_state)
    # end def fullscreen_off

    def fullscreen_on(self, event = None):
        self.fullscreen_state = True
        self.master.attributes('-fullscreen', True)
    # end def fullscreen_on

    def opt_features(self):
        # Vibrate, if enabled
        if (self.vibrate == 1):
            GPIO.output(motor_pin, 1)
            GPIO.output(motor_pin_2, 1)
            time.sleep(self.vibration_length)
            GPIO.output(motor_pin, 0)
            GPIO.output(motor_pin_2, 0)
        # end if
        
        # Play music, if enabled
        if (self.music == 1):
            sound = vlc.MediaPlayer('/home/pi/python/jingle.mp3')
            sound.play()
        # end if
        while pygame.mouse.get_pressed()[0]:
            continue
        # end while
    # end def opt_features
    
    def configure_window(self, master):
    
        size = open('/home/pi/python/size.txt', 'r')
        img_count = int(size.read())
        size.close()
        
        labels = open('/home/pi/python/labels.txt', 'r')
        label_list = labels.readlines()
        # For each image
        for i in range(img_count):
            
            # Select label
            label = label_list[i].strip()
            
            # Determine gridding
            if (img_count == 4):
                row = 1
                column = i
                basewidth = 320
            elif (img_count == 8):
                if (i < 4):
                    row = 0
                    column = i
                else:
                    row = 1
                    column = i % 4
                basewidth = 320
            elif (img_count == 15):
                if (i < 5):
                    row = 0
                    column = i
                elif ((i > 5) and (i < 10)):
                    row = 2
                    column = i % 5
                else:
                    row = 3
                    column = i % 5
                basewidth = 256
            elif (img_count == 24):
                if (i < 6):
                    row = 0
                    column = i
                elif ((i > 6) and (i < 12)):
                    row = 1
                    column = i % 6
                elif ((i > 12) and (i < 18)):
                    row = 2
                    column = i % 6
                else:
                    row = 3
                    column = i % 6
                basewidth = 213
            # end if
            
            # Event dictionary (up to 24 pictures -> 24 events)
            event_dict = {
                 0 : on_click_0,
                 1 : on_click_1,
                 2 : on_click_2,
                 3 : on_click_3,
                 4 : on_click_4,
                 5 : on_click_5,
                 6 : on_click_6,
                 7 : on_click_7,
                 8 : on_click_8,
                 9 : on_click_9,
                10 : on_click_10,
                11 : on_click_11,
                12 : on_click_12,
                13 : on_click_13,
                14 : on_click_14,
                15 : on_click_15,
                16 : on_click_16,
                17 : on_click_17,
                18 : on_click_18,
                19 : on_click_19,
                20 : on_click_20,
                21 : on_click_21,
                22 : on_click_22,
                23 : on_click_23
                }
            
            # Resize image
            img = Image.open('/home/pi/python/img'+str(i)+'.jpg')
            wpercent = (basewidth/float(img.size[0]))
            hsize = int((float(img.size[1])*float(wpercent)))
            img = img.resize((basewidth, hsize), Image.ANTIALIAS)
            img.save('/home/pi/python/img'+str(i)+'.jpg')
            
            self.Tiles[i] = Tile(master, '/home/pi/python/img'+str(i)+'.jpg', label, '/home/pi/python/sound'+str(i)+'.wav', event_dict[i], row, column)
            # Increment counter
            i += 1
        # end for
        self.current_size = i
        labels.close()
    # end def configure_window

    def check_for_updates(self, master):

        # Audio object
        m = alsaaudio.Mixer('PCM')
        
        # Bluetooth communications
        data = server.execute()

        # Reset tiles
        for i in range(self.current_size):
            # Reset
            self.Tiles[i].label.grid_forget()
            self.Tiles[i].display.grid_forget()
        # end for
        
        # Extract data
        labels = data[0]
        new_volume = data[1]
        m.setvolume(new_volume)
        self.vibration = data[2]
        self.music = data[3]
        
        # Size
        current_size = len(labels)
        
        # Save size
        size = open('/home/pi/python/size.txt', 'w')
        size.write(str(len(labels)))
        size.close()
        
        # Open labels file for write
        label_file = open('/home/pi/python/labels.txt', 'w')
        
        # Reconfigure Tiles
        for i in range(len(labels)):
            
            # Image and sound file paths
            new_image = '/home/pi/python/img' + str(i) + '.jpg'
            new_sound = '/home/pi/python/sound' + str(i) + '.wav'

            # Read in new text
            text = labels[i]

            # Save labels
            label_file.write(text + '\n')

            # Determine gridding
            if (len(labels) == 4):
                row = 1
                column = i
                basewidth = 320
            elif (len(labels) == 8):
                if (i < 4):
                    row = 0
                    column = i
                else:
                    row = 1
                    column = i % 4
                basewidth = 320
            elif (len(labels) == 15):
                if (i < 5):
                    row = 0
                    column = i
                elif ((i > 5) and (i < 10)):
                    row = 2
                    column = i % 5
                else:
                    row = 3
                    column = i % 5
                basewidth = 256
            elif (len(labels) == 24):
                if (i < 6):
                    row = 0
                    column = i
                elif ((i > 6) and (i < 12)):
                    row = 1
                    column = i % 6
                elif ((i > 12) and (i < 18)):
                    row = 2
                    column = i % 6
                else:
                    row = 3
                    column = i % 6
                basewidth = 213
            # end if
            
            # Event dictionary (up to 24 pictures -> 24 events)
            event_dict = {
                 0 : on_click_0,
                 1 : on_click_1,
                 2 : on_click_2,
                 3 : on_click_3,
                 4 : on_click_4,
                 5 : on_click_5,
                 6 : on_click_6,
                 7 : on_click_7,
                 8 : on_click_8,
                 9 : on_click_9,
                10 : on_click_10,
                11 : on_click_11,
                12 : on_click_12,
                13 : on_click_13,
                14 : on_click_14,
                15 : on_click_15,
                16 : on_click_16,
                17 : on_click_17,
                18 : on_click_18,
                19 : on_click_19,
                20 : on_click_20,
                21 : on_click_21,
                22 : on_click_22,
                23 : on_click_23
                }
            
            # Resize image
            img = Image.open(new_image)
            wpercent = (basewidth/float(img.size[0]))
            hsize = int((float(img.size[1])*float(wpercent)))
            img = img.resize((basewidth, hsize), Image.ANTIALIAS)
            img.save(new_image)
            
            # Construct new tile
            self.Tiles[i] = Tile(master, new_image, text, new_sound, event_dict[i], row, column)
        # end for
        label_file.close()
        
        # Check 4 ever
        app.check_for_updates(master)

    # end def check_for_updates
# end class ReachWindow

class Tile:
    def __init__(self, master, image, display_text, sound, event, row, column):
        
        # Collect tile information
        self.master = master
        self.label = tk.Label
        self.image = image
        self.text = display_text
        self.sound = sound
        self.event = event
        self.row = row
        self.column = column
        self.display = None
        self.photo = None
        
        # Create the tile
        self.initialize_tile(master, self.image, self.text, self.sound, self.row, self.column, self.event)
    # end def __init__
        
    def initialize_tile(self, master, image, display_text, sound, row, column, event):
            
        load = Image.open(image)
        self.photo = ImageTk.PhotoImage(load)

        # Bind the image to a label
        self.label = tk.Label(master, image = self.photo)

        # Grid the Tile
        self.label.grid(row = row, column = column, columnspan = 1)
        
        # Bind a button click to the image's event
        self.label.bind('<Button-1>', event)
        
        # Create text display
        self.text = tk.StringVar()
        self.text.set(display_text)
        self.display = tk.Label(master, textvariable = self.text)
        
        # Bind each text label to the image, stretch it to the width of the image and sticky it to the bottom of the image
        self.display.grid(row = row, column = column, columnspan = 1, sticky = tk.E+tk.W+tk.S)
        
        # Set font attributes
        self.display.config(font='comicsans 24')
    # end def initialize_tile

# end class Tile

class CommunicationDeviceServer:

    def sendmsg(self, target, client_sock):
        print('got message ' + b"target".decode("utf-8"));


    def execute(self):
    # try to automatically make device bluetooth discoverable
        
        os.system("echo 'discoverable yes\npairable yes\nquit' | bluetoothctl")

        service_uuid = "00001101-0000-1000-8000-00805F9B34FB"

        server_sock = BluetoothSocket(RFCOMM)
        server_sock.bind(("", PORT_ANY))
        server_sock.listen(1)

        port = server_sock.getsockname()[1]
        
        # wait for bluetooth service to be available
        while True:
            try:
                advertise_service(server_sock, "ReachCommDevice", service_id = service_uuid, service_classes = [service_uuid, SERIAL_PORT_CLASS], profiles = [SERIAL_PORT_PROFILE])
                break
            except:
                time.sleep(1)
        print("awaiting RFCOMM connection on channel:%d" % port)

        #t_bt = threading.Thread(target = make_discoverable)
        #t_bt.start()

        client_sock, client_info = server_sock.accept()
        print("accepted connection from:", client_info)

        data_array = defaultdict(bytearray)
        sound_array = defaultdict(bytearray)
        label_array = defaultdict(str)
        images = defaultdict(list)
        sounds = defaultdict(list)
        volume = 10
        vibration = 0
        music = 0
        try:
            while True:
                # Receive the configuration data
                cfg = client_sock.recv(1)
                # Volume
                if (cfg == b'\x00'):
                    volume = int.from_bytes(client_sock.recv(4), byteorder = 'big')
                # Vibration
                elif (cfg == b'\x01'):
                    vibration = int.from_bytes(client_sock.recv(1), byteorder = 'big')
                # Music
                elif (cfg == b'\x02'):
                    music = int.from_bytes(client_sock.recv(1), byteorder = 'big')
                # End code
                elif (cfg == b'\x7F'):
                    break
                #end if
            # end while

            # Receive the size data
            rows = int.from_bytes(client_sock.recv(4), byteorder = 'big')
            columns = int.from_bytes(client_sock.recv(4), byteorder = 'big')
            for k in range(0, rows * columns):
                # get picture
                while True:
                    # Receive the picture data
                    data = client_sock.recv(1)
                    data_array[k].extend(data)
                    # *.jpg files end in \xFF \xD9
                    if (data == b'\xD9' and prev_data == b'\xFF'):
                        break
                    prev_data = data

                # end while
                print('got picture' + str(k))
                # get sound
                size = int.from_bytes(client_sock.recv(4), byteorder='big')
                bytesread = 0
                while True:
                    # If we haven't finished receiving the entire sound clip
                    if (bytesread < size):
                        sound = client_sock.recv(1)
                        bytesread += len(sound)
                        sound_array[k].extend(sound)
                    else:
                        break
                # end while
                print('got sound' + str(k))
                # get label
                size = int.from_bytes(client_sock.recv(4), byteorder='big')
                bytesread = 0
                while True:
                    # If we haven't finished receiving the entire label
                    if (bytesread < size):
                        label = client_sock.recv(1)
                        bytesread += len(label)
                        label_array[k] = label_array[k] + label.decode('utf-8')
                    else:
                        break
                # end while
                print('got label' + str(k))

                # Create files for the images and save
                images[k] = open('/home/pi/python/img'+str(k)+'.jpg', 'wb')
                images[k].write(bytearray(data_array[k]))
                images[k].close()
                print('wrote picture' + str(k))

                # Create files for the sounds and save
                sounds[k] = open('/home/pi/python/sound'+str(k)+'.wav', 'wb')
                sounds[k].write(sound_array[k])
                sounds[k].close()
                print('wrote sound' + str(k))
            # end for
        except IOError:
            pass

        # End socket connection
        print("disconnected")

        client_sock.close()
        server_sock.close()

        return label_array, 4 * int(volume), vibration, music


def img_to_array(img):
    with open(img, 'rb') as image_to_convert:
        img = image_to_convert.read()
        arr = bytearray(img)
    return arr
# end def img_to_array


def array_to_img(arr, filename):
    image_to_write = open('/home/pi/python/'+filename+'.jpg', 'wb')
    image_to_write.write(bytearray(arr))
    image_to_write.close()
    return open('/home/pi/python/img1.jpg')
# end def array_to_img
    
def make_discoverable():
    while True:
        os.system("echo 'discoverable yes\npairable yes\nquit' | bluetoothctl")
        time.sleep(62)
# end def make_discoverable

# do the things
if __name__ == '__main__':
    global root
    global app
    root = tk.Tk()
    app = ReachWindow(root)

    # Initialize bluetooth communications
    server = CommunicationDeviceServer()

    # First thread - check for updates from controller
    thread = threading.Thread(target = app.check_for_updates, args = (root,))
    root.after(5000, thread.start())
    # Start device GUI, maybe just make this blank initially?
    root.mainloop()
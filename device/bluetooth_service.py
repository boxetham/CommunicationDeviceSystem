#! /usr/bin/python

import time
import subprocess
import shlex
import os
import signal

from bluetooth import *

class CommunicationDeviceServer:

    def sendmsg(self, target, client_sock):
        print('got message ' + target);


    def execute(self):
    # try to automatically make device bluetooth discoverable
    #os.system("echo 'discoverable yes\npairable yes\nquit' | bluetoothctl")

        service_uuid = "00001101-0000-1000-8000-00805F9B34FB"

        server_sock = BluetoothSocket(RFCOMM)
        server_sock.bind(("", PORT_ANY))
        server_sock.listen(1)

        port = server_sock.getsockname()[1]

        advertise_service(server_sock, "CommunicationDevice", service_id = service_uuid, service_classes = [service_uuid, SERIAL_PORT_CLASS], profiles = [SERIAL_PORT_PROFILE])

        print("awaiting RFCOMM connection on channel:%d" % port)

        client_sock, client_info = server_sock.accept()
        print("accepted connection from:", client_info)

        try:
            while True:
                data = client_sock.recv(1024).strip()
                if len(data) == 0: break
                print("received [%s]" % data)
                client_sock.sendall('OK')
                self.sendmsg(data, client_sock)
        except IOError:
            pass

        print("disconnected")

        client_sock.close()
        server_sock.close()
        print("all done")

print 'start'

if __name__ == '__main__':
    server = CommunicationDeviceServer()
    server.execute()

print 'stop'
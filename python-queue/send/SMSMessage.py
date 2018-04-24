#-*- coding: utf8 -*-
import serial
import time

class PhoneMessage:

    
    def __init__(self, recipient='', message=''):
        self.recipient = recipient
        self.content = message
        
    def setRecipient(self, number):
        self.recipient = number

    def setContent(self, message):
        self.content = message
        
    def connectPhone(self):
        self.ser = serial.Serial('/dev/ttyAMA0', 115200, timeout=5)
        time.sleep(1)
        
    def sendMessage(self):
        self.ser.write('ATZ\r')
        time.sleep(1)
        self.ser.write('AT+CMGF=1\r')
        time.sleep(1)
        self.ser.write('AT+CMGS="' + self.recipient + '"\r')
        time.sleep(1)
        self.ser.write(self.content + '\r')
        time.sleep(1)
        self.ser.write(chr(26))
        time.sleep(1)
        
        print self.recipient
        print self.recipient
        
    def disconnectPhone(self):
        self.ser.close()


        
# sms = SMSMessage('18500183080', 'Hello 12131231 GGG')
# sms.connectPhone()
# sms.sendMessage()
# sms.disconnectPhone()
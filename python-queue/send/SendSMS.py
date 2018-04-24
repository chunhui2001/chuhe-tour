# -*- coding: utf-8 -*-
import time
from datetime import datetime
from dateutil.parser import parse
import tzlocal
import pytz
import delorean
import moment

import twilio

from twilio.rest import Client
from SMSMessage import PhoneMessage
from TimeHelper import Helper as th


class SMS():

    def __init__(self):
        pass
    
    
    def send(self, phone, content):
        sms = SMSMessage(phone, 'Hello 12131231 GGG')
        sms.connectPhone()
        sms.sendMessage()
        sms.disconnectPhone()
        
    def notify(self, phone, content):
        # send sms by twilio service
        client = Client("AC60be392e84c7586d1d83df41cdc6e5a3",
                        "17f5e4b75e0041b8fcdc60dc8f34865a")
        client.messages.create(to="+86 185 0018 3080",
                               from_="+19093036270 ", body=content)

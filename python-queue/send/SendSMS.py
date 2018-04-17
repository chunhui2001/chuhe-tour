
import twilio

from twilio.rest import Client


class SMS():
	
	def __init__(self):
		pass	

	def send(self, phone, content):
		# send sms by twilio service
		client = Client("AC60be392e84c7586d1d83df41cdc6e5a3", "17f5e4b75e0041b8fcdc60dc8f34865a")
		client.messages.create(to="+86 185 0018 3080", from_="+19093036270 ", body=content)

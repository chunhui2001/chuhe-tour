import json
import re
from email_validator import validate_email, EmailNotValidError
import phonenumbers
from phonenumbers import carrier, NumberParseException
from phonenumbers.phonenumberutil import number_type

class Notify():
	
	codeSign = None
	codeValue = None
	receiver = None
	channel = None
	expiredTime = None
	createdAt = None

	def __init__(self, messageStr):
		try:
			message = json.loads(messageStr)
			self.codeSign = message['code_sign']
			self.codeValue = message['code_value']
			self.receiver = message['receiver'].replace(' ', '')
			self.channel = message['send_channel']
            #self.expiredTime = message['expired_time']
            #self.createdAt = message['created_at']
		except TypeError as ex: 
			print type(ex).__name__ + ', ' + str(ex) 
		except KeyError as ex: 
			print type(ex).__name__ + ', ' + str(ex)
		else:
			self.channel = self.validateReceiver()

	def validateReceiver(self):
		email_match = re.search(r'\w+@\w+', self.receiver)
		phone_match_us = re.search(r'^(\d{3}-\d{3}-\d{4})$', self.receiver)
		phone_match_cn = re.search(r'^([1]+[^126]+\d{9})$', self.receiver.replace('+86',''))

		if email_match:
			return 'email'
		if phone_match_us or phone_match_cn:
			return 'phone'

		return None
	
	def notify(self):

		if not self.channel:
			return

		if self.channel == 'email':
			self.send_email()
			return

		#if self.channel == 'phone':
		#	self.send_sms()
		#	return

		print 'ERROR: not support send channel: ' + self.channel

	def send_email(self):
		print 'send email'
		print self.channel
		print self.codeSign
		print self.codeValue
		print self.receiver		

	def send_sms(self):
		print 'send sms'
		print self.channel
		print self.codeSign
		print self.codeValue
		print self.receiver		


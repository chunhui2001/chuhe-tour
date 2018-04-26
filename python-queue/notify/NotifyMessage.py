# -*- coding: UTF-8 -*-
import json
import re
from datetime import datetime
from dateutil.parser import parse
import tzlocal
import pandas as pd
from termcolor import colored

from send import SMS, Email

class Notify():
	
	codeSign = None
	codeValue = None
	receiver = None
	channel = None
	createdAt = None
	expiredTime = None
	expiredSeconds = None
	message = None
	local_timezone = tzlocal.get_localzone()

	def __init__(self, messageStr):

		print colored('INFO receiver a message: ' + str(messageStr), 'blue') 

		try:
			self.message = json.loads(messageStr)
			self.codeSign = self.message.get('code_sign', None)
			self.codeValue = self.message.get('code_value', None)
			self.receiver = self.message.get('receiver', None)
			self.channel = self.message.get('send_channel', None)
			self.expiredTime = self.message.get('expired_at', None)
			self.createdAt = self.message.get('created_at', None)
			self.expiredSeconds = self.message.get('expired_seconds', None)

			if not self.expiredSeconds:
				self.expiredSeconds = 60

			if self.expiredTime: 
				# self.expiredTime = datetime.strptime(self.expiredTime, '%Y-%m-%dT%H:%M:%S.%f')		#'2018-04-13T12:26:15.556'
				self.expiredTime = datetime.fromtimestamp(self.expiredTime/1000.0, self.local_timezone)


			if self.createdAt: 
				# self.createdAt = datetime.strptime(self.createdAt, '%Y-%m-%dT%H:%M:%S.%f')		#'2018-04-13T12:26:15.556'
				self.createdAt = datetime.fromtimestamp(self.createdAt/1000.0, self.local_timezone)

            # self.message.get('from', None)
			# self.message.get('subject', None)
		except TypeError as ex: 
			# print type(ex).__name__ + ', ' + str(ex) 
			pass
		except KeyError as ex: 
			# print type(ex).__name__ + ', ' + str(ex)
			pass
		else:
			self.channel = self.validateReceiver()

	def validateReceiver(self):

		if not self.receiver:
			return None

		email_match = re.search(r'\w+@\w+', self.receiver.replace(' ', ''))
		phone_match_us = re.search(r'^(\d{3}-\d{3}-\d{4})$', self.receiver.replace(' ', ''))
		phone_match_cn = re.search(r'^([1]+[^126]+\d{9})$', self.receiver.replace(' ', '').replace('+86',''))

		if email_match:
			return 'email'
		if phone_match_us or phone_match_cn:
			return 'phone'

		return None
	
	def notify(self):

		if not self.channel:
			print colored('ERROR: Send Channel is None, Ignore notifycation.', 'red') 
			print 
			return

		if self.channel == 'email':
			_from = self.message.get('from', None)
			_subject = self.message.get('subject', None)
			_content = '<h1>' + u'验证码: '.encode('utf-8') + '<span style="color:green;">' + str(self.codeValue) + '</span>, ' + u'请在 '.encode('utf-8') + '<span style="color:green;"> ' +  str(self.expiredSeconds) + ' </span>' + u'秒'.encode('utf-8') + '内输入</h1>'
			_content = _content + '<h3 style="font-weight:normal;margin:0;color:gray;">' + u'发送时间: <span style="color:burlywood;">'.encode('utf-8') + self.createdAt.strftime('%Y-%m-%d %H:%M:%S.%f%z (%Z)') + '</span></h3>' 
			_content = _content + '<h3 style="font-weight:normal;margin:0;color:gray;">' + u'效期时间: <span style="color:burlywood;">'.encode('utf-8') + self.expiredTime.strftime('%Y-%m-%d %H:%M:%S.%f%z (%Z)') + '</span></h3>' 
			Email().send(self.receiver, _content, _from, _subject)
			return

		if self.channel == 'phone':
			_content = 'Your code is: ' + str(self.codeValue) + ', available in: '.encode('utf-8') + str(self.expiredSeconds) + ' seconds'
			SMS().send(self.receiver, _content)
			_content = u'你的注册验证码: '.encode('utf-8') + str(self.codeValue) + u', 请在'.encode('utf-8') + str(self.expiredSeconds) + u'秒内输入'.encode('utf-8')
			SMS().notify(self.receiver, _content)
			return

		print 'ERROR: not support send channel: ' + self.channel
		
		

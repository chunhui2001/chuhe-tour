# -*- coding: UTF-8 -*-

import requests

# curl -s --user 'api:key-a5179b50c49cbbea7aedcf1b12165d70' \
#    https://api.mailgun.net/v3/mg.snnmo.com/messages \
#    -F from='Excited User <mailgun@mg.snnmo.com>' \
#    -F to=76920104@qq.com \
#    -F to=chunhui2001@gmail.com \
#    -F subject='Hello' \
#    -F text='Testing some Mailgun awesomeness!'

'''
import requests

key = 'YOUR API KEY HERE'
sandbox = 'YOUR SANDBOX URL HERE'
recipient = 'YOUR EMAIL HERE'

request_url = 'https://api.mailgun.net/v2/{0}/messages'.format(sandbox)
request = requests.post(request_url, auth=('api', key), data={
    'from': 'hello@example.com',
    'to': recipient,
    'subject': 'Hello',
    'text': 'Hello from Mailgun'
})

print 'Status: {0}'.format(request.status_code)
print 'Body:   {0}'.format(request.text)
'''

class Email():

	apiKey = None
	sandbox = None
	emailFrom = None
	emailSubject = None
	
	def __init__(self):
		self.sandbox = 'mg.snnmo.com'
		self.emailFrom = u'系统通知'.encode('utf-8') + ' <mailgun@mg.snnmo.com>'
		self.apiKey = 'key-a5179b50c49cbbea7aedcf1b12165d70'
		self.emailSubject = '系统通知'


	def send(self, receiver, content, _from, subject):

		if not receiver:
			print 'ERROR: Send email to: None'
			return

		print 'Send email to: ' + receiver

		request_url = 'https://api.mailgun.net/v3/{0}/messages'.format(self.sandbox)
		request = requests.post(request_url, auth=('api', self.apiKey), data = {
		    'from': _from or self.emailFrom,
		    'to':  receiver,
		    'subject': subject or self.emailSubject,
		    'html': content
		})

		print 'Status: {0}'.format(request.status_code)
		print 'Body:   {0}'.format(request.text)
		
		

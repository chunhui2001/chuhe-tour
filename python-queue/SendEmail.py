

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

	email_address = None
	content = None
	
	def __init__(self):
		self.email_address = None
		self.content = None

	def send(self, email, content):
		print 'send email'
		print email
		print content
		
		

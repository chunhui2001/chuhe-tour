

# curl -s --user 'api:key-a5179b50c49cbbea7aedcf1b12165d70' \
#    https://api.mailgun.net/v3/mg.snnmo.com/messages \
#    -F from='Excited User <mailgun@mg.snnmo.com>' \
#    -F to=76920104@qq.com \
#    -F to=chunhui2001@gmail.com \
#    -F subject='Hello' \
#    -F text='Testing some Mailgun awesomeness!'


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
		
		

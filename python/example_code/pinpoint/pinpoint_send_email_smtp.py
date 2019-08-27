# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http:#aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-sourcedescription:[pinpoint_send_email_smtp demonstrates how to send a transactional email by using the Amazon Pinpoint SMTP interface.]
# snippet-service:[mobiletargeting]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Amazon Pinpoint]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-20]
# snippet-sourceauthor:[AWS]
# snippet-start:[pinpoint.python.pinpoint_send_email_smtp.complete]

import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

# If you're using Amazon Pinpoint in a region other than US West (Oregon),
# replace email-smtp.us-west-2.amazonaws.com with the Amazon Pinpoint SMTP
# endpoint in the appropriate AWS Region.
HOST = "email-smtp.us-west-2.amazonaws.com"

# The port to use when connecting to the SMTP server.
PORT = 587

# Replace sender@example.com with your "From" address.
# This address must be verified.
SENDER = 'Mary Major <sender@example.com>'

# Replace recipient@example.com with a "To" address. If your account
# is still in the sandbox, this address has to be verified.
RECIPIENT  = 'recipient@example.com'

# CC and BCC addresses. If your account is in the sandbox, these
# addresses have to be verified.
CCRECIPIENT = "cc_recipient@example.com"
BCCRECIPIENT = "bcc_recipient@example.com"

# Replace smtp_username with your Amazon Pinpoint SMTP user name.
USERNAME_SMTP = "AKIAIOSFODNN7EXAMPLE"

# Replace smtp_password with your Amazon Pinpoint SMTP password.
PASSWORD_SMTP = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"

# (Optional) the name of a configuration set to use for this message.
# If you comment out this line, you also need to remove or comment out
# the "X-Pinpoint-CONFIGURATION-SET:" header below.
CONFIGURATION_SET = "ConfigSet"

# The subject line of the email.
SUBJECT = 'Amazon Pinpoint Test (Python smtplib)'

# The email body for recipients with non-HTML email clients.
BODY_TEXT = ("Amazon Pinpoint Test\r\n"
             "This email was sent through the Amazon Pinpoint SMTP "
             "Interface using the Python smtplib package."
            )

# Create a MIME part for the text body.
textPart = MIMEText(BODY_TEXT, 'plain')

# The HTML body of the email.
BODY_HTML = """<html>
<head></head>
<body>
  <h1>Amazon Pinpoint SMTP Email Test</h1>
  <p>This email was sent with Amazon Pinpoint using the
    <a href='https://www.python.org/'>Python</a>
    <a href='https://docs.python.org/3/library/smtplib.html'>
    smtplib</a> library.</p>
</body>
</html>
            """

# Create a MIME part for the HTML body.
htmlPart = MIMEText(BODY_HTML, 'html')

# The message tags that you want to apply to the email.
TAG0 = "key0=value0"
TAG1 = "key1=value1"

# Create message container. The correct MIME type is multipart/alternative.
msg = MIMEMultipart('alternative')

# Add sender and recipient addresses to the message
msg['From'] = SENDER
msg['To'] = RECIPIENT
msg['Cc'] = CCRECIPIENT
msg['Bcc'] = BCCRECIPIENT

# Add the subject line, text body, and HTML body to the message.
msg['Subject'] = SUBJECT
msg.attach(textPart)
msg.attach(htmlPart)

# Add  headers for configuration set and message tags to the message.
msg.add_header('X-SES-CONFIGURATION-SET',CONFIGURATION_SET)
msg.add_header('X-SES-MESSAGE-TAGS',TAG0)
msg.add_header('X-SES-MESSAGE-TAGS',TAG1)

# Open a new connection to the SMTP server and begin the SMTP conversation.
try:
    with smtplib.SMTP(HOST, PORT) as server:
        server.ehlo()
        server.starttls()
        #stmplib docs recommend calling ehlo() before and after starttls()
        server.ehlo()
        server.login(USERNAME_SMTP, PASSWORD_SMTP)
        #Uncomment the next line to send SMTP server responses to stdout
        #server.set_debuglevel(1)
        server.sendmail(SENDER, RECIPIENT, msg.as_string())
except Exception as e:
    print ("Error: ", e)
else:
    print ("Email sent!")

# snippet-end:[pinpoint.python.pinpoint_send_email_smtp.complete]

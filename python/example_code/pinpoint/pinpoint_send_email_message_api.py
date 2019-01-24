# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-sourcedescription:[pinpoint_send_email_message_api demonstrates how to send a transactional email by using the SendMessages operation in the Amazon Pinpoint API.]
# snippet-service:[mobiletargeting]
# snippet-keyword:[Python]
# snippet-keyword:[Amazon Pinpoint]
# snippet-keyword:[Code Sample]
# snippet-keyword:[SendMessages]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-20]
# snippet-sourceauthor:[AWS]
# snippet-start:[pinpoint.python.pinpoint_send_email_message_api.complete]

import boto3
from botocore.exceptions import ClientError

# The AWS Region that you want to use to send the email. For a list of
# AWS Regions where the Amazon Pinpoint API is available, see
# https://docs.aws.amazon.com/pinpoint/latest/apireference/
AWS_REGION = "us-west-2"

# The "From" address. This address has to be verified in
# Amazon Pinpoint in the region you're using to send email.
SENDER = "Mary Major <sender@example.com>"

# The addresses on the "To" line. If your Amazon Pinpoint account is in
# the sandbox, these addresses also have to be verified.
TOADDRESS = "recipient@example.com"

# The Amazon Pinpoint project/application ID to use when you send this message.
# Make sure that the email channel is enabled for the project or application
# that you choose.
APPID = "ce796be37f32f178af652b26eexample"

# The subject line of the email.
SUBJECT = "Amazon Pinpoint Test (SDK for Python (Boto 3))"

# The body of the email for recipients whose email clients don't support HTML
# content.
BODY_TEXT = """Amazon Pinpoint Test (SDK for Python)
-------------------------------------
This email was sent with Amazon Pinpoint using the AWS SDK for Python (Boto 3).
For more information, see https:#aws.amazon.com/sdk-for-python/
            """

# The body of the eamil for recipients whose email clients can display HTML
# content.
BODY_HTML = """<html>
<head></head>
<body>
  <h1>Amazon Pinpoint Test (SDK for Python)</h1>
  <p>This email was sent with
    <a href='https:#aws.amazon.com/pinpoint/'>Amazon Pinpoint</a> using the
    <a href='https:#aws.amazon.com/sdk-for-python/'>
      AWS SDK for Python (Boto 3)</a>.</p>
</body>
</html>
            """

# The character encoding that you want to use for the subject line and message
# body of the email.
CHARSET = "UTF-8"

# Create a new client and specify a region.
client = boto3.client('pinpoint',region_name=AWS_REGION)
try:
    response = client.send_messages(
        ApplicationId=APPID,
        MessageRequest={
            'Addresses': {
                TOADDRESS: {
                     'ChannelType': 'EMAIL'
                }
            },
            'MessageConfiguration': {
                'EmailMessage': {
                    'FromAddress': SENDER,
                    'SimpleEmail': {
                        'Subject': {
                            'Charset': CHARSET,
                            'Data': SUBJECT
                        },
                        'HtmlPart': {
                            'Charset': CHARSET,
                            'Data': BODY_HTML
                        },
                        'TextPart': {
                            'Charset': CHARSET,
                            'Data': BODY_TEXT
                        }
                    }
                }
            }
        }
    )
except ClientError as e:
    print(e.response['Error']['Message'])
else:
    print("Message sent! Message ID: "
            + response['MessageResponse']['Result'][TOADDRESS]['MessageId'])

# snippet-end:[pinpoint.python.pinpoint_send_email_message_api.complete]

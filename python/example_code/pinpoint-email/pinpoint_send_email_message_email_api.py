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

# snippet-sourcedescription:[pinpoint_send_email_message_email_api demonstrates how to send a transactional email message by using the SendEmail operation in the Amazon Pinpoint Email API.]
# snippet-service:[mobiletargeting]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Amazon Pinpoint Email API]
# snippet-keyword:[Code Sample]
# snippet-keyword:[SendEmail]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-20]
# snippet-sourceauthor:[AWS]
# snippet-start:[pinpoint.python.pinpoint_send_email_message_email_api.complete]

import boto3
from botocore.exceptions import ClientError

# The AWS Region that you want to use to send the email. For a list of
# AWS Regions where the Amazon Pinpoint Email API is available, see
# https://docs.aws.amazon.com/pinpoint-email/latest/APIReference
AWS_REGION = "us-west-2"

# The "From" address. This address has to be verified in
# Amazon Pinpoint in the region you're using to send email.
SENDER = "Mary Major <sender@example.com>"

# The addresses on the "To" line. If your Amazon Pinpoint account is in
# the sandbox, these addresses also have to be verified.
TOADDRESSES = ["recipient@example.com"]

# CC and BCC addresses. If your account is in the sandbox, these
# addresses have to be verified.
CCADDRESSES = ["cc_recipient1@example.com", "cc_recipient2@example.com"]
BCCADDRESSES = ["bcc_recipient@example.com"]

# The configuration set that you want to use to send the email.
CONFIGURATION_SET = "ConfigSet"

# The subject line of the email.
SUBJECT = "Amazon Pinpoint Test (SDK for Python)"

# The body of the email for recipients whose email clients don't support HTML
# content.
BODY_TEXT = """Amazon Pinpoint Test (SDK for Python)
-------------------------------------
This email was sent with Amazon Pinpoint using the AWS SDK for Python.
For more information, see https:#aws.amazon.com/sdk-for-python/
            """

# The body of the email for recipients whose email clients can display HTML
# content.
BODY_HTML = """<html>
<head></head>
<body>
  <h1>Amazon Pinpoint Test (SDK for Python)</h1>
  <p>This email was sent with
    <a href='https:#aws.amazon.com/pinpoint/'>Amazon Pinpoint</a> using the
    <a href='https:#aws.amazon.com/sdk-for-python/'>
      AWS SDK for Python</a>.</p>
</body>
</html>
            """

# The message tags that you want to apply to the email.
TAG0 = {'Name': 'key0', 'Value': 'value0'}
TAG1 = {'Name': 'key1', 'Value': 'value1'}

# The character encoding that you want to use for the subject line and message
# body of the email.
CHARSET = "UTF-8"

# Create a new Pinpoint resource and specify a region.
client = boto3.client('pinpoint-email', region_name=AWS_REGION)

# Send the email.
try:
    # Create a request to send the email. The request contains all of the
    # message attributes and content that were defined earlier.
    response = client.send_email(

        FromEmailAddress=SENDER,

        # An object that contains all of the email addresses that you want to
        # send the message to. You can send a message to up to 50 recipients in
        # a single call to the API.
        Destination={
            'ToAddresses': TOADDRESSES,
            'CcAddresses': CCADDRESSES,
            'BccAddresses': BCCADDRESSES
        },
        # The body of the email message.
        Content={
            # Create a new Simple message. If you need to include attachments,
            # you should send a RawMessage instead.
            'Simple': {
                'Subject': {
                    'Charset': CHARSET,
                    'Data': SUBJECT,
                },
                'Body': {
                    'Html': {
                        'Charset': CHARSET,
                        'Data': BODY_HTML
                    },
                    'Text': {
                        'Charset': CHARSET,
                        'Data': BODY_TEXT,
                    }
                }
            }
        },
        # The configuration set that you want to use when you send this message.
        ConfigurationSetName=CONFIGURATION_SET,
        EmailTags=[
            TAG0,
            TAG1
        ]
    )
# Display an error if something goes wrong.
except ClientError as e:
    print("The message wasn't sent. Error message: \"" + e.response['Error']['Message'] + "\"")
else:
    print("Email sent!")
    print("Message ID: " + response['MessageId'])
# snippet-end:[pinpoint.python.pinpoint_send_email_message_email_api.complete]

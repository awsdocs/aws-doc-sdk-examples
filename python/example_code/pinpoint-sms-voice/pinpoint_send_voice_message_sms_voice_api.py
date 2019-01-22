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

# snippet-sourcedescription:[pinpoint_send_voice_message_sms_voice_api demonstrates how to send a transactional voice message by using the SendVoiceMessage operation in the Amazon Pinpoint SMS and Voice API.]
# snippet-service:[Amazon Pinpoint]
# snippet-keyword:[Python]
# snippet-keyword:[Amazon Pinpoint SMS and Voice API]
# snippet-keyword:[Code Sample]
# snippet-keyword:[SendVoiceMessage]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-20]
# snippet-sourceauthor:[AWS]
# snippet-start:[pinpoint.python.pinpoint_send_voice_message_sms_voice_api.complete]

import boto3
from botocore.exceptions import ClientError

# The AWS Region that you want to use to send the voice message. For a list of
# AWS Regions where the Amazon Pinpoint SMS and Voice API is available, see
# https://docs.aws.amazon.com/pinpoint-sms-voice/latest/APIReference/
region = "us-east-1"

# The phone number that the message is sent from. The phone number that you
# specify has to be associated with your Amazon Pinpoint account. For best results, you
# should specify the phone number in E.164 format.
originationNumber = "+12065550110"

# The recipient's phone number. For best results, you should specify the phone
# number in E.164 format.
destinationNumber = "+12065550142"

# The language to use when sending the message. For a list of supported
# languages, see https://docs.aws.amazon.com/polly/latest/dg/SupportedLanguage.html
languageCode = "en-US"

# The Amazon Polly voice that you want to use to send the message. For a list
# of voices, see https://docs.aws.amazon.com/polly/latest/dg/voicelist.html
voiceId = "Matthew"

# The content of the message. This example uses SSML to customize and control
# certain aspects of the message, such as the volume or the speech rate.
# The message can't contain any line breaks.
ssmlMessage = ("<speak>"
               "This is a test message sent from <emphasis>Amazon Pinpoint</emphasis> "
               "using the <break strength='weak'/>AWS SDK for Python. "
               "<amazon:effect phonation='soft'>Thank you for listening."
               "</amazon:effect>"
               "</speak>")

# The phone number that you want to appear on the recipient's device. The phone
# number that you specify has to be associated with your Amazon Pinpoint account.
callerId = "+12065550199"

# The configuration set that you want to use to send the message.
configurationSet = "ConfigSet"

# Create a new SMS and Voice client and specify an AWS Region.
client = boto3.client('sms-voice',region_name=region)

try:
    response = client.send_voice_message(
        DestinationPhoneNumber = destinationNumber,
        OriginationPhoneNumber = originationNumber,
        CallerId = callerId,
        ConfigurationSetName = configurationSet,
        Content={
            'SSMLMessage':{
                'LanguageCode': languageCode,
                'VoiceId': voiceId,
                'Text': ssmlMessage
            }
        }
    )
# Display an error message if something goes wrong.
except ClientError as e:
    print(e.response['Error']['Message'])
# If the message is sent successfully, show the message ID.
else:
    print("Message sent!"),
    print("Message ID: " + response['MessageId'])

# snippet-end:[pinpoint.python.pinpoint_send_voice_message_sms_voice_api.complete]

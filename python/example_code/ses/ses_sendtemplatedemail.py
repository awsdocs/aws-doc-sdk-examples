# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You
# may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# or in the "license" file accompanying this file. This file is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
# ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.send_templated_email(
  Source='EMAIL_ADDRESS',
  Destination={
    'ToAddresses': [
      'EMAIL_ADDRESS',
    ],
    'CcAddresses': [
      'EMAIL_ADDRESS',
    ]
  },
  ReplyToAddresses=[
    'EMAIL_ADDRESS',
  ],
  Template='TEMPLATE_NAME',
  TemplateData='{ \"REPLACEMENT_TAG_NAME\":\"REPLACEMENT_VALUE\" }'
)

print(response)
 

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[ses_sendtemplatedemail.py demonstrates how to send a templated email to recipients.]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Simple Email Service]
# snippet-service:[ses]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-08-11]
# snippet-sourceauthor:[tapasweni-pathak]


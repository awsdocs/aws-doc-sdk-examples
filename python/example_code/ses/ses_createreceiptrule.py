# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

response = ses.create_receipt_rule(
  RuleSetName   = 'RULE_SET_NAME',
  Rule          = {
    'Name'      : 'RULE_NAME',
    'Enabled'   : True | False,
    'TlsPolicy' : 'Require' | 'Optional',
    'Recipients': [
      'DOMAIN | EMAIL_ADDRESS',
    ],
    'Actions'   : [
      {
        'S3Action'         : {
          'BucketName'     : 'S3_BUCKET_NAME',
          'ObjectKeyPrefix': 'email'
        }
      }
    ],
  }
)

print(response)
 

#snippet-sourceauthor: [jschwarzwalder]

#snippet-sourcedescription:[Description]

#snippet-service:[AWSService]

#snippet-sourcetype:[full example]

#snippet-sourcedate:[N/A]


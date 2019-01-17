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

# Create receipt filter
response = ses.create_receipt_filter(
  Filter = {
    'NAME'     : 'NAME',
    'IpFilter' : {
      'Cidr'   : 'IP_ADDRESS_OR_RANGE',
      'Policy' : 'Allow' | 'Block'
    }
  }
)

print(response)
 

#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[ses_createreceiptfilter.py demonstrates how to allow or block emails from a specific IP address.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon Simple Email Service]
#snippet-service:[ses]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-08-11]
#snippet-sourceauthor:[tapasweni-pathak]


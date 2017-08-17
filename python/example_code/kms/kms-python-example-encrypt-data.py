# Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

import boto3
import base64

# Encrypt data
#
# Replace the fictitious key ARN with a valid key ID

keyId = 'arn:aws:kms:us-west-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab'

text = '1234567890'
text_64 = base64.b64encode(text)
bytes_64 = bytearray()
bytes_64.extend(text)

client = boto3.client('kms')

response = client.encrypt(
    KeyId=keyId,
    Plaintext=bytes_64,
)

print(response)

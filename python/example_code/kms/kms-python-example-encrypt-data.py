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
import binascii

# Decrypt data
#
# Replace the fictitious blob with a valid blob

keyId = 'arn:aws:kms:us-west-2:188580781645:key/cd6b2911-8ed2-4783-87d9-499af9a13af4'

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

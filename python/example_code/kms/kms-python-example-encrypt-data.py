# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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


from __future__ import print_function
import base64

import boto3

# Encrypt data key

# Replace the fictitious key ARN with a valid key ID

key_id = 'arn:aws:kms:us-west-2:111122223333:key/0987dcba-09fe-87dc-65ba-ab0987654321'
region_name = 'us-west-2'

client = boto3.client('kms', region_name=region_name)

text = '1234567890'

response = client.encrypt(
    KeyId=key_id,
    Plaintext=text
)

print('Encrypted ciphertext:', response['CiphertextBlob'])
 

#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[kms-python-example-create-key.py demonstrates how to encrypt a string with an existing CMK using AWS Key Management Service.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS Key Management Service (KMS)]
#snippet-service:[kms]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-06-25]
#snippet-sourceauthor:[jschwarzwalder (AWS)]


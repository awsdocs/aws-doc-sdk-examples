# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
# snippet-start:[kms.python.decrypt.complete]

from __future__ import print_function

import boto3

# Decrypt blob

blob = b'\x01\x02\x02...'
region_name = 'us-west-2'

client = boto3.client('kms', region_name=region_name)

response = client.decrypt(
    CiphertextBlob=blob
)

print('Decrypted plaintext:', response['Plaintext'])
 
 
# snippet-end:[kms.python.decrypt.complete]
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[kms-python-example-decrypt-key.py  demonstrates how to retrieve the plain text of a previously encrypted text using AWS Key Management Service.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[AWS Key Management Service (KMS)]
# snippet-service:[kms]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-06-25]
# snippet-sourceauthor:[jschwarzwalder (AWS)]


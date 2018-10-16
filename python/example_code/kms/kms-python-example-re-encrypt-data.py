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

import boto3

# Re-encrypt data key

blob = b'\x01\x02\x02...'

# Replace the fictitious key ARN with a valid key ID

destination_key_id = 'arn:aws:kms:us-west-2:111122223333:key/0987dcba-09fe-87dc-65ba-ab0987654321'
region_name = 'us-west-2'

client = boto3.client('kms', region_name=region_name)

response = client.re_encrypt(
    CiphertextBlob=blob,
    DestinationKeyId=destination_key_id
)

print('New ciphertext:', response['CiphertextBlob'])
 

#snippet-sourcedescription:[kms-python-example-re-encrypt-key demonstrates how to re-encrypt plain text with the same CMK used to decrypt the original encrypted text.]
#snippet-keyword:[Python]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS Key Management Service (KMS)]
#snippet-service:[kms]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[]
#snippet-sourceauthor:[jschwarzwalder]


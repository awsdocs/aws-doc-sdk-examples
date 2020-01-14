# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
# 
# http://aws.amazon.com/apache2.0/
# 
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-sourcedescription:[get_session_token_with_mfa.py demonstrates how to call GetSessionToken with MFA authentication.]
# snippet-service:[iam]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS Identity and Access Management (IAM)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[GetSessionToken]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-22]
# snippet-sourceauthor:[stephswo (AWS)]
# snippet-start:[iam.python.get_session_token_with_mfa.complete]

import boto3

# Prompt for an MFA time-based one-time password (TOTP)
mfa_TOTP = input('Enter the MFA code: ')

# Create an STS client
sts_client = boto3.client('sts')

# Get temporary credentials for an AWS account or IAM user.
# Replace MFA_DEVICE with the virtual MFA device ID for the user. Alternatively, set the
# SerialNumber string to the serial number of the MFA hardware device, such as
# SerialNumber='GAHT12345678'.
response = sts_client.get_session_token(DurationSeconds=3600,   # Credentials valid for one hour
                                        SerialNumber='arn:aws:iam::MFA_DEVICE:mfa/user',
                                        TokenCode=mfa_TOTP)

# Reference the temporary credentials section of the response
tempCredentials = response['Credentials']

# Use the temporary credentials to create an S3 client that can access the
# external account.
s3_client = boto3.client('s3',
                         aws_access_key_id=tempCredentials['AccessKeyId'],
                         aws_secret_access_key=tempCredentials['SecretAccessKey'],
                         aws_session_token=tempCredentials['SessionToken'])

# Replace BUCKET_NAME with a bucket that exists on the external account
bucket_name = 'BUCKET_NAME'
# List the objects in the external account's bucket. The user's
# permissions must allow this type of S3 access.
try:
    response = s3_client.list_objects_v2(Bucket=bucket_name)
except Exception as e:
    print(f'ERROR: Could not find bucket {bucket_name}')
else:
    print(f'Objects in {bucket_name}')
    for obj in response['Contents']:
        print(f'   {obj["Key"]}')
# snippet-end:[iam.python.get_session_token_with_mfa.complete]

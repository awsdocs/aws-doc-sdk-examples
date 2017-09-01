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
from __future__ import print_function

import boto3

# Create a customer master key (CMK)
# Since we are only encrypting small amounts of data (4 KiB or less) directly,
# a CMK is fine for our purposes.
# For larger amounts of data,
# use the CMK to encrypt a data encryption key (DEK).
region_name = 'us-west-2'

client = boto3.client('kms', region_name=region_name)

response = client.create_key()

print('Created CMK ARN:', response['KeyMetadata']['Arn'])

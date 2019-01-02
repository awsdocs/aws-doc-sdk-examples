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
# snippet-start:[acm.python.list_tags_for_certificate.complete]

import boto3


# Create ACM client
acm = boto3.client('acm')

# List certificate tags.
response = acm.list_tags_for_certificate(
    CertificateArn='arn:aws:acm:region:123456789012:certificate/12345678-1234-1234-1234-123456789012'
)
for tag in response['Tags']:
    print(tag)
 
 
#snippet-end:[acm.python.list_tags_for_certificate.complete]
#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[list_tags_for_certificate.py demonstrates how to list the tags that have been applied to the AWS Certificate Manager certificate.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS Certificate Manager]
#snippet-service:[acm]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-12-26]
#snippet-sourceauthor:[walkerk1980]


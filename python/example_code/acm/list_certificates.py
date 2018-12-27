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
# snippet-start:[acm.python.list_certificates.complete]

import boto3


# Create ACM client
acm = boto3.client('acm')

# List certificates with the pagination interface
paginator = acm.get_paginator('list_certificates')
for response in paginator.paginate():
    for certificate in response['CertificateSummaryList']:
        print(certificate)
 
 
#snippet-end:[acm.python.list_certificates.complete]
#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[list_certificates.py demonstrates how to retrieve a list of certificate ARNs and domain names.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS Certificate Manager]
#snippet-service:[acm]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-12-26]
#snippet-sourceauthor:[walkerk1980]


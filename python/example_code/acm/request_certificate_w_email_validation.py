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
# snippet-start:[acm.python.certificate_w_email_validation.complete]

import boto3


# Create ACM client
acm = boto3.client('acm')

# Request a certificate with the specified Subjects and use EMAIL validation.
response = acm.request_certificate(
    DomainName='example.com',
    ValidationMethod='EMAIL',
    SubjectAlternativeNames=[
        '*.example.com',
        '*.subdomain1.example.com',
        '*.subdomain2.example.com',
    ],
    IdempotencyToken='Token201809031516',
    DomainValidationOptions=[
        {
            'DomainName': '*.subdomain1.example.com',
            'ValidationDomain': 'example.com'
        },
        {
            'DomainName': '*.subdomain2.example.com',
            'ValidationDomain': 'example.com'
        },
    ],
    Options={
        'CertificateTransparencyLoggingPreference': 'ENABLED'
    },
)

print(response)

 
 
#snippet-end:[acm.python.request_certificate_w_email_validation.complete]
#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[request_certificate_w_email_validation.py demonstrates how to request an AWS Certificate Manager certificate for use with other AWS services.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS Certificate Manager]
#snippet-service:[acm]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-12-26]
#snippet-sourceauthor:[walkerk1980]


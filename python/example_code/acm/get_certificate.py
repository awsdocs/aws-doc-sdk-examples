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

import boto3


# Create ACM client
acm = boto3.client('acm')

try:
    # Describe the specified certificate.
    response = acm.get_certificate(
        CertificateArn='arn:aws:acm:region:123456789012:certificate/12345678-1234-1234-1234-123456789012'
    )

    print('\n\rCertificate:\n\r')
    print(response['Certificate'])
    print('CertificateChain:\n\r')
    print(response['CertificateChain'])

except acm.exceptions.ResourceNotFoundException as e:
    print(e.response['Error']['Code'] + ': ' + e.response['Error']['Message'])
    exit(1)

except acm.exceptions.RequestInProgressException as e:
    print(e.response['Error']['Code'] + ': ' + e.response['Error']['Message'])
    exit(1)

except:
    print('There was an error.')
    exit(1)
 

#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[get_certificate.py demonstrates how to retrieve a certificate specified by an ARN and its certificate chain.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS Certificate Manager]
#snippet-service:[acm]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-09-05]
#snippet-sourceauthor:[walkerk1980]


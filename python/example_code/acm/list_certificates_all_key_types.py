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

# List certificates with the pagination interface
paginator = acm.get_paginator('list_certificates')
for response in paginator.paginate(
Includes={
        'extendedKeyUsage': [
            'TLS_WEB_SERVER_AUTHENTICATION','TLS_WEB_CLIENT_AUTHENTICATION','CODE_SIGNING','EMAIL_PROTECTION','TIME_STAMPING','OCSP_SIGNING','IPSEC_END_SYSTEM','IPSEC_TUNNEL','IPSEC_USER','ANY','NONE'
        ],
        'keyUsage': [
            'DIGITAL_SIGNATURE','NON_REPUDIATION','KEY_ENCIPHERMENT','DATA_ENCIPHERMENT','KEY_AGREEMENT','CERTIFICATE_SIGNING','CRL_SIGNING','ENCIPHER_ONLY','DECIPHER_ONLY','ANY','CUSTOM',
        ],
        'keyTypes': [
            'RSA_2048','RSA_1024','RSA_4096','EC_prime256v1','EC_secp384r1','EC_secp521r1'
        ]
    }
):
    for certificate in response['CertificateSummaryList']:
        print(certificate)
 

#snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
#snippet-keyword:[Python]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS Certificate Manager]
#snippet-service:[acm]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[]
#snippet-sourceauthor:[AWS]


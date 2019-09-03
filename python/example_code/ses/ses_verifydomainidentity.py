# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[ses_verifydomainidentity.py demonstrates how to add a domain to the list of identities for your Amazon SES account.]
# snippet-service:[ses]
# snippet-keyword:[Amazon Simple Email Service]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2018-08-11]
# snippet-sourceauthor:[tapasweni-pathak]

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

import boto3

# Add a domain to the list of identities in the AWS SES account.
# Also attempts to verify the domain.
ses = boto3.client('ses')
response = ses.verify_domain_identity(Domain='DOMAIN_NAME')
print(response)

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

from __future__ import print_function

import boto3

# Create CloudFront client
cf = boto3.client('cloudfront')

# List distributions
print("\nCloudFront Distributions:\n")
distributions=cf.list_distributions()

if distributions['DistributionList']['Quantity'] > 0:
    for distribution in distributions['DistributionList']['Items']:
        print("Domain: {}".format(distribution['DomainName']))
        print("Distribution Id: {}".format(distribution['Id']))
        print("Certificate Source: {}".format(distribution['ViewerCertificate']['CertificateSource']))

        if distribution['ViewerCertificate']['CertificateSource'] == "acm":
              print("Certificate: {}".format(distribution['ViewerCertificate']['Certificate']))

        print("")
else:
    print("Error - No CloudFront Distributions Detected.")

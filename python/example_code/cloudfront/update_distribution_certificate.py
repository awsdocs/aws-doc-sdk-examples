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
import sys

#support for python 2 and 3 input types
def read(output):
  if sys.version_info[0] < 3:
    return(raw_input(output))
  else:
    return(input(output))

# Create CloudFront client
cf = boto3.client('cloudfront')

# List distributions with the pagination interface
print("\nAvailable CloudFront Distributions:\n")
paginator = cf.get_paginator('list_distributions')
for distributionlist in paginator.paginate():
  for distribution in distributionlist['DistributionList']['Items']:
    print("Domain: " + distribution['DomainName'])
    print("Distribution Id: " + distribution['Id'])
    print("Certificate Source: " + distribution['ViewerCertificate']['CertificateSource'])
    if (distribution['ViewerCertificate']['CertificateSource'] == "acm"):
      print("Certificate ARN: " + distribution['ViewerCertificate']['Certificate'])
    print("")

print('Enter the Distribution Id of the CloudFront Distribution who\'s ACM \
Certificate you would like to replace. ')

distribution_id = read('Note that certificate source must be ACM - \
DistributionId: ')

distribution_config_response=cf.get_distribution_config(Id=distribution_id)
distribution_config=distribution_config_response['DistributionConfig']
distribution_etag=distribution_config_response['ETag']

if (distribution_config['ViewerCertificate']['CertificateSource'] != "acm"):
	print("\nThe DistributionId you have entered is not currently using \
an ACM Certificate, exiting...\n")
	exit()

old_cert_arn=distribution_config['ViewerCertificate']['ACMCertificateArn']

new_cert_arn=read("Please enter the ARN of the new ACM Certificate you would \
like to attach to Distribution " + distribution_id + ": ")

print("Replacing: " + old_cert_arn + "\nwith: " + new_cert_arn + "\n")

distribution_config['ViewerCertificate']['ACMCertificateArn']=new_cert_arn
distribution_config['ViewerCertificate']['Certificate']=new_cert_arn

cf.update_distribution(DistributionConfig=distribution_config, \
Id=distribution_id,IfMatch=distribution_etag)

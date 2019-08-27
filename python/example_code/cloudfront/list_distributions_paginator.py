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
# snippet-start:[cloudfront.python.list_distribution_certificate_paginator.complete]

import boto3


# Create CloudFront client
cf = boto3.client('cloudfront')

# List distributions with the pagination interface
print("\nCloudFront Distributions:\n")
paginator = cf.get_paginator('list_distributions')
for distributionlist in paginator.paginate():
  if distributionlist['DistributionList']['Quantity'] > 0:
    for distribution in distributionlist['DistributionList']['Items']:
      #print(distribution)
      print("Domain: " + distribution['DomainName'])
      print("Distribution Id: " + distribution['Id'])
      print("Certificate Source: " + distribution['ViewerCertificate']['CertificateSource'])
      if (distribution['ViewerCertificate']['CertificateSource'] == "acm"):
        print("Certificate ARN: " + distribution['ViewerCertificate']['Certificate'])
      print("")
  else:    
    print("Error - No CloudFront Distributions Detected.") 
 
# snippet-end:[cloudfront.python.list_distribution_certificate_paginator.complete]
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[list_distributions_paginator.py demonstrates how to list Amazon CloudFront distributions with the pagination interface.]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon CloudFront]
# snippet-service:[cloudfront]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-12-26]
# snippet-sourceauthor:[walkerk1980]

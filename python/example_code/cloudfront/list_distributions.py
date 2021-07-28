# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0


# snippet-start:[python.cloudfront.example_code.list_distributions]
import boto3


def main(cloudfront_client):
    print("CloudFront distributions:\n")
    distributions = cloudfront_client.list_distributions()
    if distributions['DistributionList']['Quantity'] > 0:
        for distribution in distributions['DistributionList']['Items']:
            print(f"Domain: {distribution['DomainName']}")
            print(f"Distribution Id: {distribution['Id']}")
            print(f"Certificate Source: "
                  f"{distribution['ViewerCertificate']['CertificateSource']}")
            if distribution['ViewerCertificate']['CertificateSource'] == "acm":
                print(f"Certificate: {distribution['ViewerCertificate']['Certificate']}")
            print("")
    else:
        print("No CloudFront distributions detected.")


if __name__ == '__main__':
    main(boto3.client('cloudfront'))
# snippet-end:[python.cloudfront.example_code.list_distributions]

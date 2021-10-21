# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon CloudFront to
perform distribution operations.
"""

import boto3


# snippet-start:[python.example_code.cloudfront.CloudFrontWrapper]
class CloudFrontWrapper:
    """Encapsulates Amazon CloudFront operations."""
    def __init__(self, cloudfront_client):
        """
        :param cloudfront_client: A Boto3 CloudFront client
        """
        self.cloudfront_client = cloudfront_client
# snippet-end:[python.example_code.cloudfront.CloudFrontWrapper]

# snippet-start:[python.example_code.cloudfront.ListDistributions]
    def list_distributions(self):
        print("CloudFront distributions:\n")
        distributions = self.cloudfront_client.list_distributions()
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
# snippet-end:[python.example_code.cloudfront.ListDistributions]

# snippet-start:[python.example_code.cloudfront.UpdateDistribution]
    def update_distribution(self):
        distribution_id = input(
            "This script updates the comment for a CloudFront distribution.\n"
            "Enter a CloudFront distribution ID: ")

        distribution_config_response = self.cloudfront_client.get_distribution_config(
            Id=distribution_id)
        distribution_config = distribution_config_response['DistributionConfig']
        distribution_etag = distribution_config_response['ETag']

        distribution_config['Comment'] = input(
            f"\nThe current comment for distribution {distribution_id} is "
            f"'{distribution_config['Comment']}'.\n"
            f"Enter a new comment: ")
        self.cloudfront_client.update_distribution(
            DistributionConfig=distribution_config, Id=distribution_id,
            IfMatch=distribution_etag)
        print("Done!")
# snippet-end:[python.example_code.cloudfront.UpdateDistribution]


def main():
    cloudfront = CloudFrontWrapper(boto3.client('cloudfront'))
    cloudfront.list_distributions()
    cloudfront.update_distribution()


if __name__ == '__main__':
    main()

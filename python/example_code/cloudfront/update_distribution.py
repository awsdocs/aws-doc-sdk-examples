# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.cloudfront.example_code.update_distribution]
import boto3


def main(cloudfront_client):
    distribution_id = input(
        "This script updates the comment for a CloudFront distribution.\n"
        "Enter a CloudFront distribution ID: ")

    distribution_config_response = cloudfront_client.get_distribution_config(
        Id=distribution_id)
    distribution_config = distribution_config_response['DistributionConfig']
    distribution_etag = distribution_config_response['ETag']

    distribution_config['Comment'] = input(
        f"\nThe current comment for distribution {distribution_id} is "
        f"'{distribution_config['Comment']}'.\n"
        f"Enter a new comment: ")
    cloudfront_client.update_distribution(
        DistributionConfig=distribution_config, Id=distribution_id,
        IfMatch=distribution_etag)
    print("Done!")


if __name__ == '__main__':
    main(boto3.client('cloudfront'))
# snippet-end:[python.cloudfront.example_code.update_distribution]

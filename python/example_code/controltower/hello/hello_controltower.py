# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.controltower.Hello]
import boto3


def hello_controltower(controltower_client):
    """
    Use the AWS SDK for Python (Boto3) to create an AWS Control Tower client
    and list all available baselines.
    This example uses the default settings specified in your shared credentials
    and config files.

    :param controltower_client: A Boto3 AWS Control Tower Client object. This object wraps
                               the low-level AWS Control Tower service API.
    """
    print("Hello, AWS Control Tower! Let's list available baselines:\n")
    paginator = controltower_client.get_paginator("list_baselines")
    page_iterator = paginator.paginate()

    baseline_names: [str] = []
    try:
        for page in page_iterator:
            for baseline in page['baselines']:
                baseline_names.append(baseline['name'])

        print(f"{len(baseline_names)} baseline(s) retrieved.")
        for baseline_name in baseline_names:
            print(f"\t{baseline_name}")

    except controltower_client.exceptions.AccessDeniedException:
        print("Access denied. Please ensure you have the necessary permissions.")
    except Exception as e:
        print(f"An error occurred: {str(e)}")


if __name__ == "__main__":
    hello_controltower(boto3.client("controltower"))
# snippet-end:[python.example_code.controltower.Hello]

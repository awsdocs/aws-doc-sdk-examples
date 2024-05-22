# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import boto3
import os
import random
import string
import yaml


def create_bucket_name(bucket_prefix):
    """
    Generate a unique name for an S3 bucket based on a prefix.

    Args:
    bucket_prefix (str): Prefix for the bucket name to ensure uniqueness.

    Returns:
    str: A unique bucket name.
    """
    # Append a random string to the bucket prefix to ensure uniqueness
    random_suffix = "".join(
        random.choices(string.ascii_lowercase + string.digits, k=16)
    )
    return bucket_prefix + random_suffix


def create_bucket(bucket_prefix, s3_client):
    """
    Create an S3 bucket in the region of the provided S3 client.

    Args:
    bucket_prefix (str): Prefix to help generate the bucket name.
    s3_client (boto3.Client): An initialized boto3 S3 client object.

    Returns:
    str: The name of the created bucket.
    """
    bucket_name = create_bucket_name(bucket_prefix)
    s3_client.create_bucket(Bucket=bucket_name)
    print("Bucket created:", bucket_name)
    return bucket_name


def upload_file_to_bucket(bucket_name, file_path, s3_client):
    """
    Upload a file to an S3 bucket.

    Args:
    bucket_name (str): The name of the bucket to which the file is uploaded.
    file_path (str): Path to the file to upload.
    s3_client (boto3.Client): An initialized boto3 S3 client object.

    Returns:
    None: Outputs to the console the name of the file uploaded.
    """
    file_name = os.path.basename(file_path)
    s3_client.upload_file(file_path, bucket_name, file_name)
    print("File uploaded:", file_name)


def write_bucket_name_to_yaml(bucket_name, file_path="../config.yaml"):
    """
    Write the bucket name to a YAML file.

    Args:
    bucket_name (str): The name of the bucket to store in the YAML file.
    file_path (str): Path to the YAML file to write.

    Returns:
    None: Outputs to the console the path to the YAML file where the bucket name was written.
    """
    new_values = {"bucket_name": bucket_name, "file_name": "einstein_resume.pdf"}
    if os.path.exists(file_path):
        with open(file_path, "r") as file:
            existing_data = yaml.safe_load(file)
            if existing_data is None:
                existing_data = {}
            existing_data.update(new_values)
        with open(file_path, "w") as file:
            yaml.dump(existing_data, file)
        print("Values updated and written to YAML file:", file_path)
    else:
        with open(file_path, "w") as file:
            yaml.dump(new_values, file)
        print("New YAML file created with values:", file_path)


def main():
    """
    Main function to execute the S3 bucket operations.
    """
    s3_client = boto3.client("s3")
    bucket_name = create_bucket("example-", s3_client)
    upload_file_to_bucket(bucket_name, "einstein_resume.pdf", s3_client)
    write_bucket_name_to_yaml(bucket_name)


if __name__ == "__main__":
    main()

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use AWS SDK for Python (Boto3) to get started using Amazon Simple Storage
Service (Amazon S3). Create a bucket, move objects into and out of it, and delete all
resources at the end of the demo.

This example follows the steps in "Getting started with Amazon S3" in the Amazon S3
user guide.
    - https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html
"""

# snippet-start:[python.example_code.s3.Scenario_GettingStarted]
import io
import os
import uuid

import boto3
from boto3.s3.transfer import S3UploadFailedError
from botocore.exceptions import ClientError


def do_scenario(s3_resource):
    print('-'*88)
    print("Welcome to the Amazon S3 getting started demo!")
    print('-'*88)

    bucket_name = f'doc-example-bucket-{uuid.uuid4()}'
    bucket = s3_resource.Bucket(bucket_name)
    try:
        bucket.create(
            CreateBucketConfiguration={
                'LocationConstraint': s3_resource.meta.client.meta.region_name})
        print(f"Created demo bucket named {bucket.name}.")
    except ClientError as err:
        print(f"Tried and failed to create demo bucket {bucket_name}.")
        print(f"\t{err.response['Error']['Code']}:{err.response['Error']['Message']}")
        print(f"\nCan't continue the demo without a bucket!")
        return

    file_name = None
    while file_name is None:
        file_name = input("\nEnter a file you want to upload to your bucket: ")
        if not os.path.exists(file_name):
            print(f"Couldn't find file {file_name}. Are you sure it exists?")
            file_name = None

    obj = bucket.Object(os.path.basename(file_name))
    try:
        obj.upload_file(file_name)
        print(f"Uploaded file {file_name} into bucket {bucket.name} with key {obj.key}.")
    except S3UploadFailedError as err:
        print(f"Couldn't upload file {file_name} to {bucket.name}.")
        print(f"\t{err}")

    answer = input(f"\nDo you want to download {obj.key} into memory (y/n)? ")
    if answer.lower() == 'y':
        data = io.BytesIO()
        try:
            obj.download_fileobj(data)
            data.seek(0)
            print(f"Got your object. Here are the first 20 bytes:\n")
            print(f"\t{data.read(20)}")
        except ClientError as err:
            print(f"Couldn't download {obj.key}.")
            print(f"\t{err.response['Error']['Code']}:{err.response['Error']['Message']}")

    answer = input(
        f"\nDo you want to copy {obj.key} to a subfolder in your bucket (y/n)? ")
    if answer.lower() == 'y':
        dest_obj = bucket.Object(f'demo-folder/{obj.key}')
        try:
            dest_obj.copy({'Bucket': bucket.name, 'Key': obj.key})
            print(f"Copied {obj.key} to {dest_obj.key}.")
        except ClientError as err:
            print(f"Couldn't copy {obj.key} to {dest_obj.key}.")
            print(f"\t{err.response['Error']['Code']}:{err.response['Error']['Message']}")

    print("\nYour bucket contains the following objects:")
    try:
        for o in bucket.objects.all():
            print(f"\t{o.key}")
    except ClientError as err:
        print(f"Couldn't list the objects in bucket {bucket.name}.")
        print(f"\t{err.response['Error']['Code']}:{err.response['Error']['Message']}")

    answer = input(
        "\nDo you want to delete all of the objects as well as the bucket (y/n)? ")
    if answer.lower() == 'y':
        try:
            bucket.objects.delete()
            bucket.delete()
            print(f"Emptied and deleted bucket {bucket.name}.\n")
        except ClientError as err:
            print(f"Couldn't empty and delete bucket {bucket.name}.")
            print(f"\t{err.response['Error']['Code']}:{err.response['Error']['Message']}")

    print("Thanks for watching!")
    print('-'*88)


if __name__ == '__main__':
    do_scenario(boto3.resource('s3'))
# snippet-end:[python.example_code.s3.Scenario_GettingStarted]

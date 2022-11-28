# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to export the datasets (manifest files and images)
from an Amazon Lookout for Vision project to a new S3 location.
"""

import argparse
import json
import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


def copy_file(s3_resource, source_file,  destination_file):
    """
    Copies a file from a source S3 folder to a destination S3 folder.
    The destination can be in different bucket.
    :param s3: An S3 boto resource.
    :param source_file: The S3 path to the source file.
    :param destination_file: The destination S3 path for the copy operation.
    """

    source_bucket, source_key = source_file.replace("s3://", "").split("/", 1)
    destination_bucket, destination_key = destination_file.replace(
        "s3://", "").split("/", 1)

    try:

        bucket = s3_resource.Bucket(destination_bucket)
        dest_object = bucket.Object(destination_key)
        dest_object.copy_from(CopySource={
            'Bucket': source_bucket,
            'Key': source_key
        })
        dest_object.wait_until_exists()
        logger.info("Copied %s to %s", source_file, destination_file)
    except ClientError as error:
        if error.response['Error']['Code'] == '404':
            error_message = f"Failed to copy {source_file} to " \
                f"{destination_file}. : {error.response['Error']['Message']}"
            logger.warning(error_message)
            error.response['Error']['Message'] = error_message
        raise


def upload_manifest_file(s3_resource, manifest_file, destination):
    """
    Uploads a manifest file to a destination S3 folder.
    :param s3: An S3 boto resource.
    :param manifest_file: The manifest file that you want to upload.
    :destination: The S3 folder location to upload the manifest file to.
    """

    destination_bucket, destination_key = destination.replace(
        "s3://", "").split("/", 1)

    bucket = s3_resource.Bucket(destination_bucket)

    put_data = open(manifest_file, 'rb')
    obj = bucket.Object(destination_key + manifest_file)

    try:
        obj.put(Body=put_data)
        obj.wait_until_exists()
        logger.info(
            "Put manifest file '%s' to bucket '%s'.", obj.key,
            obj.bucket_name)
    except ClientError:
        logger.exception(
            "Couldn't put manifest file '%s' to bucket '%s'.", obj.key,
            obj.bucket_name)
        raise
    finally:
        if getattr(put_data, 'close', None):
            put_data.close()


def get_dataset_types(lookoutvision_client, project):
    """
    Determines the types of the datasets (train or test) in an
    Amazon Lookout for Vision project.
    :param lookoutvision_client: A Lookout for Vision boto3 client.
    :param project: The Lookout for Vision project that you want to check.
    :return: The dataset types in the project.
    """

    try:
        response = lookoutvision_client.describe_project(ProjectName=project)

        datasets = []

        for dataset in response['ProjectDescription']['Datasets']:
            if dataset['Status'] in ("CREATE_COMPLETE", "UPDATE_COMPLETE"):
                datasets.append(dataset['DatasetType'])
        return datasets

    except lookoutvision_client.exceptions.ResourceNotFoundException:
        logger.exception("Project %s not found.", project)
        raise


def process_json_line(s3_resource, entry, dataset_type, destination):
    """
    Creates a JSON line for a new manifest file, copies image and mask to
    destination.
    :param s3_resource: A Boto3 S3 resource.
    :param entry: A JSON line from the manifest file.
    :param dataset_type: The type (train or test) of the dataset that
    you want to create the manifest file for.
    :param destination: The S3 destination folder for the manifest file and dataset images.
    :return: A JSON line with details for the destination location.
    """
    entry_json = json.loads(entry)

    print(f"source: {entry_json['source-ref']}")

    # Use existing folder paths to ensure console added image names don't clash.
    bucket, key = entry_json['source-ref'].replace(
        "s3://", "").split("/", 1)
    logger.info("Source location: %s/%s", bucket, key)

    destination_image_location = destination + dataset_type + "/images/" + key

    copy_file(s3_resource, entry_json['source-ref'],
              destination_image_location)

    # Update JSON for writing.
    entry_json['source-ref'] = destination_image_location

    if 'anomaly-mask-ref' in entry_json:

        source_anomaly_ref = entry_json['anomaly-mask-ref']
        mask_bucket, mask_key = source_anomaly_ref.replace(
            "s3://", "").split("/", 1)

        destination_mask_location = destination + dataset_type + "/masks/" + \
            mask_key
        entry_json['anomaly-mask-ref'] = destination_mask_location

        copy_file(s3_resource, source_anomaly_ref,
                  entry_json['anomaly-mask-ref'])

    return entry_json


def write_manifest_file(lookoutvision_client, s3_resource, project,  dataset_type, destination):
    """
    Creates a manifest file for a dataset. Copies the manifest file and
    dataset images (and masks, if present) to the specified S3 destination.
    :param lookoutvision_client: A Lookout for Vision boto3 client.
    :param project: The Lookout for Vision project that you want to use.
    :param dataset_type: The type (train or test) of the dataset that
    you want to create the manifest file for.
    :param destination: The S3 destination folder for the manifest file
    and dataset images.
    """

    try:

        # Create a reusable Paginator
        paginator = lookoutvision_client.get_paginator('list_dataset_entries')

        # Create a PageIterator from the Paginator
        page_iterator = paginator.paginate(ProjectName=project,
                                           DatasetType=dataset_type,
                                           PaginationConfig={
                                               'PageSize': 100
                                           }
                                           )

        output_manifest_file = dataset_type + ".manifest"

        # Create manifest file then upload to S3 with images.
        with open(output_manifest_file, "w", encoding="utf-8") as manifest_file:

            for page in page_iterator:
                for entry in page['DatasetEntries']:

                    try:
                        entry_json = process_json_line(
                            s3_resource, entry, dataset_type, destination)

                        manifest_file.write(json.dumps(entry_json) + "\n")

                    except ClientError as error:
                        if error.response['Error']['Code'] == '404':
                            print(error.response['Error']['Message'])
                            print(f"Excluded JSON line: {entry}")
                        else:
                            raise
        upload_manifest_file(s3_resource, output_manifest_file,
                             destination + "datasets/")

    except ClientError:
        logger.exception("Problem getting dataset_entries")
        raise


def export_datasets(lookoutvision_client, s3_resource, project, destination):
    """
    Exports the datasets from an Amazon Lookout for Vision project to a specified S3
    destination.
    :param project: The Lookout for Vision project that you want to use.
    :param destination: The destination S3 folder for the exported datasets.
    """
    # Add trailing backslash, if missing.
    destination = destination if destination[-1] == "/"  \
        else destination+"/"

    print(f"Exporting project {project} datasets to {destination}.")

    # Get each dataset and export to destination.

    dataset_types = get_dataset_types(lookoutvision_client, project)
    for dataset in dataset_types:

        logger.info("Copying %s dataset to %s.", dataset, destination)

        write_manifest_file(lookoutvision_client, s3_resource, project, dataset,
                            destination)

    print("Exported dataset locations")
    for dataset in dataset_types:
        print(f"   {dataset}: {destination}datasets/{dataset}.manifest")

    print("Done.")


def add_arguments(parser):
    """
    Adds command line arguments to the parser.
    :param parser: The command line parser.
    """

    parser.add_argument(
        "project", help="The project that contains the dataset.")
    parser.add_argument("destination", help="The destination S3 folder.")


def main():
    """
    Exports the datasets from an Amazon Lookout for Vision project to a
    destination S3 location.
    """
    logging.basicConfig(level=logging.INFO,
                        format="%(levelname)s: %(message)s")
    parser = argparse.ArgumentParser(usage=argparse.SUPPRESS)
    add_arguments(parser)

    args = parser.parse_args()

    try:

        lookoutvision_client = boto3.client("lookoutvision")
        s3_resource = boto3.resource('s3')
        export_datasets(lookoutvision_client, s3_resource,
                        args.project, args.destination)
    except ClientError as err:
        logger.exception(err)
        print(f"Failed: {format(err)}")


if __name__ == "__main__":
    main()

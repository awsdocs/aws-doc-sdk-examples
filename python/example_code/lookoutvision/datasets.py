# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
Amazon lookout for Vision dataset code examples used in the service documentation:
https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/model-create-dataset.html
Shows how to create and manage datasets. Also, how to create a manifest file and
upload to an S3 bucket.
"""
import logging
import time
from datetime import datetime
import os
import json
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class Datasets:
    """
    Provides example functions for creating, listing and deleting Amazon Lookout
    for Vision datasets. Also shows how to create a manifest file in an S3 bucket.
    """

    @staticmethod
    def create_dataset(lookoutvision_client, project_name, manifest_file, dataset_type):
        """
        Creates a new Amazon Lookout for Vision dataset
        :param lookoutvision_client: The Amazon Lookout for Vision Boto3 client.
        :param project_name: The name of the project in which you want to
         create a dataset.
        :param bucket:  The bucket that contains the manifest file.
        :param manifest_file: The path and name of the manifest file.
        :param dataset_type: The type of the dataset (train or test).
        """

        try:

            bucket, key = manifest_file.replace("s3://", "").split("/", 1)

            # Create a dataset
            logger.info("Creating %s dataset type...", dataset_type)

            dataset = {
                "GroundTruthManifest": {"S3Object": {"Bucket": bucket, "Key": key}}
            }

            response = lookoutvision_client.create_dataset(
                ProjectName=project_name,
                DatasetType=dataset_type,
                DatasetSource=dataset,
            )
            logger.info("Dataset Status: %s", response["DatasetMetadata"]["Status"])
            logger.info(
                "Dataset Status Message: %s",
                response["DatasetMetadata"]["StatusMessage"],
            )
            logger.info("Dataset Type: %s", response["DatasetMetadata"]["DatasetType"])

            # Wait until either created or failed.
            finished = False
            status = ""

            while finished is False:

                dataset_description = lookoutvision_client.describe_dataset(
                    ProjectName=project_name, DatasetType=dataset_type
                )
                status = dataset_description["DatasetDescription"]["Status"]

                if status == "CREATE_IN_PROGRESS":
                    logger.info("Dataset creation in progress...")
                    time.sleep(2)
                    continue

                if status == "CREATE_COMPLETE":
                    logger.info("Dataset created.")
                    finished = True
                    continue

                logger.info(
                    "Dataset creation failed: %s",
                    dataset_description["DatasetDescription"]["StatusMessage"],
                )
                finished = True

            if status != "CREATE_COMPLETE":
                message = dataset_description["DatasetDescription"]["StatusMessage"]
                logger.exception("Couldn't create dataset: %s", message)
                raise Exception(f"Couldn't create dataset: {message}")

        except ClientError as err:
            logger.exception(
                "Service error: Couldn't create dataset: %s", err.response["Message"]
            )
            raise

    @staticmethod
    def create_manifest_file_s3(s3_resource, image_s3_path, manifest_s3_path):
        """
        Creates a manifest file and uploads to S3.
        :param image_s3_path: The S3 path to the images referenced by the manifest file.
        The images must be in an S3 bucket with the following folder structure.
        s3://my-bucket/<train or test>/
            normal/
            anomaly/
        Place normal images in the normal folder. Anomalous images in the anomaly
        folder.
        https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/create-dataset-s3.html
        :param manifest_s3_path: The S3 location in which to store the created
        manifest file.
        """

        try:
            output_manifest_file = "temp.manifest"

            # Current date and time in manifest file format

            dttm = datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%f")

            # get bucket and folder from image and manifest file paths
            bucket, prefix = image_s3_path.replace("s3://", "").split("/", 1)

            manifest_bucket, manifest_prefix = manifest_s3_path.replace(
                "s3://", ""
            ).split("/", 1)

            # create local temp manifest file
            with open(output_manifest_file, "w") as mfile:

                logger.info("Creating manifest file")
                # create JSON lines for anomalous images

                src_bucket = s3_resource.Bucket(bucket)
                # create json lines for abnormal images.

                for obj in src_bucket.objects.filter(
                    Prefix=prefix + "anomaly/", Delimiter="/"
                ):
                    image_path = f"s3://{src_bucket.name}/{obj.key}"
                    manifest = Datasets.create_json_line(image_path, "anomaly", dttm)
                    mfile.write(json.dumps(manifest) + "\n")

                # create json lines for normal images
                for obj in src_bucket.objects.filter(
                    Prefix=prefix + "normal/", Delimiter="/"
                ):
                    image_path = f"s3://{src_bucket.name}/{obj.key}"
                    manifest = Datasets.create_json_line(image_path, "normal", dttm)
                    mfile.write(json.dumps(manifest) + "\n")

            # copy local manifest to target S3 location
            logger.info("Uploading manifest file to %s", manifest_s3_path)
            s3_resource.Bucket(manifest_bucket).upload_file(
                output_manifest_file, manifest_prefix
            )

            # delete local manifest file
            os.remove(output_manifest_file)

        except ClientError as err:
            logger.exception("S3 Service Error: %s", format(err))
            raise

        except Exception as err:
            logger.exception(format(err))
            raise
        else:
            logger.info("Completed manifest file creation and upload.")

    @staticmethod
    def create_json_line(image, class_name, dttm):
        """
        Creates a single JSON line for an image.
        :param image: The S3 location for the image.
        :param label: The label for the image (normal or anomaly)
        :param dttm: The date and time that the JSON is created.
        """

        label = 0
        if class_name == "normal":
            label = 0
        elif class_name == "anomaly":
            label = 1
        else:
            logger.exception("Unexpected label value: %s for %s", str(label), image)

            raise Exception(
                "Unexpected label value: {} for {}".format(str(label), image)
            )

        manifest = {
            "source-ref": image,
            "anomaly-label": label,
            "anomaly-label-metadata": {
                "confidence": 1,
                "job-name": "labeling-job/anomaly-label",
                "class-name": class_name,
                "human-annotated": "yes",
                "creation-date": dttm,
                "type": "groundtruth/image-classification",
            },
        }
        return manifest

    @staticmethod
    def delete_dataset(lookoutvision_client, project_name, dataset_type):
        """
        Deletes an Amazon Lookout for Vision dataset
        :param lookoutvision_client: The Amazon Lookout for Vision Boto3 client.
        :param project_name: The name of the project that contains the dataset that
        you want to delete.
        :param dataset_type: The type (train or test) of the dataset that you
        want to delete.
        """
        try:

            # Delete the dataset
            logger.info(
                "Deleting the %s dataset for project %s.", dataset_type, project_name
            )
            lookoutvision_client.delete_dataset(
                ProjectName=project_name, DatasetType=dataset_type
            )
            logger.info("Dataset deleted.")

        except ClientError as err:
            logger.exception(
                "Service error: Couldn't delete dataset: %s.", err.response["Message"]
            )
            raise

    @staticmethod
    def describe_dataset(lookoutvision_client, project_name, dataset_type):
        """
        Gets information about an Amazon Lookout for Vision dataset.
        :param lookoutvision_client: The Amazon Lookout for Vision Boto3 client.
        :param project_name: The name of the project that contains the dataset that
        you want to describe.
        :param dataset_type: The type (train or test) of the dataset that you want
        to describe.
        """

        try:
            # Describe a dataset

            response = lookoutvision_client.describe_dataset(
                ProjectName=project_name, DatasetType=dataset_type
            )
            print(f"Name: {response['DatasetDescription']['ProjectName']}")
            print(f"Type: {response['DatasetDescription']['DatasetType']}")
            print(f"Status: {response['DatasetDescription']['Status']}")
            print(f"Message: {response['DatasetDescription']['StatusMessage']}")
            print(
                f"Images: {str(response['DatasetDescription']['ImageStats']['Total'])}"
            )
            print(
                f"Labeled: {str(response['DatasetDescription']['ImageStats']['Labeled'])}"
            )
            print(
                f"Normal: {str(response['DatasetDescription']['ImageStats']['Normal'])}"
            )
            print(
                f"Anomaly: {str(response['DatasetDescription']['ImageStats']['Anomaly'])}"
            )

            print("Done...")

        except ClientError as err:
            logger.exception(
                "Service error: problem list datasets: %s", err.response["Message"]
            )
        print("Done")

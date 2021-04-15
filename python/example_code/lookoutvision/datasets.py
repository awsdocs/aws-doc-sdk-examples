# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
Amazon lookout for vision dataset code examples used in the service documentation:
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
        param: lookoutvision_client: The Amazon Lookout for Vision boto 3 client.
        param: project_name: The name of the project in which you want to create a dataset.
        param: bucket:  The bucket that contains the manifest file.
        param: manifest_file: The path and name of the manifest file.
        param: dataset_type: The type of the dataset (train or test).
        """

        try:

            bucket, key = manifest_file.replace("s3://", "").split("/", 1)

            # Create a dataset
            logger.info("Creating %s dataset type...", dataset_type)

            dataset = json.loads(
                '{ "GroundTruthManifest": { "S3Object": { "Bucket": "'
                + bucket
                + '", "Key": "'
                + key
                + '" } } }'
            )

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
                logger.exception(
                    "Couldn't create dataset: %s",
                    dataset_description["DatasetDescription"]["StatusMessage"],
                )
                raise Exception(
                    "Couldn't create dataset: {}".format(
                    dataset_description["DatasetDescription"]["StatusMessage"],
                ))

        except ClientError as err:
            logger.exception(
                "Service error: Couldn't create dataset: %s", err.response["Message"]
            )
            raise

    @staticmethod
    def create_manifest_file_s3(image_s3_path, manifest_s3_path):
        """
        Creates a manifest file and uploads to S3.
        param: image_s3_path: The S3 path to the images referenced by the manifest file. The images
        must be in an S3 bucket with the following folder structure.
        s3://my-bucket/<train or test>/
            normal/
            anomaly/
        Place normal images in the normal folder. Anomalous images in the anomaly folder.
        https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/create-dataset-s3.html
        param: manifest_s3_path:  The S3 location in which to store the created manifest file.
        """

        try:
            output_manifest_file = "temp.manifest"

            # Current date and time in manifest file format
            #now=datetime.now()
            dttm = datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%f")

            # get bucket and folder from image and manifest file paths
            bucket, prefix = image_s3_path.replace("s3://", "").split("/", 1)

            manifest_bucket, manifest_prefix = manifest_s3_path.replace(
                "s3://", ""
            ).split("/", 1)

            s3_client = boto3.client("s3")

            # create local temp manifest file
            with open(output_manifest_file, "w") as mfile:

                logger.info("Creating manifest file")
                # create JSON lines for anomalous images
                response = s3_client.list_objects_v2(
                    Bucket=bucket, Prefix=prefix + "anomaly/", Delimiter="/"
                )
                for file in response["Contents"]:
                    image_path = "s3://{}/{}".format(bucket, file["Key"])
                    manifest = Datasets.create_json_line(image_path, 1, dttm)
                    mfile.write(json.dumps(manifest) + "\n")
                # create json lines for normal images
                response = s3_client.list_objects_v2(
                    Bucket=bucket, Prefix=prefix + "normal/", Delimiter="/"
                )
                for file in response["Contents"]:
                    image_path = "s3://{}/{}".format(bucket, file["Key"])
                    manifest = Datasets.create_json_line(image_path, 0, dttm)
                    mfile.write(json.dumps(manifest) + "\n")

            # copy local manifest to target S3 location
            logger.info("Uploading manifest file to %s", manifest_s3_path)
            response = s3_client.upload_file(
                output_manifest_file, manifest_bucket, manifest_prefix
            )
            # delete local manifest file
            os.remove(output_manifest_file)

        except ClientError as err:
            print("S3 Service Error: {}".format(err))
            raise

        except Exception as err:
            print(err)
            raise
        else:
            logger.info("Completed manifest file creation and upload.")

    @staticmethod
    def create_json_line(image, label, dttm):
        """
        Creates a single JSON line for an image.
        param: image: The S3 location for the image.
        param: label: The label for the image (normal or anomaly)
        param: dttm: The date and time that the JSON is created.
        """

        class_name = ""

        if label == 0:
            class_name = "normal"
        elif label == 1:
            class_name = "anomaly"
        else:
            logger.exception("Unexpected label value: %s for %s", str(label), image)

            raise Exception(
                "Unexpected label value: {} for {}".format(str(label), image)
            )

        manifest = {
            "source-ref": image,
            "auto-label": label,
            "auto-label-metadata": {
                "confidence": 1,
                "job-name": "labeling-job/auto-label",
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
        param: lookoutvision_client: The Amazon Lookout for Vision boto 3 client.
        param: project_name: The name of the project that contains the dataset that
        you want to delete.
        param: dataset_type: The type (train or test) of the dataset that you
        want to delete.
        """
        try:
            lookoutvision_client = boto3.client("lookoutvision")

            # Delete the dataset
            logger.info(
                "Deleting the " + dataset_type + " dataset for project " + project_name
            )
            lookoutvision_client.delete_dataset(
                ProjectName=project_name, DatasetType=dataset_type
            )
            logger.info("Dataset deleted")

        except ClientError as err:
            logger.exception(
                "Service error: Couldn't delete dataset: %s", err.response["Message"]
            )
            raise

    @staticmethod
    def describe_dataset(lookoutvision_client, project_name, dataset_type):
        """
        Gets information about an Amazon Lookout for Vision dataset.
        param: lookoutvision_client: The Amazon Lookout for Vision boto3 client.
        param: project_name: The name of the project that contains the dataset that
        you want to describe.
        param: dataset_type: The type (train or test) of the dataset that you want
        to describe.
        """

        try:
            # Describe a dataset

            response = lookoutvision_client.describe_dataset(
                ProjectName=project_name, DatasetType=dataset_type
            )
            print("Name: " + response["DatasetDescription"]["ProjectName"])
            print("Type: " + response["DatasetDescription"]["DatasetType"])
            print("Status: " + response["DatasetDescription"]["Status"])
            print("Message: " + response["DatasetDescription"]["StatusMessage"])
            print(
                "Images: " + str(response["DatasetDescription"]["ImageStats"]["Total"])
            )
            print(
                "Labeled: "
                + str(response["DatasetDescription"]["ImageStats"]["Labeled"])
            )
            print(
                "Normal: " + str(response["DatasetDescription"]["ImageStats"]["Normal"])
            )
            print(
                "Anomaly: "
                + str(response["DatasetDescription"]["ImageStats"]["Anomaly"])
            )

            print("Done...")

        except ClientError as err:
            logger.exception(
                "Service error: problem list datasets: %s", err.response["Message"]
            )
        print("Done")

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Amazon Lookout for Vision dataset code examples used in the service documentation:
https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/model-create-dataset.html
Shows how to create and manage datasets. Also, how to create a manifest file and
upload to an Amazon S3 bucket.
"""

import logging
import time
from datetime import datetime
import os
import json

from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.lookoutvision.Datasets]
class Datasets:
    # snippet-end:[python.example_code.lookoutvision.Datasets]
    """
    Provides example functions for creating, listing, and deleting Lookout for Vision
    datasets. Also shows how to create a manifest file in an Amazon S3 bucket.
    """

# snippet-start:[python.example_code.lookoutvision.CreateDataset]
    @staticmethod
    def create_dataset(lookoutvision_client, project_name, manifest_file, dataset_type):
        """
        Creates a new Lookout for Vision dataset

        :param lookoutvision_client: A Lookout for Vision Boto3 client.
        :param project_name: The name of the project in which you want to
                             create a dataset.
        :param bucket: The bucket that contains the manifest file.
        :param manifest_file: The path and name of the manifest file.
        :param dataset_type: The type of the dataset (train or test).
        """
        try:

            bucket, key = manifest_file.replace("s3://", "").split("/", 1)
            logger.info("Creating %s dataset type...", dataset_type)
            dataset = {
                "GroundTruthManifest": {"S3Object": {"Bucket": bucket, "Key": key}}
            }
            response = lookoutvision_client.create_dataset(
                ProjectName=project_name,
                DatasetType=dataset_type,
                DatasetSource=dataset,
            )
            logger.info("Dataset Status: %s",
                        response["DatasetMetadata"]["Status"])
            logger.info(
                "Dataset Status Message: %s",
                response["DatasetMetadata"]["StatusMessage"],
            )
            logger.info("Dataset Type: %s",
                        response["DatasetMetadata"]["DatasetType"])

            # Wait until either created or failed.
            finished = False
            status = ""
            dataset_description = {}
            while finished is False:
                dataset_description = lookoutvision_client.describe_dataset(
                    ProjectName=project_name, DatasetType=dataset_type
                )
                status = dataset_description["DatasetDescription"]["Status"]

                if status == "CREATE_IN_PROGRESS":
                    logger.info("Dataset creation in progress...")
                    time.sleep(2)
                elif status == "CREATE_COMPLETE":
                    logger.info("Dataset created.")
                    finished = True
                else:
                    logger.info(
                        "Dataset creation failed: %s",
                        dataset_description["DatasetDescription"]["StatusMessage"])
                    finished = True

            if status != "CREATE_COMPLETE":
                message = dataset_description["DatasetDescription"]["StatusMessage"]
                logger.exception("Couldn't create dataset: %s", message)
                raise Exception(f"Couldn't create dataset: {message}")

        except ClientError:
            logger.exception("Service error: Couldn't create dataset.")
            raise
# snippet-end:[python.example_code.lookoutvision.CreateDataset]

# snippet-start:[python.example_code.lookoutvision.Scenario_CreateManifestFile]
    @staticmethod
    def create_manifest_file_s3(s3_resource, image_s3_path, manifest_s3_path):
        """
        Creates a manifest file and uploads to Amazon S3.

        :param s3_resource: A Boto3 Amazon S3 resource.
        :param image_s3_path: The Amazon S3 path to the images referenced by the
                              manifest file. The images must be in an Amazon S3 bucket
                              with the following folder structure.
                                s3://doc-example-bucket/<train or test>/
                                    normal/
                                    anomaly/
                              Place normal images in the normal folder and anomalous
                              images in the anomaly folder.
        :param manifest_s3_path: The Amazon S3 location in which to store the created
                                 manifest file.
        """
        output_manifest_file = "temp.manifest"
        try:

            # Current date and time in manifest file format.
            dttm = datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%f")

            # Get bucket and folder from image and manifest file paths.
            bucket, prefix = image_s3_path.replace("s3://", "").split("/", 1)
            if prefix[-1] != '/':
                prefix += '/'
            manifest_bucket, manifest_prefix = manifest_s3_path.replace(
                "s3://", "").split("/", 1)

            with open(output_manifest_file, "w") as mfile:
                logger.info("Creating manifest file")
                src_bucket = s3_resource.Bucket(bucket)

                # Create JSON lines for anomalous images.
                for obj in src_bucket.objects.filter(
                        Prefix=prefix + "anomaly/", Delimiter="/"):
                    image_path = f"s3://{src_bucket.name}/{obj.key}"
                    manifest = Datasets.create_json_line(
                        image_path, "anomaly", dttm)
                    mfile.write(json.dumps(manifest) + "\n")

                # Create json lines for normal images.
                for obj in src_bucket.objects.filter(
                        Prefix=prefix + "normal/", Delimiter="/"):
                    image_path = f"s3://{src_bucket.name}/{obj.key}"
                    manifest = Datasets.create_json_line(
                        image_path, "normal", dttm)
                    mfile.write(json.dumps(manifest) + "\n")

            logger.info("Uploading manifest file to %s", manifest_s3_path)
            s3_resource.Bucket(manifest_bucket).upload_file(
                output_manifest_file, manifest_prefix)
        except ClientError:
            logger.exception("Error uploading manifest.")
            raise
        except Exception:
            logger.exception("Error uploading manifest.")
            raise
        else:
            logger.info("Completed manifest file creation and upload.")
        finally:
            try:
                os.remove(output_manifest_file)
            except FileNotFoundError:
                pass

    @staticmethod
    def create_json_line(image, class_name, dttm):
        """
        Creates a single JSON line for an image.

        :param image: The S3 location for the image.
        :param class_name: The class of the image (normal or anomaly)
        :param dttm: The date and time that the JSON is created.
        """

        label = 0
        if class_name == "normal":
            label = 0
        elif class_name == "anomaly":
            label = 1
        else:
            logger.error("Unexpected label value: %s for %s", label, image)
            raise Exception(f"Unexpected label value: {label} for {image}")

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
# snippet-end:[python.example_code.lookoutvision.Scenario_CreateManifestFile]

# snippet-start:[python.example_code.lookoutvision.DeleteDataset]
    @staticmethod
    def delete_dataset(lookoutvision_client, project_name, dataset_type):
        """
        Deletes a Lookout for Vision dataset

        :param lookoutvision_client: A Boto3 Lookout for Vision client.
        :param project_name: The name of the project that contains the dataset that
                             you want to delete.
        :param dataset_type: The type (train or test) of the dataset that you
                             want to delete.
        """
        try:
            logger.info(
                "Deleting the %s dataset for project %s.", dataset_type, project_name)
            lookoutvision_client.delete_dataset(
                ProjectName=project_name, DatasetType=dataset_type)
            logger.info("Dataset deleted.")
        except ClientError:
            logger.exception("Service error: Couldn't delete dataset.")
            raise
# snippet-end:[python.example_code.lookoutvision.DeleteDataset]

# snippet-start:[python.example_code.lookoutvision.DescribeDataset]
    @staticmethod
    def describe_dataset(lookoutvision_client, project_name, dataset_type):
        """
        Gets information about a Lookout for Vision dataset.

        :param lookoutvision_client: A Boto3 Lookout for Vision client.
        :param project_name: The name of the project that contains the dataset that
                             you want to describe.
        :param dataset_type: The type (train or test) of the dataset that you want
                             to describe.
        """
        try:
            response = lookoutvision_client.describe_dataset(
                ProjectName=project_name, DatasetType=dataset_type)
            print(f"Name: {response['DatasetDescription']['ProjectName']}")
            print(f"Type: {response['DatasetDescription']['DatasetType']}")
            print(f"Status: {response['DatasetDescription']['Status']}")
            print(
                f"Message: {response['DatasetDescription']['StatusMessage']}")
            print(
                f"Images: {response['DatasetDescription']['ImageStats']['Total']}")
            print(
                f"Labeled: {response['DatasetDescription']['ImageStats']['Labeled']}")
            print(
                f"Normal: {response['DatasetDescription']['ImageStats']['Normal']}")
            print(
                f"Anomaly: {response['DatasetDescription']['ImageStats']['Anomaly']}")
        except ClientError:
            logger.exception("Service error: problem listing datasets.")
            raise
        print("Done.")
# snippet-end:[python.example_code.lookoutvision.DescribeDataset]

# snippet-start:[python.example_code.lookoutvision.UpdateDatasetEntries]
    @staticmethod
    def update_dataset_entries(lookoutvision_client, project_name, dataset_type, updates_file):
        """
        Adds dataset entries to an Amazon Lookout for Vision dataset.    
        :param lookoutvision_client: The Amazon Rekognition Custom Labels Boto3 client.
        :param project_name: The project that contains the dataset that you want to update.
        :param dataset_type: The type of the dataset that you want to update (train or test).
        :param updates_file: The manifest file of JSON Lines that contains the updates. 
        """

        try:
            status = ""
            status_message = ""
            manifest_file = ""

            # Update dataset entries
            logger.info(f"Updating {dataset_type} dataset for project {project_name}"
                "with entries from {updates_file}.")

            with open(updates_file) as f:
                manifest_file = f.read()

            lookoutvision_client.update_dataset_entries(
                ProjectName=project_name,
                DatasetType=dataset_type,
                Changes=manifest_file,
            )

            finished = False
            
            while not finished:

                dataset = lookoutvision_client.describe_dataset(ProjectName=project_name,
                                                                DatasetType=dataset_type)

                status = dataset['DatasetDescription']['Status']
                status_message = dataset['DatasetDescription']['StatusMessage']

                if status == "UPDATE_IN_PROGRESS":
                    logger.info(
                        (f"Updating {dataset_type} dataset for project {project_name}."))
                    time.sleep(5)
                    continue

                if status == "UPDATE_FAILED_ROLLBACK_IN_PROGRESS":
                    logger.info(
                        (f"Update failed, rolling back {dataset_type} dataset for project {project_name}."))
                    time.sleep(5)
                    continue

                if status == "UPDATE_COMPLETE":
                    logger.info(
                        f"Dataset updated: {status} : {status_message} : {dataset_type} dataset for project {project_name}.")
                    finished = True
                    continue

                if status == "UPDATE_FAILED_ROLLBACK_COMPLETE":
                    logger.info(
                        f"Rollback completed after update failure: {status} : {status_message} : {dataset_type} dataset for project {project_name}.")
                    finished = True
                    continue

                logger.exception(
                    f"Failed. Unexpected state for dataset update: {status} : {status_message} : "
                     "{dataset_type} dataset for project {project_name}.")
                raise Exception(
                    f"Failed. Unexpected state for dataset update: {status} : "
                    "{status_message} :{dataset_type} dataset for project {project_name}.")

            logger.info(f"Added entries to dataset.")

            return status, status_message

        except ClientError as err:
            logger.exception(
                f"Couldn't update dataset: {err.response['Error']['Message']}")
            raise
    # snippet-end:[python.example_code.lookoutvision.UpdateDatasetEntries]

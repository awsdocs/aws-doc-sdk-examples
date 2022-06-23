# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to create and optionally start an Amazon Lookout for Vision model.
"""

import argparse
import logging
import boto3
from projects import Projects
from datasets import Datasets
from models import Models
from hosting import Hosting

logger = logging.getLogger(__name__)


def start_model(lookoutvision_client, project_name, version):
    """
    Starts a model, if requested.

    :param lookoutvision_client: A Boto3 Lookout for Vision client.
    :param project_name: The name of the project that contains the model version
                         you want to start.
    :param: version: The version of the model that you want to start.
    """
    start = input("Do you want to start your model (y/n)?")
    if start == "y":
        print("Starting model...")
        Hosting.start_model(lookoutvision_client, project_name, version, 1)
        print("Your model is ready to use with the following command.\n")
        print(f"python inference.py {project_name} {version} <your_image>")
        print(
            "\nStop your model when you're done. You're charged while it's running. "
            "See hosting.py")
    else:
        print("Not starting model.")


def create_dataset(
        lookoutvision_client, s3_resource, bucket, project_name, dataset_images,
        dataset_type):
    """
    Creates a manifest from images in the supplied bucket and then creates
    a dataset.

    :param lookoutvision_client: A Boto3 Lookout for Vision client.
    :param s3_resource: A Boto3 Amazon S3 client.
    :param bucket: The bucket that stores the manifest file.
    :param project_name: The project in which to create the dataset.
    :param dataset_images: The location of the images referenced by the dataset.
    :param dataset_type: The type of dataset to create (train or test).
    """
    print(f"Creating {dataset_type} dataset...")

    manifest_file = f"s3://{bucket}/{project_name}/manifests/{dataset_type}.manifest"

    logger.info("Creating %s manifest file in %s.", dataset_type, manifest_file)
    Datasets.create_manifest_file_s3(s3_resource, dataset_images, manifest_file)

    logger.info("Create %s dataset for project %s", dataset_type,project_name)
    Datasets.create_dataset(
        lookoutvision_client, project_name, manifest_file, dataset_type)


def train_model(lookoutvision_client, bucket, project_name):
    """
    Trains a model.

    :param lookoutvision_client: A Boto3 Lookout for Vision client.
    :param bucket: The bucket where the training output is stored.
    :param project_name: The project that you want to train.
    """
    print("Training model...")
    training_results = f"{bucket}/{project_name}/output/"
    status, version = Models.create_model(
        lookoutvision_client, project_name, training_results)

    Models.describe_model(lookoutvision_client, project_name, version)
    if status == "TRAINED":
        print(
            "\nCheck the performance metrics and decide if you need to improve "
            "the model performance.")
        print(
            "\nMore information: "
            "https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/improve.html")
        print("If you are satisfied with your model, you can start it.")
        start_model(lookoutvision_client, project_name, version)
    else:
        print("Model training failed.")


def main():
    """
    Creates and optionally starts an Amazon Lookout for Vision model using
    command line arguments.

    A new project, training dataset, optional test dataset, and model are created.
    After model training is completed, you can use the code in inference.py to try your
    model with an image.
    """
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    parser = argparse.ArgumentParser(usage=argparse.SUPPRESS)
    parser.add_argument("project", help="A unique name for your project")
    parser.add_argument(
        "bucket",
        help="The bucket used to upload your manifest files and store training output")
    parser.add_argument(
        "training", help="The S3 path where the service gets the training images.")
    parser.add_argument(
        "test", nargs="?", default=None,
        help="(Optional) The S3 path where the service gets the test images.")
    args = parser.parse_args()

    project_name = args.project
    bucket = args.bucket
    training_images = args.training
    test_images = args.test
    lookoutvision_client = boto3.client("lookoutvision")
    s3_resource = boto3.resource("s3")

    print(f"Storing information in s3://{bucket}/{project_name}/")
    print("Creating project...")
    Projects.create_project(lookoutvision_client, project_name)

    create_dataset(
        lookoutvision_client, s3_resource, bucket, project_name, training_images, "train")
    if test_images is not None:
        create_dataset(
            lookoutvision_client, s3_resource, bucket, project_name, test_images, "test")

    # Train the model and optionally start hosting.
    train_model(lookoutvision_client, bucket, project_name)


if __name__ == "__main__":
    main()

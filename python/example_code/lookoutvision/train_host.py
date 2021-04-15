# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
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

def main():
    """
    Creates and optionally starts an Amazon Lookout for Vision model using command line arguments.
    A new project, training dataset, optional test dataset, and model are created.
    After model training is completed, you can use the code in inference.py to try your
    model with an image.
    param: project: A name for your project.
    param: bucket:  The S3 bucket in which to store your manifest file and training output.
    param: train: The S3 path to where the service gets your training images.
    param: test: (Optional) the S3 path to where the service gets your test images.
    """

    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    # Get command line arguments
    parser = argparse.ArgumentParser(usage=argparse.SUPPRESS)
    parser.add_argument("project", help="A unique name for your project")
    parser.add_argument(
        "bucket",
        help="The bucket used to upload your manifest files and store training output",
    )
    parser.add_argument(
        "training", help="The S3 path to where the service gets the training images."
    )
    parser.add_argument(
        "test",
        nargs="?",
        default=None,
        help="(Optional) The S3 path to where the service gets the test images.",
    )

    args = parser.parse_args()
    project_name = args.project
    bucket = args.bucket
    training_images = args.training
    test_images = args.test

    print("Storing information in s3://{}/{}/".format(bucket, project_name))

    lookoutvision_client = boto3.client("lookoutvision")

    print("Creating project...")
    Projects.create_project(lookoutvision_client, project_name)

    print("Creating training dataset...")
    train_manifest_file = "s3://{}/{}/manifests/{}".format(
        bucket, project_name, "train.manifest"
    )

    Datasets.create_manifest_file_s3(training_images, train_manifest_file)

    Datasets.create_dataset(
        lookoutvision_client,
        project_name,
        train_manifest_file,
        "train",
    )

    if test_images is not None:
        print("Creating test dataset")
        test_manifest_file = "s3://{}/{}/manifests/{}".format(
            bucket, project_name, "test.manifest"
        )
        Datasets.create_manifest_file_s3(test_images, test_manifest_file)
        Datasets.create_dataset(
            lookoutvision_client, project_name, test_manifest_file, "test"
        )

    print("Training model...")
    training_results = "{}/{}/output/".format(bucket, project_name)
    status, version = Models.create_model(
        lookoutvision_client, project_name, training_results
    )

    Models.describe_model(lookoutvision_client, project_name, version)
    if status == "TRAINED":
        print(
        "\nCheck the performance metrics and decide if you need to improve the model performance."
        )
        print(
        "More information: " 
        "https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/improve.html"
        )
        print("If you are satisfied with your model your model, you can start it.")
        start = input("Do you want to start your model (y/n)?")
        if start == "y":
            print("Starting model...")
            Hosting.start_model(lookoutvision_client, project_name, version, 1)
            print("Model is ready to use with the following command.\n")
            print(
                "python inference.py {} {} your_image".format(project_name, version)
            )
            print(
                "\nStop your model when you're done. You're charged whilst its running."
                + "See hosting.py"
            )
        else:
            print("Not starting model.")
    else:
        print("Model training failed.")



if __name__ == "__main__":
    main()

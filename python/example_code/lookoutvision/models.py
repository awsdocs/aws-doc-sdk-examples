# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
Amazon lookout for Vision model code examples used in the service documentation:
https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/model.html
Shows how to create and delete a model. Also, how to view the versions of the models
in a project.
"""

import time
import logging
import json

from botocore.exceptions import ClientError


logger = logging.getLogger(__name__)


class Models:
    """
    Provides example methods that create and manage Amazon Lookout for Vision
    models.
    """

    @staticmethod
    def create_model(
        lookoutvision_client,
        project_name,
        training_results,
        tag_key=None,
        tag_key_value=None,
    ):
        """
        Creates a version of an Amazon Lookout for Vision model.
        :param lookoutvision_client: The Amazon Lookout for Vision Boto3 client.
        :param project_name: The name of the project in which you want to create a
        model.
        :param training_results: The S3 location where training results are stored.
        :param tag_key: The key for a tag to add to the model.
        :param tag_key_value - A value associated with the tag_key.
        return: The model status and version.
        """

        try:
            # Create a model
            logger.info("Training model...")

            # Define training output location
            output_bucket, output_folder = training_results.replace("s3://", "").split(
                "/", 1
            )

            output_config = {
                "S3Location": {"Bucket": output_bucket, "Prefix": output_folder}
            }

            # Add tag, if defined.
            tags = []

            if tag_key is not None:
                tags = [{"Key": tag_key, "Value": tag_key_value}]

            # Create the model.
            response = lookoutvision_client.create_model(
                ProjectName=project_name, OutputConfig=output_config, Tags=tags
            )

            logger.info("ARN: %s", response["ModelMetadata"]["ModelArn"])
            logger.info("Version: %s", response["ModelMetadata"]["ModelVersion"])
            logger.info("Started training...")

            print("Training started. Training might take several hours to complete.")

            # Wait until training completes.
            finished = False
            while finished is False:
                model_description = lookoutvision_client.describe_model(
                    ProjectName=project_name,
                    ModelVersion=response["ModelMetadata"]["ModelVersion"],
                )
                status = model_description["ModelDescription"]["Status"]

                if status == "TRAINING":
                    logger.info("Model training in progress...")
                    time.sleep(600)
                    continue

                if status == "TRAINED":
                    logger.info("Model was successfully trained.")
                    finished = True
                    break

                logger.info(
                    "Model training failed: %s ",
                    model_description["ModelDescription"]["StatusMessage"],
                )
                finished = True
                break

            return status, response["ModelMetadata"]["ModelVersion"]

        except ClientError as err:
            print("Service error: " + format(err))
            raise

    @staticmethod
    def describe_model(lookoutvision_client, project_name, model_version):
        """
        Shows the performance metrics for a trained model.
        :param lookoutvision_client: The Amazon Lookout for Vision Boto3 client.
        :param project_name: The name of the project that contains the desired model.
        :param model_version: The version of the model.
        """

        # Get model description
        model_description = lookoutvision_client.describe_model(
            ProjectName=project_name, ModelVersion=model_version
        )["ModelDescription"]
        print(f"\tModel version: {model_description['ModelVersion']}")
        print(f"\tARN: {model_description['ModelArn']}")
        if "Description" in model_description:
            print(f"\tDescription: {model_description['Description']}")

        status = model_description["Status"]

        print(f"\tStatus: {status}")
        print(f"\tMessage: {model_description['StatusMessage']}")
        print(f"\tCreated: {str(model_description['CreationTimestamp'])}")

        if status in ("TRAINED", "HOSTED"):
            duration = str(
                model_description["EvaluationEndTimestamp"]
                - model_description["CreationTimestamp"]
            )
            print(f"\tTraining duration: {duration}")

            print("\n\tPerformance metrics\n\t-------------------")
            recall = str(model_description["Performance"]["Recall"])
            print(f"\tRecall: {recall}")
            precision = str(model_description["Performance"]["Precision"])
            print(f"\tPrecision: {precision}")
            f1 = str(model_description["Performance"]["F1Score"])
            print(f"\tF1: {f1}")

            training_output_bucket = str(
                model_description["OutputConfig"]["S3Location"]["Bucket"]
            )
            prefix = str(model_description["OutputConfig"]["S3Location"]["Prefix"])
            print(f"\tTraining output: s3://{training_output_bucket}/{prefix}")

    @staticmethod
    def describe_models(lookoutvision_client, project_name):
        """
        Gets information about all models in an Amazon Lookout for Vision project.
        :param project_name: The name of the project that you want to use.
        """
        try:

            # list models
            response = lookoutvision_client.list_models(ProjectName=project_name)
            print("Project: " + project_name)
            for model in response["Models"]:

                Models.describe_model(
                    lookoutvision_client, project_name, model["ModelVersion"]
                )
                print()

            print("Done...")

        except ClientError as err:
            logger.exception("Service error: %s", format(err))
            raise

    @staticmethod
    def delete_model(lookoutvision_client, project_name, model_version):
        """
        Deletes an Amazon Lookout for Vision model. The model must first be
        stopped and can't be in training.
        :param project_name: The name of the project that contains the desired model.
        :param model_version: The version of the model that you want to delete.
        """

        try:

            # Delete the model
            logger.info("Deleting model: %s", model_version)
            response = lookoutvision_client.delete_model(
                ProjectName=project_name, ModelVersion=model_version
            )

            model_exists = True

            while model_exists:
                model_exists = False
                response = lookoutvision_client.list_models(ProjectName=project_name)
                for model in response["Models"]:
                    if model["ModelVersion"] == model_version:
                        model_exists = True

                if model_exists is False:
                    logger.info("Model deleted")
                else:
                    logger.info("Model is being deleted...")
                    time.sleep(2)

            logger.info("Deleted Model: %s", model_version)

        except ClientError as err:
            logger.exception("Service error: %s", format(err))
            raise

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
Amazon Lookout for Vision model hosting examples used in the
 service documentation:
 https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/running-model.html
Shows how to start and stop a model. Also, how to get a list of running models
 in your AWS account.
"""

import time
import logging

from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class Hosting:
    """
    Class showing how to start and stop an Amazon Lookout for Vision Model. Also shows
    how to list the models that are currently running.
    """

    @staticmethod
    def start_model(
        lookoutvision_client, project_name, model_version, min_inference_units
    ):
        """
        Starts the hosting of an Amazon Lookout for Vision model.
        :param lookoutvision_client: The Amazon Lookout for Vision Boto3 client.
        :param project_name:  The name of the project that contains the version of the
        model that you want to start hosting.
        :param model_version: The version of the model that you want to start hosting.
        :param min_inference_units: The number of inference units to use for hosting.
        """

        try:

            # Start the model
            logger.info(
                "Starting model version %s for project %s", model_version, project_name
            )
            lookoutvision_client.start_model(
                ProjectName=project_name,
                ModelVersion=model_version,
                MinInferenceUnits=min_inference_units,
            )

            print("Starting hosting...")

            # Wait until either hosted or failed.
            status = ""
            finished = False

            while finished is False:

                model_description = lookoutvision_client.describe_model(
                    ProjectName=project_name, ModelVersion=model_version
                )
                status = model_description["ModelDescription"]["Status"]

                if status == "STARTING_HOSTING":
                    logger.info("Host starting in progress...")
                    time.sleep(10)
                    continue

                if status == "HOSTED":
                    logger.info("Model is hosted and ready for use.")
                    finished = True
                    continue

                logger.info("Model hosting failed and the model can't be used.")
                finished = True

            if status != "HOSTED":
                logger.exception("Error hosting model: %s", status)
                raise Exception(f"Error hosting model: {status}")

        except ClientError as err:
            logger.exception(format(err))
            raise

    @staticmethod
    def stop_model(lookoutvision_client, project_name, model_version):
        """
        Stops a running Amazon Lookout for Vision Model.
        :param lookoutvision_client: The Amazon Lookout for Vision Boto3 client.
        :param project_Name: The name of the project that contains the version of
        the model that you want to stop hosting.
        :param model_version:  The version of the model that you want to stop hosting.
        """
        try:
            # Stop the model
            logger.info("Stopping model version %s for %s", model_version, project_name)
            response = lookoutvision_client.stop_model(
                ProjectName=project_name, ModelVersion=model_version
            )
            logger.info("Stopping hosting...")
            status = response["Status"]

            # stops when hosting has stopped or failure.
            status = ""
            finished = False

            while finished is False:
                model_description = lookoutvision_client.describe_model(
                    ProjectName=project_name, ModelVersion=model_version
                )
                status = model_description["ModelDescription"]["Status"]

                if status == "STOPPING_HOSTING":
                    logger.info("Host stopping in progress...")
                    time.sleep(10)
                    continue

                if status == "TRAINED":
                    logger.info("Model is no longer hosted.")
                    finished = True
                    continue

                logger.info("Failed to stop model: %s ", status)
                finished = True

            if status != "TRAINED":
                logger.exception("Error stopping model: %s", status)
                raise Exception(f"Error stoppping model: {status}")

        except ClientError as err:
            logger.info(format(err))
            raise

    @staticmethod
    def list_hosted(lookoutvision_client):
        """
        :param lookoutvision_client: The Amazon Lookout for Vision Boto3 client.
        Displays a list of models in your account that are currently hosted.
        """
        try:
            response = lookoutvision_client.list_projects()
            hosted = 0
            print("Hosted models\n-------------")

            for project in response["Projects"]:

                response_models = lookoutvision_client.list_models(
                    ProjectName=project["ProjectName"]
                )

                for model in response_models["Models"]:
                    # Get model description
                    model_description = lookoutvision_client.describe_model(
                        ProjectName=project["ProjectName"],
                        ModelVersion=model["ModelVersion"],
                    )

                    if model_description["ModelDescription"]["Status"] == "HOSTED":
                        print(
                        f"Project: {project['ProjectName']} Model version: {model['ModelVersion']}"
                        )
                        hosted += 1
            print(f"{hosted} model(s) hosted")
        except ClientError as err:
            logger.info("Problem listing hosted models: %s", format(err))
            raise

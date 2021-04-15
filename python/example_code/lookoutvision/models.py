# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
Amazon lookout for vision model code examples used in the service documentation:
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
        tag_key = None,
        tag_key_value = None
    ):
        """
        Creates a version of an Amazon Lookout for Vision model.
        param: lookoutvision_client: The Amazon Lookout for Vision boto 3 client.
        param: project_name: The name of the project in which you want to create a model.
        param: training_results: The S3 location where training results are stored.
        param: tag_key: The key for a tag to add to the model.
        param: tag_key_value - A value associated with the tag_key.
        return: The model status and version.
        """

        try:
            # Create a model
            logger.info("Training model...")

            # Define training output location
            output_bucket, output_folder = training_results.replace("s3://", "").split(
            "/", 1 )

            output_config = json.loads(
                '{ "S3Location": { "Bucket": "'
                + output_bucket
                + '", "Prefix": "'
                + output_folder
                + '" } } '
            )

            #Add tag, if defined.
            tags=[]

            if tag_key is not None:
                tags = json.loads(
                '[{"Key": "' + tag_key + '" ,"Value":"' + tag_key_value + '"}]'
            )

            #Create the model.
            response = lookoutvision_client.create_model(
                    ProjectName=project_name,
                    OutputConfig=output_config,
                    Tags=tags
                )


            logger.info("ARN: %s", response["ModelMetadata"]["ModelArn"])
            logger.info("Version: %s", response["ModelMetadata"]["ModelVersion"])
            logger.info("Started training...")

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
                    finished=True
                    break
                logger.info(
                    "Model training failed: %s ",
                    model_description["ModelDescription"]["StatusMessage"],
                )
                finished=True


                print("Failed. Unexpected state for training: " + status)
                break

            return status, response["ModelMetadata"]["ModelVersion"]

        except ClientError as err:
            print("Service error: " + format(err))
            raise



    @staticmethod
    def describe_model(lookoutvision_client, project_name, model_version):
        """
        Shows the performance metrics for a trained model.
        param: project_name: The name of the project that contains the desired model.
        param: model_version: The version of the model.
        """

        # Get model description
        model_description = lookoutvision_client.describe_model(
        ProjectName=project_name, ModelVersion=model_version
                )["ModelDescription"]
        print("\tModel version: {}".format(model_description["ModelVersion"]))
        print("\tARN: {}".format(model_description["ModelArn"]))
        if "Description" in model_description:
            print("\tDescription: {}".format(model_description["Description"]))

        status = model_description["Status"]

        print("\tStatus: {}".format(status))
        print("\tMessage: {}".format(model_description["StatusMessage"]))
        print("\tCreated: {}".format(str(model_description["CreationTimestamp"])))

        if status in ('TRAINED', 'HOSTED'):
            print("\tTraining duration: {}".format(str(
                            model_description["EvaluationEndTimestamp"]
                            - model_description["CreationTimestamp"])
                )
            )
            print("\n\tPerformance metrics\n\t-------------------")
            print("\tRecall: {}".format(str(
                model_description["Performance"]["Recall"])
                )
            )
            print("\tPrecision: {}".format(
                str(
                    model_description["Performance"]
                    ["Precision"]
                    )
                )
            )
            print("\tF1: {}".format(str(model_description["Performance"]["F1Score"])))

            print("\tTraining output : s3://{}/{}".format(
               str(
                    model_description["OutputConfig"][
                        "S3Location"
                    ]["Bucket"]
                ),
                str(model_description["OutputConfig"]["S3Location"]["Prefix"])
                )
            )




    @staticmethod
    def delete_model(lookoutvision_client, project_name, model_version):
        """
        Deletes an Amazon Lookout for Vision model. The model must first be
        stopped and can't be in training.
        param: project_name: The name of the project that contains the desired model.
        param: model_version: The version of the model that you want to delete.
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
            print("Service error: %s", format(err))
            raise

    @staticmethod
    def describe_models(lookoutvision_client, project_name):
        """
        Gets information about all models in an Amazon Lookout for Vsion project.
        param: project_name: The name of the project that you want to use.
        """
        try:

            # list models
            response = lookoutvision_client.list_models(ProjectName=project_name)
            print("Project: " + project_name)
            for model in response["Models"]:

                Models.describe_model(lookoutvision_client, project_name, model["ModelVersion"] )
                print()

            print("Done...")

        except ClientError as err:
            print("Service error: " + format(err))
            raise

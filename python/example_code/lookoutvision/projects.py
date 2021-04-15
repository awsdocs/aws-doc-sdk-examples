# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Lookout for Vision project code examples for using projects.
Examples are used in the service documentation:
https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/.
Shows how to create and delete a project. Also, how to get information about your projects.
"""
import logging
from botocore.exceptions import ClientError


logger = logging.getLogger(__name__)


class Projects:
    """
    Provides example functions for creating, listing and deleting Amazon Lookout for Vision Projects
    """

    @staticmethod
    def create_project(lookoutvision_client, project_name):
        """
        Creates a new Amazon Lookout for Vision project.
        :param lookoutvision_client: An Amazon Lookout for Vision boto3 client.
        :param  project_name: The name for the new project.
        :return project_arn: The ARN of the new project.
        """

        try:
            # Create a project
            logger.info("Creating project: %s", project_name)
            response = lookoutvision_client.create_project(ProjectName=project_name)

            logger.info("project ARN: %s", response["ProjectMetadata"]["ProjectArn"])

            return response["ProjectMetadata"]["ProjectArn"]

        except ClientError as err:
            logger.exception("Couldn't create project: %s", err.response["Message"])
            raise

    @staticmethod
    def delete_project(lookoutvision_client, project_name):
        """
        Deletes an Amazon Lookout for Vision Model
        :param lookoutvision_client: An Amazon Lookout for Vision boto3 client.
        :param: project_name - the name of the project that you want to delete.
        """
        try:

            # Delete a project
            logger.info("Deleting project: %s", project_name)
            response = lookoutvision_client.delete_project(ProjectName=project_name)
            logger.info("Deleted project ARN: %s ", response["ProjectArn"])

        except ClientError as err:
            logger.exception("Couldn't delete project: %s", err.response["Message"])
            raise

    @staticmethod
    def list_projects(lookoutvision_client):
        """
        Lists information about the projects in your account.
        :param lookoutvision_client: An Amazon Lookout for Vision boto3 client.
        """

        try:
            response = lookoutvision_client.list_projects()

            for project in response["Projects"]:

                print("Project: " + project["ProjectName"])
                print("\tARN: " + project["ProjectArn"])
                print("\tCreated: " + str(["CreationTimestamp"]))

                print("Datasets")
                project_description = lookoutvision_client.describe_project(
                    ProjectName=project["ProjectName"]
                )
                if len(project_description["ProjectDescription"]["Datasets"]) == 0:
                    print("\tNo datasets")
                else:
                    for dataset in project_description["ProjectDescription"][
                        "Datasets"
                    ]:
                        print("\ttype: " + dataset["DatasetType"])
                        print("\tStatus: " + dataset["StatusMessage"])

                print("Models")
                # list models
                response_models = lookoutvision_client.list_models(
                    ProjectName=project["ProjectName"]
                )
                if len(response_models["Models"]) == 0:
                    print("\tNo models")
                else:
                    for model in response_models["Models"]:
                        print("\tVersion: " + model["ModelVersion"])
                        print("\tARN: " + model["ModelArn"])
                        if "Description" in model:
                            print("\tDescription: " + model["Description"])

                        # Get model description
                        model_description = lookoutvision_client.describe_model(
                            ProjectName=project["ProjectName"],
                            ModelVersion=model["ModelVersion"],
                        )
                        print(
                            "\tStatus: "
                            + model_description["ModelDescription"]["Status"]
                        )
                        print(
                            "\tMessage: "
                            + model_description["ModelDescription"]["StatusMessage"]
                        )
                        print(
                            "\tCreated: "
                            + str(
                                model_description["ModelDescription"][
                                    "CreationTimestamp"
                                ]
                            )
                        )

                        if model_description["ModelDescription"]["Status"] == "TRAINED":
                            print(
                                "\tTraining duration: "
                                + str(
                                    model_description["ModelDescription"][
                                        "EvaluationEndTimestamp"
                                    ]
                                    - model_description["ModelDescription"][
                                        "CreationTimestamp"
                                    ]
                                )
                            )
                            print(
                                "\tRecall: "
                                + str(
                                    model_description["ModelDescription"][
                                        "Performance"
                                    ]["Recall"]
                                )
                            )
                            print(
                                "\tPrecision: "
                                + str(
                                    model_description["ModelDescription"][
                                        "Performance"
                                    ]["Precision"]
                                )
                            )
                            print(
                                "\tF1: "
                                + str(
                                    model_description["ModelDescription"][
                                        "Performance"
                                    ]["F1Score"]
                                )
                            )
                            print(
                                "\tTraining output : s3://"
                                + str(
                                    model_description["ModelDescription"][
                                        "OutputConfig"
                                    ]["S3Location"]["Bucket"]
                                )
                                + "/"
                                + str(
                                    model_description["ModelDescription"][
                                        "OutputConfig"
                                    ]["S3Location"]["Prefix"]
                                )
                            )

                        print()
                print("-----------------\n")

            print()

        except ClientError as err:
            logger.exception("Problem listing projects: %s ", err.response["Message"])
            raise

    print("Done...")

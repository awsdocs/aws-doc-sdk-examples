# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier:  Apache-2.0
"""
Purpose: Displays a list of running Amazon Lookout for Vision
models across all accessible AWS Regions.
"""
import logging
import boto3
from boto3.session import Session

from botocore.exceptions import ClientError, EndpointConnectionError


logger = logging.getLogger(__name__)


def find_running_models_in_project(client, project_name):
    """
    Gets a list of running models in a project.
    :param lookoutvision_client: A Boto3 Amazon Lookout for Vision client.
    param project_name: The name of the project that you want to use.
    return: A list of running models. Empty if no models are
    running in the project.
    """
    
    logger.info("Finding running models in project: %s", project_name)
    running_models = []
    # Get a list of models in the current project.
    response = client.list_models(ProjectName=project_name)

    for model in response["Models"]:

        # Get model description and store hosted state, if model is running.
        model_description = client.describe_model(
            ProjectName=project_name, ModelVersion=model["ModelVersion"]
        )

        logger.info("Checking: %s",
                    model_description["ModelDescription"]["ModelArn"])

        if model_description["ModelDescription"]["Status"] == 'HOSTED':
            running_model = {
                "Project": project_name,
                "ARN": model_description["ModelDescription"]["ModelArn"],
                "Version": model_description["ModelDescription"]["ModelVersion"]
            }
            running_models.append(running_model)
            logger.info("Running model ARN: %s Version %s",
                        model_description["ModelDescription"]["ModelArn"],
                        model_description["ModelDescription"]["ModelVersion"])

    logger.info("Done finding running models in project: %s", project_name)

    return running_models



def display_running_models(running_model_regions):
    """
    Displays running model information.
    :param running_model_region: A list of AWS regions
    and models that are running within each AWS Region.
    """
    count = 0

    if len(running_model_regions) > 0:
        print("Running models.\n")
        for region in running_model_regions:
            print(region['Region'])
            for model in region['Models']:
                print(f"  Project: {model['Project']}")
                print(f"  Version: {model['Version']}")
                print(f"  ARN: {model['ARN']}\n")
                count += 1

    print(f"There are {count} running models")




def find_running_models():
    """
    Finds the running Lookout for Vision models across all accessible 
    AWS Regions.
    :return: A list of running running models.
    """
       
    running_models = []

    # Get a list of accessible regions.
    regions = Session().get_available_regions(service_name='lookoutvision')

    # Loop through each region and collect running models.
    for region in regions:
        logger.info("Checking %s", region)
        region_info = {}
        region_info['Region'] = region
        region_info['Models'] = []
        running_models_in_region = []

        lfv_client = boto3.client(
            "lookoutvision", region_name=region)

        # Get the projects in the current AWS region.
        projects = lfv_client.list_projects()['Projects']

        for project in projects:
            running_models_in_project = find_running_models_in_project(
                lfv_client, project["ProjectName"])
            for running_model in running_models_in_project:
                running_models_in_region.append(running_model)

            region_info['Models'] = running_models_in_region

        if len(region_info['Models']) > 0:
            running_models.append(region_info)
        
    return running_models
    

def main():

    logging.basicConfig(level=logging.INFO,
                        format="%(levelname)s: %(message)s")

    try:

        running_models=find_running_models()
        display_running_models(running_models)


    except TypeError as err:
        print("Couldn't get available regions: " + format(err))
    except ClientError as err:
        print("Service error occurred: " + format(err))
    except EndpointConnectionError:
        logger.info("Problem calling endpoint: %s", format(err))
        raise

        

if __name__ == "__main__":
    main()

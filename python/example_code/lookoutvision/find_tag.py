# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
Purpose
Shows how to find a tag value that's associated with models within
your Amazon Lookout for Vision projects.
To run: python find_tag.py tag tag_value
"""
import logging
import argparse
import boto3

from botocore.exceptions import ClientError


logger = logging.getLogger(__name__)


def find_tag(tags, key, value):
    """Finds a tag in supplied list of tags
    :param tags: a list of tags associated with an Amazon Lookout for Vision model.
    :param: key: the tag to search for.
    :param: value: the tag ket value to search for.
    :return: True if the tag value exists, otherwise False.
    """

    found = False
    for tag in tags:
        if key == tag["Key"]:
            logger.info("\t\tMatch found for tag: %s value: %s.", key, value)
            found = True
            break
    return found


def find_tag_in_projects(lookoutvision_client, key, value):
    """
    Finds Amazon Lookout for Vision models tagged with the supplied key and key value.
    :param lookoutvision_client: An Amazon Lookout for Vision boto3 client.
    :param key: The tag key to find.
    :param value: The value of the tag that you want to find.
    return: A list of matching model versions (and model projects) that were found.
    """
    try:

        found_tags = []
        found = False

        projects = lookoutvision_client.list_projects()
        # Iterate through each project and models within a project.
        for project in projects["Projects"]:
            logger.info("Searching project: %s ...", project["ProjectName"])

            response_models = lookoutvision_client.list_models(
                ProjectName=project["ProjectName"]
            )
            for model in response_models["Models"]:
                # Get model description
                model_description = lookoutvision_client.describe_model(
                    ProjectName=project["ProjectName"],
                    ModelVersion=model["ModelVersion"],
                )
                tags = lookoutvision_client.list_tags_for_resource(
                    ResourceArn=model_description["ModelDescription"]["ModelArn"]
                )

                logger.info(
                    "\tSearching model: %s for tag: %s value: %s.",
                    model_description["ModelDescription"]["ModelArn"],
                    key,
                    value,
                )
                # Check if tag exists
                if find_tag(tags["Tags"], key, value) is True:
                    found = True
                    logger.info(
                        "\t\tMATCH: Project: %s: model version %s",
                        project["ProjectName"],
                        model_description["ModelDescription"]["ModelVersion"],
                    )
                    found_tags.append(
                        {
                            "Project": project["ProjectName"],
                            "ModelVersion": model_description["ModelDescription"][
                                "ModelVersion"
                            ],
                        }
                    )

        if found is False:
            logger.info("No match for Tag %s with value %s.", key, value)
        return found_tags
    except ClientError as err:
        logger.info("Problem finding tags: %s. ", format(err))
        raise


def main():
    """
    Entry point for example."
    """
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    #Set up command line arguments.
    parser = argparse.ArgumentParser(usage=argparse.SUPPRESS)

    parser.add_argument("tag", help="The tag that you want to find.")
    parser.add_argument("value", help="The tag value that you want to find.")

    args = parser.parse_args()
    key = args.tag
    value = args.value

    print(
        "Searching your models for tag: {tag} with value: {value}.".format(
            tag=key, value=value
        )
    )

    lookoutvision_client = boto3.client("lookoutvision")

    # Get tagged models for all projects.
    tagged_models = find_tag_in_projects(lookoutvision_client, key, value)

    print("Matched models\n--------------")
    if len(tagged_models) > 0:
        for model in tagged_models:
            print(
                "Project: {project}. model version:{version}.".format(
                    project=model["Project"], version=model["ModelVersion"]
                )
            )
    else:
        print("No matches found.")


if __name__ == "__main__":
    main()

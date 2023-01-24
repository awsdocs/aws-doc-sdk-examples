# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.lookoutvision.GettingStarted]

"""
This example shows how to list your Amazon Lookout for Vision projects.
If you haven't previously created a project in the current AWS Region,
the response is an empty list, but does confirm that you can call the
 Lookout for Vision API.
"""
from botocore.exceptions import ClientError
import boto3

class GettingStarted:
    """ Getting Started class for Amazon Lookout for Vision """

    @staticmethod
    def list_projects(lookoutvision_client):
        """
        Lists information about the projects that are in in your AWS account
        and in the current AWS Region.

        : param lookoutvision_client: A Boto3 Lookout for Vision client.
        """
        try:
            response = lookoutvision_client.list_projects()
            for project in response["Projects"]:
                print("Project: " + project["ProjectName"])
                print("ARN: " + project["ProjectArn"])
                print()
            print("Done!")
        except ClientError as err:
            print(f"Couldn't list projects. \n{err}")
            raise


def main():
    """
    Entrypoint for script.
    """

    session = boto3.Session(profile_name='lookoutvision-access')
    lookoutvision_client = session.client("lookoutvision")

    GettingStarted.list_projects(lookoutvision_client)


if __name__ == "__main__":
    main()

# snippet-end:[python.example_code.lookoutvision.GettingStarted]

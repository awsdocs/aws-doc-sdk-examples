# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Audit Manager to create a
custom framework with all standard controls using AWS Security Hub as their data source.
"""

# snippet-start:[python.example_code.auditmanager.Scenario_CustomFrameworkFromSecurityHub]
import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class SecurityHub:
    def __init__(self, auditmanager_client):
        self.auditmanager_client = auditmanager_client

    def get_sechub_controls(self):
        """
        Gets the list of controls that use Security Hub as their data source.

        :return: The list of Security Hub controls.
        """
        print('-'*88)
        next_token = None
        page = 1
        sechub_control_list = []
        while True:
            print("Page [" + str(page) + "]")
            if next_token is None:
                control_list = self.auditmanager_client.list_controls(
                    controlType='Standard',
                    maxResults=100)
            else:
                control_list = self.auditmanager_client.list_controls(
                    controlType='Standard',
                    nextToken=next_token,
                    maxResults=100)
            print('Total controls found:', len(control_list.get('controlMetadataList')))
            for control in control_list.get('controlMetadataList'):
                control_details = self.auditmanager_client.get_control(
                    controlId=control.get('id')).get('control', {})
                if "AWS Security Hub" in control_details.get('controlSources'):
                    sechub_control_list.append({'id': control_details.get('id')})
            next_token = control_list.get('nextToken')
            if not next_token:
                break
            page += 1
        print('Number of Security Hub controls found: ', len(sechub_control_list))
        return sechub_control_list

    def create_custom_framework(self, am_controls):
        """
        Create a custom framework with a list of controls.

        :param am_controls: The list of controls to include in the framework.
        """
        try:
            print('Creating custom framework...')
            custom_framework = self.auditmanager_client.create_assessment_framework(
                name='All Security Hub Controls Framework',
                controlSets=[{'name': "Security-Hub", 'controls': am_controls}])
            print(f"Successfully created the custom framework: "
                  f"{custom_framework.get('framework').get('name')}: "
                  f"{custom_framework.get('framework').get('id')}")
            print('-' * 88)
        except ClientError:
            logger.exception("Failed to create custom framework.")
            raise


def run_demo():
    print('-' * 88)
    print("Welcome to the AWS Audit Manager Security Hub demo!")
    print('-' * 88)
    print(" This script creates a custom framework with all Security Hub controls.")
    print('-' * 88)
    sechub = SecurityHub(boto3.client('auditmanager'))
    am_controls = sechub.get_sechub_controls()
    sechub.create_custom_framework(am_controls)


if __name__ == '__main__':
    run_demo()
# snippet-end:[python.example_code.auditmanager.Scenario_CustomFrameworkFromSecurityHub]

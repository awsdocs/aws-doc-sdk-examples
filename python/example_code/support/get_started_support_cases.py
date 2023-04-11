# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Support to
do the following:

1.  Get and display services. Select a service from the list.
    2.  Select a category from the selected service.
    3.  Get and display severity levels and select a severity level from the list.
    4.  Create a support case using the selected service, category, and severity level.
    5.  Get and display a list of open support cases for the current day.
    6.  Create an attachment set with a sample text file to add to the case.
    7.  Add a communication with the attachment to the support case.
    8.  List the communications of the support case.
    9.  Describe the attachment set.
    10. Resolve the support case.
    11. Get a list of resolved cases for the current day.
"""

import json
import logging
import sys

import boto3
from botocore.exceptions import ClientError
from support_wrapper import SupportWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append('../..')
import demo_tools.question as q
from demo_tools.retries import wait

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.support.Scenario_GetStartedSupportCases]
class SupportCasesScenario:
    """Runs an interactive scenario that shows how to get started using AWS Support."""

    def __init__(self, support_wrapper):
        """
        :param support_wrapper: An object that wraps AWS Support actions.
        """
        self.support_wrapper = support_wrapper

    def display_and_select_service(self):
        """
        Lists support services and prompts the user to select one.

        :return: The support service selected by the user.
        """
        print('-' * 88)
        services_list = self.support_wrapper.describe_services('en')
        print(f"AWS Support client returned {len(services_list)} services.")
        print("Displaying first 10 services:")

        service_choices = [services_list[index]['name'] for index in range(10)]
        selected_index = q.choose(
            "Select an example support service by entering a number from the preceeding list:",
            service_choices)
        selected_service = services_list[selected_index]
        print('-' * 88)
        return selected_service

    def display_and_select_category(self, service):
        """
        Lists categories for a support service and prompts the user to select one.

        :return: The selected category.
        """
        print('-' * 88)
        print(f"Available support categories for Service {service['name']}:")
        categories_choices = [category['name'] for category in service['categories']]
        selected_index = q.choose(
            "Select an example support category by entering a number from the preceeding list:",
            categories_choices)
        selected_category = service['categories'][selected_index]
        print('-' * 88)
        return selected_category

    def display_and_select_severity(self):
        """
        Lists available severity levels and prompts the user to select one.

        :return: The selected severity level.
        """
        print('-' * 88)
        severity_levels_list = self.support_wrapper.describe_severity_levels('en')
        print(f"Available severity levels:")
        severity_choices = [level['name'] for level in severity_levels_list]
        selected_index = q.choose(
            "Select an example severity level by entering a number from the preceeding list:",
            severity_choices)
        selected_severity = severity_levels_list[selected_index]
        print('-' * 88)
        return selected_severity

    def create_example_case(self, service, category, severity_level):
        """
        Creates an example support case with the user's selections.

        :return: The caseId of the new support case.
        """
        print('-' * 88)
        print(f"Creating new case for service {service['name']}.")
        case_id = self.support_wrapper.create_case(service, category, severity_level)
        print(f"New case created with ID {case_id}.")
        print('-' * 88)
        return case_id

    def resolve_case(self, case_id):
        """
        Shows how to resolve an AWS Support case by its ID.

        :param case_id: The ID of the case to resolve.
        """
        print('-' * 88)
        print(f"Resolving case with ID {case_id}.")
        case_status = self.support_wrapper.resolve_case(case_id)
        print(f"Final case status is {case_status}.")
        print('-' * 88)

    def run_scenario(
            self):
        logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

        print('-'*88)
        print("Welcome to the AWS Support get started with support cases demo.")
        print('-'*88)

        selected_service = self.display_and_select_service()
        selected_category = self.display_and_select_category(selected_service)
        selected_severity = self.display_and_select_severity()
        new_case_id = self.create_example_case(selected_service, selected_category, selected_severity)
        self.resolve_case(new_case_id)

        print("\nThanks for watching!")
        print('-'*88)


if __name__ == '__main__':
    try:
        scenario = SupportCasesScenario(SupportWrapper.from_client())
        scenario.run_scenario()
    except Exception:
        logging.exception("Something went wrong with the demo.")
# snippet-end:[python.example_code.support.Scenario_GetStartedSupportCases]

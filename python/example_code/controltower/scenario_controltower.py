# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0


import logging
import sys
import datetime

import boto3
from botocore.exceptions import ClientError

from controltower_wrapper import ControlTowerWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append("../..")
import demo_tools.question as q  # noqa

logger = logging.getLogger(__name__)

# snippet-start:[python.example_code.controltower.ControlTowerScenario]
class ControlTowerScenario:
    stack_name = ""

    def __init__(self, controltower_wrapper, org_client):
        """
        :param controltower_wrapper: An instance of the ControlTowerWrapper class.
        :param org_client: A Boto3 Organization client.
        """
        self.controltower_wrapper = controltower_wrapper
        self.org_client = org_client
        self.stack = None
        self.ou_id = None
        self.ou_arn = None
        self.account_id = None
        self.landing_zone_id = None
        self.use_landing_zone = False

    def run_scenario(self):
        print("-" * 88)
        print(
            "\tWelcome to the AWS Control Tower with ControlCatalog example scenario."
        )
        print("-" * 88)

        print("This demo will walk you through working with AWS Control Tower for landing zones,")
        print("managing baselines, and working with controls.")

        self.account_id = boto3.client("sts").get_caller_identity()["Account"]

        print("Some demo operations require the use of a landing zone. "
              "You can use an existing landing zone or opt out of these operations in the demo."
              "For instructions on how to set up a landing zone, "
              "see https://docs.aws.amazon.com/controltower/latest/userguide/getting-started-from-console.html")
        # List available landing zones
        landing_zones = self.controltower_wrapper.list_landing_zones()
        if landing_zones:
            print("\nAvailable Landing Zones:")
            for i, lz in enumerate(landing_zones, 1):
                print(f"{i} {lz['arn']})")

            # Ask if user wants to use the first landing zone in the list
            if q.ask(
                f"Do you want to use the first landing zone in the list ({landing_zones[0]['arn']})? (y/n) ",
                q.is_yesno,
            ):
                self.use_landing_zone = True
                self.landing_zone_id = landing_zones[0]['arn']
                print(f"Using landing zone ID: {self.landing_zone_id})")
                # Set up organization and get Sandbox OU ID.
                sandbox_ou_id = self.setup_organization()
                # Store the OU ID for use in the CloudFormation template.
                self.ou_id = sandbox_ou_id
            elif q.ask(
                f"Do you want to use a different existing Landing Zone for this demo? (y/n) ",
                q.is_yesno,
            ):
                self.use_landing_zone = True
                self.landing_zone_id = q.ask("Enter landing zone id: ", q.non_empty)
                # Set up organization and get Sandbox OU ID.
                sandbox_ou_id = self.setup_organization()
                # Store the OU ID for use in the CloudFormation template.
                self.ou_id = sandbox_ou_id

        # List and Enable Baseline.
        control_tower_baseline = None
        baselines = self.controltower_wrapper.list_baselines()
        print("\nListing available Baselines:")
        for baseline in baselines:
            if baseline['name'] == 'AWSControlTowerBaseline':
                control_tower_baseline = baseline
            print(f"{baseline['name']}")

        if self.use_landing_zone:
            print("\nEnabling Control Tower Baseline")
            baseline_arn = self.controltower_wrapper.enable_baseline(
                self.ou_arn,
                control_tower_baseline['arn'],
                '4.0'
            )
            if baseline_arn:
                print(f"Enabled baseline ARN: {baseline_arn}")
            else:
                print("Baseline is already enabled for this target")

        # List and Enable Controls.
        print("Managing Controls:")
        controls = self.controltower_wrapper.list_controls()
        print("\nListing first 5 available Controls:")
        for i, control in enumerate(controls[:5], 1):
            print(f"{i}. {control['Name']}")

        if self.use_landing_zone:
            # Enable first control as an example.
            control_arn = controls[0]['Arn']
            target_ou = self.ou_arn

            print(f"\nEnabling control: {controls[0]['Name']} {control_arn}")
            operation_id = self.controltower_wrapper.enable_control(
                control_arn, target_ou)

            if operation_id:
                print(f"Enabling control with operation id {operation_id}")
            else:
                print("Control is already enabled for this target")
            # Wait for control operation to complete.

            if operation_id:
                while True:
                    status = self.controltower_wrapper.get_control_operation(operation_id)
                    print(f"Control operation status: {status}")
                    if status in ['SUCCEEDED', 'FAILED']:
                        break
                    datetime.time.sleep(30)

                if status == 'SUCCEEDED':
                    # Disable the control.
                    print("\nDisabling the control...")
                    operation_id = self.controltower_wrapper.disable_control(
                        control_arn, target_ou)
                    print(f"Disable operation ID: {operation_id}")

            print("This concludes the scenario.")

            print("Thanks for watching!")
            print("-" * 88)

    def setup_organization(self):
        """
        Checks if the current account is part of an organization and creates one if needed.
        Also ensures a Sandbox OU exists and returns its ID.

        :return: The ID of the Sandbox OU
        """
        print("\nChecking organization status...")

        try:
            # Check if account is part of an organization
            org_response = self.org_client.describe_organization()
            org_id = org_response['Organization']['Id']
            print(f"Account is part of organization: {org_id}")

        except ClientError as error:
            if error.response['Error']['Code'] == 'AWSOrganizationsNotInUseException':
                print("No organization found. Creating a new organization...")
                try:
                    create_response = self.org_client.create_organization(
                        FeatureSet='ALL'
                    )
                    org_id = create_response['Organization']['Id']
                    print(f"Created new organization: {org_id}")

                    # Wait for organization to be available.
                    waiter = self.org_client.get_waiter('organization_active')
                    waiter.wait(
                        Organization=org_id,
                        WaiterConfig={'Delay': 5, 'MaxAttempts': 12}
                    )

                except ClientError as create_error:
                    logger.error(
                        "Couldn't create organization. Here's why: %s: %s",
                        create_error.response["Error"]["Code"],
                        create_error.response["Error"]["Message"]
                    )
                    raise
            else:
                logger.error(
                    "Couldn't describe organization. Here's why: %s: %s",
                    error.response["Error"]["Code"],
                    error.response["Error"]["Message"]
                )
                raise

        # Look for Sandbox OU.
        sandbox_ou_id = None
        paginator = self.org_client.get_paginator('list_organizational_units_for_parent')

        try:
            # Get root ID first.
            roots = self.org_client.list_roots()['Roots']
            if not roots:
                raise ValueError("No root found in organization")
            root_id = roots[0]['Id']

            # Search for existing Sandbox OU.
            print("Checking for Sandbox OU...")
            for page in paginator.paginate(ParentId=root_id):
                for ou in page['OrganizationalUnits']:
                    if ou['Name'] == 'Sandbox':
                        sandbox_ou_id = ou['Id']
                        self.ou_arn = ou['Arn']
                        print(f"Found existing Sandbox OU: {sandbox_ou_id}")
                        break
                if sandbox_ou_id:
                    break

            # Create Sandbox OU if it doesn't exist.
            if not sandbox_ou_id:
                print("Creating Sandbox OU...")
                create_ou_response = self.org_client.create_organizational_unit(
                    ParentId=root_id,
                    Name='Sandbox'
                )
                sandbox_ou_id = create_ou_response['OrganizationalUnit']['Id']
                print(f"Created new Sandbox OU: {sandbox_ou_id}")

                # Wait for OU to be available.
                waiter = self.org_client.get_waiter('organizational_unit_active')
                waiter.wait(
                    OrganizationalUnitId=sandbox_ou_id,
                    WaiterConfig={'Delay': 5, 'MaxAttempts': 12}
                )

        except ClientError as error:
            logger.error(
                "Couldn't set up Sandbox OU. Here's why: %s: %s",
                error.response["Error"]["Code"],
                error.response["Error"]["Message"]
            )
            raise

        return sandbox_ou_id


if __name__ == "__main__":
    try:
        org = boto3.client("organizations")
        control_tower_wrapper = ControlTowerWrapper.from_client()

        scenario = ControlTowerScenario(control_tower_wrapper, org)
        scenario.run_scenario()
    except Exception:
        logging.exception("Something went wrong with the scenario.")
# snippet-end:[python.example_code.controltower.ControlTowerScenario]
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon EventBridge Scheduler to schedule
and receive events.
"""

import logging
import sys
from eventbridge_scheduler import EventBridgeSchedulerWrapper
from boto3 import client
from botocore.exceptions import ClientError
from boto3.resources.base import ServiceResource
import os

import boto3

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append("../..")
import demo_tools.question as q

DASHES = "-" * 80

logger = logging.getLogger(__name__)

class EventBridgeSchedulerWorkflow:
    """
        A scenario that demonstrates how to use Boto3 to schedule and receive events using
        the Amazon EventBridge Scheduler.
        """
    def __init__(self, eventbridge_scheduler : EventBridgeSchedulerWrapper, cloud_formation_client : ServiceResource):
        self.eventbridge_scheduler = eventbridge_scheduler
        self.cloud_formation_resource = cloud_formation_client
        self.stack :  ServiceResource = None
        self.schedule_group_name = "workflow-schedules-group"
        self.schedule_group_created = False

    def run(self) -> None:
        """
        Runs the scenario.
        """
        print(DASHES)

        print("Welcome to the Amazon EventBridge Scheduler Workflow.");
        print(DASHES)
        print(DASHES)

        self.prepare_application()

        print(DASHES)
        print(DASHES)

        self.cleanup()

    def prepare_application(self):
        """
        Prepares the application by prompting the user setup information, deploying a CloudFormation stack and
        creating a schedule group.
        """
        print("Preparing the application...");
        print("\nThis example creates resources in a CloudFormation stack, including an SNS topic" +
              "\nthat will be subscribed to the EventBridge Scheduler events. " +
              "\n\nYou will need to confirm the subscription in order to receive event emails. ")

        email_address = "meyertst@amazon.com" # q.ask("Enter an email address to use for event subscriptions: ")
        stack_name = "python-test" # q.ask("Enter a name for the AWS Cloud Formation Stack: ")

        script_directory = os.path.dirname(os.path.abspath(sys.argv[0]))
        template_file = os.path.join(script_directory, "cfn_template.yaml")

        parameters = [
            {
                'ParameterKey': 'email',
                'ParameterValue': email_address
            }
        ]

        self.stack = self.deploy_cloudformation_stack(stack_name, template_file, parameters)

        self.eventbridge_scheduler.create_schedule_group(self.schedule_group_name)
        self.schedule_group_created = True

    def create_one_time_schedule(self):
        """
        Creates a one-time schedule to send an initial event.
        """
        schedule_name = q.ask("Enter a name for the one-time schedule:");

        print(f"Creating a one-time schedule named '{schedule_name}' " +
          f"\nto send an initial event in 1 minute with a flexible time window...");

    var
    createSuccess = await _schedulerWrapper.CreateScheduleAsync(

        print(f"Subscription email will receive an email from this event.");
        print(f"You must confirm your subscription to receive event emails.");
        print(f"One-time schedule '{schedule_name}' created successfully.");


def deploy_cloudformation_stack(self, stack_name :str, template_file : str, parameters :[dict[str, str]]) -> ServiceResource:
        """
        Deploys prerequisite resources used by the scenario. The resources are
        defined in the associated `setup.yaml` AWS CloudFormation script and are deployed
        as a CloudFormation stack, so they can be easily managed and destroyed.
        """


        with open(
                template_file
        ) as setup_file:
            setup_template = setup_file.read()
        print(f"Creating stack {stack_name}.")
        stack = self.cloud_formation_resource.create_stack(
            StackName=stack_name,
            TemplateBody=setup_template,
            Capabilities=["CAPABILITY_NAMED_IAM"],
            Parameters=parameters,
        )
        print("\t\tWaiting for stack to deploy. This typically takes a minute or two.")
        waiter = self.cloud_formation_resource.meta.client.get_waiter("stack_create_complete")
        waiter.wait(StackName=stack.name)
        stack.load()
        print(f"\t\tStack status: {stack.stack_status}")

        return stack

    def destroy_cloudformation_stack(self, stack : ServiceResource) -> None:
        """
        Destroys the resources managed by the CloudFormation stack, and the CloudFormation
        stack itself.

        :param stack: The CloudFormation stack that manages the example resources.
        """
        print(f"\t\tDeleting {stack.name}.")
        stack.delete()
        print("\t\tWaiting for stack removal. This may take a few minutes.")
        waiter = self.cloud_formation_resource.meta.client.get_waiter("stack_delete_complete")
        waiter.wait(StackName=stack.name)
        print("\t\tStack delete complete.")

    def cleanup(self):
        """
        Deletes the CloudFormation stack and the resources created for the demo.
        """
        print("\nCleaning up resources...")
        if self.schedule_group_created:
            self.schedule_group_created = False
            self.eventbridge_scheduler.delete_schedule_group(self.schedule_group_name)


        if self.stack is not None:
            stack = self.stack
            self.stack = None
            self.destroy_cloudformation_stack(stack)
        print("\t\tStack deleted, demo complete.")

if __name__ == "__main__":
    eventbridge_wrapper : EventBridgeSchedulerWrapper = None
    try:
        eventbridge_wrapper = EventBridgeSchedulerWrapper.from_client()
        cloud_formation_client = boto3.resource("cloudformation")
        demo = EventBridgeSchedulerWorkflow(eventbridge_wrapper, cloud_formation_client)
        demo.run()

    except Exception:
        if eventbridge_wrapper is not None:
            eventbridge_wrapper.cleanup()
        logging.exception("Something went wrong with the demo!")

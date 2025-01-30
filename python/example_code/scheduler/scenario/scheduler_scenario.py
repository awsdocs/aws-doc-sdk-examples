# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon EventBridge Scheduler to schedule
and receive events.
"""

import logging
import sys
from datetime import datetime, timedelta, timezone
import os
from boto3.resources.base import ServiceResource
from boto3 import resource

script_dir = os.path.dirname(os.path.abspath(__file__))

# Add relative path to include SchedulerWrapper.
sys.path.append(os.path.dirname(script_dir))
from scheduler_wrapper import SchedulerWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../../.."))
import demo_tools.question as q

DASHES = "-" * 80

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.scheduler.FeatureScenario]
class SchedulerScenario:
    """
    A scenario that demonstrates how to use Boto3 to schedule and receive events using
    the Amazon EventBridge Scheduler.
    """

    def __init__(
        self,
        scheduler_wrapper: SchedulerWrapper,
        cloud_formation_resource: ServiceResource,
    ):
        self.eventbridge_scheduler = scheduler_wrapper
        self.cloud_formation_resource = cloud_formation_resource
        self.stack: ServiceResource = None
        self.schedule_group_name = None
        self.sns_topic_arn = None
        self.role_arn = None

    def run(self) -> None:
        """
        Runs the scenario.
        """

        print(DASHES)
        print("Welcome to the Amazon EventBridge Scheduler Workflow.")
        print(DASHES)

        print(DASHES)
        self.prepare_application()
        print(DASHES)

        print(DASHES)
        self.create_one_time_schedule()
        print(DASHES)

        print(DASHES)
        self.create_recurring_schedule()
        print(DASHES)

        print(DASHES)
        if q.ask(
            "Do you want to delete all resources created by this workflow? (y/n) ",
            q.is_yesno,
        ):
            self.cleanup()
        print(DASHES)

        print("Amazon EventBridge Scheduler workflow completed.")

    def prepare_application(self) -> None:
        """
        Prepares the application by prompting the user setup information, deploying a CloudFormation stack and
        creating a schedule group.
        """
        print("Preparing the application...")
        print(
            "\nThis example creates resources in a CloudFormation stack, including an SNS topic"
            + "\nthat will be subscribed to the EventBridge Scheduler events. "
            + "\n\nYou will need to confirm the subscription in order to receive event emails. "
        )

        email_address = q.ask("Enter an email address to use for event subscriptions: ")
        stack_name = q.ask("Enter a name for the AWS Cloud Formation Stack: ")

        template_file = SchedulerScenario.get_template_as_string()

        parameters = [{"ParameterKey": "email", "ParameterValue": email_address}]

        self.stack = self.deploy_cloudformation_stack(
            stack_name, template_file, parameters
        )
        outputs = self.stack.outputs
        for output in outputs:
            if output.get("OutputKey") == "RoleARN":
                self.role_arn = output.get("OutputValue")
            elif output.get("OutputKey") == "SNStopicARN":
                self.sns_topic_arn = output.get("OutputValue")

        if not self.sns_topic_arn or not self.role_arn:
            error_string = f"""
            Failed to retrieve required outputs from CloudFormation stack.
            'sns_topic_arn'={self.sns_topic_arn}, 'role_arn'={self.role_arn}
            """
            logger.error(error_string)
            raise ValueError(error_string)

        print(f"Stack output RoleARN: {self.role_arn}")
        print(f"Stack output SNStopicARN: a")
        schedule_group_name = "scenario-schedules-group"
        schedule_group_arn = self.eventbridge_scheduler.create_schedule_group(
            schedule_group_name
        )
        print(
            f"Successfully created schedule group '{self.schedule_group_name}': {schedule_group_arn}."
        )
        self.schedule_group_name = schedule_group_name
        print("Application preparation complete.")

    def create_one_time_schedule(self) -> None:
        """
        Creates a one-time schedule to send an initial event.
        """
        schedule_name = q.ask("Enter a name for the one-time schedule:")

        scheduled_time = datetime.now(timezone.utc) + timedelta(minutes=1)
        formatted_scheduled_time = scheduled_time.strftime("%Y-%m-%dT%H:%M:%S")

        print(
            f"Creating a one-time schedule named '{schedule_name}' "
            + f"\nto send an initial event in 1 minute with a flexible time window..."
        )

        schedule_arn = self.eventbridge_scheduler.create_schedule(
            schedule_name,
            f"at({formatted_scheduled_time})",
            self.schedule_group_name,
            self.sns_topic_arn,
            self.role_arn,
            f"One time scheduled event test from schedule {schedule_name}.",
            delete_after_completion=True,
            use_flexible_time_window=True,
        )
        print(
            f"Successfully created schedule '{schedule_name}' in schedule group 'scenario-schedules-group': {schedule_arn}."
        )
        print(f"Subscription email will receive an email from this event.")
        print(f"You must confirm your subscription to receive event emails.")
        print(f"One-time schedule '{schedule_name}' created successfully.")

    def create_recurring_schedule(self) -> None:
        """
        Create a recurring schedule to send events at a specified rate in minutes.
        """

        print("Creating a recurring schedule to send events for one hour...")
        schedule_name = q.ask("Enter a name for the recurring schedule: ")
        schedule_rate_in_minutes = q.ask(
            "Enter the desired schedule rate (in minutes): ", q.is_int
        )

        schedule_arn = self.eventbridge_scheduler.create_schedule(
            schedule_name,
            f"rate({schedule_rate_in_minutes} minutes)",
            self.schedule_group_name,
            self.sns_topic_arn,
            self.role_arn,
            f"Recurrent event test from schedule {schedule_name}.",
        )

        print(
            f"Successfully created schedule '{schedule_name}' in schedule group 'scenario-schedules-group': {schedule_arn}."
        )
        print(f"Subscription email will receive an email from this event.")
        print(f"You must confirm your subscription to receive event emails.")

        if q.ask(
            f"Are you ready to delete the '{schedule_name}' schedule? (y/n)", q.is_yesno
        ):
            self.eventbridge_scheduler.delete_schedule(
                schedule_name, self.schedule_group_name
            )

    def deploy_cloudformation_stack(
        self, stack_name: str, cfn_template: str, parameters: [dict[str, str]]
    ) -> ServiceResource:
        """
        Deploys prerequisite resources used by the scenario. The resources are
        defined in the associated `cfn_template.yaml` AWS CloudFormation script and are deployed
        as a CloudFormation stack, so they can be easily managed and destroyed.

        :param stack_name: The name of the CloudFormation stack.
        :param cfn_template: The CloudFormation template as a string.
        :param parameters: The parameters for the CloudFormation stack.
        :return: The CloudFormation stack resource.
        """
        print(f"Deploying CloudFormation stack: {stack_name}.")
        stack = self.cloud_formation_resource.create_stack(
            StackName=stack_name,
            TemplateBody=cfn_template,
            Capabilities=["CAPABILITY_NAMED_IAM"],
            Parameters=parameters,
        )
        print(f"CloudFormation stack creation started: {stack_name}")
        print("Waiting for CloudFormation stack creation to complete...")
        waiter = self.cloud_formation_resource.meta.client.get_waiter(
            "stack_create_complete"
        )
        waiter.wait(StackName=stack.name)
        stack.load()
        print("CloudFormation stack creation complete.")

        return stack

    def destroy_cloudformation_stack(self, stack: ServiceResource) -> None:
        """
        Destroys the resources managed by the CloudFormation stack, and the CloudFormation
        stack itself.

        :param stack: The CloudFormation stack that manages the example resources.
        """
        print(
            f"CloudFormation stack '{stack.name}' is being deleted. This may take a few minutes."
        )
        stack.delete()
        waiter = self.cloud_formation_resource.meta.client.get_waiter(
            "stack_delete_complete"
        )
        waiter.wait(StackName=stack.name)
        print(f"CloudFormation stack '{stack.name}' has been deleted.")

    def cleanup(self) -> None:
        """
        Deletes the CloudFormation stack and the resources created for the demo.
        """

        if self.schedule_group_name:
            schedule_group_name = self.schedule_group_name
            self.schedule_group_name = None
            self.eventbridge_scheduler.delete_schedule_group(schedule_group_name)
            print(f"Successfully deleted schedule group '{schedule_group_name}'.")

        if self.stack is not None:
            stack = self.stack
            self.stack = None
            self.destroy_cloudformation_stack(stack)
        print("Stack deleted, demo complete.")

    @staticmethod
    def get_template_as_string() -> str:
        """
        Returns a string containing this scenario's CloudFormation template.
        """
        script_directory = os.path.dirname(os.path.abspath(__file__))
        template_file_path = os.path.join(script_directory, "cfn_template.yaml")
        file = open(template_file_path, "r")
        return file.read()


if __name__ == "__main__":
    demo: SchedulerScenario = None
    try:
        scheduler_wrapper = SchedulerWrapper.from_client()
        cloud_formation_resource = resource("cloudformation")
        demo = SchedulerScenario(scheduler_wrapper, cloud_formation_resource)
        demo.run()

    except Exception as exception:
        logging.exception("Something went wrong with the demo!")
        if demo is not None:
            demo.cleanup()

# snippet-end:[python.example_code.scheduler.FeatureScenario]

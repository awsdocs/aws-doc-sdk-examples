# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS IoT SiteWise to manage IoT physical assets.
"""

import logging
import sys
import time
import os
from boto3.resources.base import ServiceResource
from boto3 import resource
from botocore.exceptions import ClientError

script_dir = os.path.dirname(os.path.abspath(__file__))

# Add relative path to include IoTSitewiseWrapper.
sys.path.append(os.path.dirname(script_dir))
from iotsitewise_wrapper import IoTSitewiseWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../.."))
import demo_tools.question as q

logger = logging.getLogger(__name__)

no_art = False  # 'no_art' suppresses 'art' to improve accessibility.
def print_dashes():
    """
    Print a line of dashes to separate sections of the output.
    """
    if not no_art:
        print("-" * 80)

use_press_enter_to_continue = False
def press_enter_to_continue():
    if use_press_enter_to_continue:
        q.ask("Press Enter to continue...")


# snippet-start:[python.example_code.scheduler.FeatureScenario]
class IoTSitewiseGettingStarted:
    """
    A scenario that demonstrates how to use Boto3 to manage IoT physical assets using
    the AWS IoT SiteWise.
    """

    def __init__(
        self,
        iot_sitewise_wrapper: IoTSitewiseWrapper,
        cloud_formation_resource: ServiceResource,
    ):
        self.iot_sitewise_wrapper = iot_sitewise_wrapper
        self.cloud_formation_resource = cloud_formation_resource
        self.stack: ServiceResource = None
        self.asset_model_id: str = None
        self.asset_id = None

    def run(self) -> None:
        """
        Runs the scenario.
        """
        print("""
AWS IoT SiteWise is a fully managed software-as-a-service (SaaS) that
makes it easy to collect, store, organize, and monitor data from industrial equipment and processes.
It is designed to help industrial and manufacturing organizations collect data from their equipment and
processes, and use that data to make informed decisions about their operations.

One of the key features of AWS IoT SiteWise is its ability to connect to a wide range of industrial
equipment and systems, including programmable logic controllers (PLCs), sensors, and other
industrial devices. It can collect data from these devices and organize it into a unified data model,
making it easier to analyze and gain insights from the data. AWS IoT SiteWise also provides tools for
visualizing the data, setting up alarms and alerts, and generating reports.

Another key feature of AWS IoT SiteWise is its ability to scale to handle large volumes of data.
It can collect and store data from thousands of devices and process millions of data points per second,
making it suitable for large-scale industrial operations. Additionally, AWS IoT SiteWise is designed
to be secure and compliant, with features like role-based access controls, data encryption,
and integration with other AWS services for additional security and compliance features.

Let's get started...
        """);
        press_enter_to_continue()
        print_dashes()
        print(f"")
        print(f"Use AWS CloudFormation to create an IAM role that is required for this scenario.")
        template_file = IoTSitewiseGettingStarted.get_template_as_string()

        self.stack = self.deploy_cloudformation_stack(
            "python-iot-sitewise-basics", template_file
        )
        outputs = self.stack.outputs
        iamRole = None
        for output in outputs:
            if output.get("OutputKey") == "SitewiseRoleArn":
                iamRole = output.get("OutputValue")

        if iamRole is None:
            error_string = f"Failed to retrieve iamRole from CloudFormation stack."
            logger.error(error_string)
            raise ValueError(error_string)

        print(f"The ARN of the IAM role is {iamRole}")
        print_dashes()
        print_dashes()
        print(f"1. Create an AWS SiteWise Asset Model")
        print("""
An AWS IoT SiteWise Asset Model is a way to represent the physical assets, such as equipment,
processes, and systems, that exist in an industrial environment. This model provides a structured and
hierarchical representation of these assets, allowing users to define the relationships and double_properties
of each asset.

This scenario creates two asset model double_properties: temperature and humidity.
        """);
        press_enter_to_continue()
        asset_model_name = "MyAssetModel1"
        try:
            self.asset_model_id = self.iot_sitewise_wrapper.create_asset_model(
                asset_model_name
            )
            print(f"Asset Model successfully created. Asset Model ID: {self.asset_model_id}. ")
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceAlreadyExistsException":
                self.asset_model_id = self.get_model_id_for_model_name(
                    asset_model_name
                )
                print(f"Asset Model {asset_model_name} already exists. Asset Model ID: {self.asset_model_id}. ")
            else:
                raise

        press_enter_to_continue()
        print_dashes()
        print(f"2. Create an AWS IoT SiteWise Asset")
        print("""
The IoT SiteWise model that we just created defines the structure and metadata for your physical assets.
Now we create an asset from the asset model.
        
        """);
        print(f"Let's wait for the asset model to become active.")
        press_enter_to_continue()
        self.iot_sitewise_wrapper.wait_asset_model_active(self.asset_model_id)
        self.asset_id = self.iot_sitewise_wrapper.create_asset("MyAsset1", self.asset_model_id)
        self.iot_sitewise_wrapper.wait_asset_active(self.asset_id)
        print(f"Asset created with ID: {self.asset_id}")
        press_enter_to_continue()
        print_dashes()
        print_dashes()
        print(f"3. Retrieve the property ID values")
        print("""
To send data to an asset, we need to get the property ID values. In this scenario, we access the
temperature and humidity property ID values.
        """);
        press_enter_to_continue()
        property_ids = self.iot_sitewise_wrapper.list_asset_model_properties(self.asset_model_id)
        humidity_property_id = None
        temperature_property_id = None
        for property_id in property_ids:
            if property_id.get("name") == "humidity":
                humidity_property_id = property_id.get("id")
            elif property_id.get("name") == "temperature":
                temperature_property_id = property_id.get("id")
        if humidity_property_id is None or temperature_property_id is None:
            error_string = f"Failed to retrieve property IDs from Asset Model."
            logger.error(error_string)
            raise ValueError(error_string)

        print(f"The Humidity property Id is {humidity_property_id}")
        print(f"The Temperature property Id is {temperature_property_id}")
        press_enter_to_continue()
        print_dashes()
        print_dashes()
        print(f"4. Send data to an AWS IoT SiteWise Asset")
        print("""
By sending data to an IoT SiteWise Asset, you can aggregate data from
multiple sources, normalize the data into a standard format, and store it in a
centralized location. This makes it easier to analyze and gain insights from the data.

In this example, we generate sample temperature and humidity data and send it to the AWS IoT SiteWise asset.

        """);
        press_enter_to_continue()
        print(f"Data sent successfully.")
        press_enter_to_continue()
        print_dashes()
        print_dashes()
        print(f"5. Retrieve the value of the IoT SiteWise Asset property")
        print("""
IoT SiteWise is an AWS service that allows you to collect, process, and analyze industrial data
from connected equipment and sensors. One of the key benefits of reading an IoT SiteWise property
is the ability to gain valuable insights from your industrial data.
        
        """);
        press_enter_to_continue()
        # print(f"The value of this property is: {assetVal}")
        # press_enter_to_continue()
        # print(f"The value of this property is: {assetVal}")
        # print(f"The AWS resource was not found: {cause.getMessage()}")
        # logging.error("An unexpected error occurred: {}", cause.getMessage(), cause);
        press_enter_to_continue()
        print_dashes()
        print_dashes()
        print(f"6. Create an IoT SiteWise Portal")
        print("""
An IoT SiteWise Portal allows you to aggregate data from multiple industrial sources,
such as sensors, equipment, and control systems, into a centralized platform.
        """);
        press_enter_to_continue()
#        print(f"Portal created successfully. Portal ID {portalId}")
        press_enter_to_continue()
        print_dashes()
        print_dashes()
        print(f"7. Describe the Portal")
        print("""
In this step, we get a description of the portal and display the portal URL.
        """);
        press_enter_to_continue()
#        print(f"Portal URL: {portalUrl}")
        press_enter_to_continue()
        print_dashes()
        print_dashes()
        print(f"8. Create an IoT SiteWise Gateway")
        press_enter_to_continue()
#        print(f"Gateway creation completed successfully. id is {gatewayId}")
        print_dashes()
        print_dashes()
        print(f"9. Describe the IoT SiteWise Gateway")
        press_enter_to_continue()
        # print(f"Gateway Name: {response.gatewayName()}")
        # print(f"Gateway ARN: {response.gatewayArn()}")
        # print(f"Gateway Platform: {response.gatewayPlatform()}")
        # print(f"Gateway Creation Date: {response.creationDate()}")
        print_dashes()
        print_dashes()
        print(f"10. Delete the AWS IoT SiteWise Assets")
        print(f"Would you like to delete the IoT SiteWise Assets? (y/n)")
        print(f"You selected to delete the SiteWise assets.")
        press_enter_to_continue()
        # print(f"Portal {portalId} was deleted successfully.")
        # print(f"Gateway {gatewayId} was deleted successfully.")
        # print(f"Request to delete asset {assetId} sent successfully")
        print(f"Let's wait 1 minute for the asset to be deleted.")
        press_enter_to_continue()
        print(f"Delete the AWS IoT SiteWise Asset Model")
        print(f"Asset model deleted successfully.")
        press_enter_to_continue()
        print(f"The resources will not be deleted.")
        print_dashes()
        print_dashes()
        print(f"This concludes the AWS IoT SiteWise Scenario")
        print_dashes()
        print(f"Enter 'c' followed by <ENTER> to continue:")
        print(f"Continuing with the program...")
        print(f"Invalid input. Please try again.")
        print(f"Countdown complete!")
        self.cleanup()

    def cleanup(self) -> None:
        """
        Deletes the CloudFormation stack and the resources created for the demo.
        """

        if self.asset_id is not None:
            self.iot_sitewise_wrapper.delete_asset(self.asset_id)
            print(f"Deleted asset with id {self.asset_id}.")
            self.iot_sitewise_wrapper.wait_asset_deleted(self.asset_id)
            self.asset_id = None

        if self.asset_model_id is not None:
            self.iot_sitewise_wrapper.delete_asset_model(self.asset_model_id)
            print(f"Deleted asset model with id {self.asset_model_id}.")
            self.asset_model_id = None
        if self.stack is not None:
            stack = self.stack
            self.stack = None
            self.destroy_cloudformation_stack(stack)
        print("Stack deleted, demo complete.")


    def deploy_cloudformation_stack(
        self, stack_name: str, cfn_template: str
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

    @staticmethod
    def get_template_as_string() -> str:
        """
        Returns a string containing this scenario's CloudFormation template.
        """
        template_file_path = os.path.join(script_dir, "SitewiseRoles-template.yaml")
        file = open(template_file_path, "r")
        return file.read()

    def get_model_id_for_model_name(self, model_name: str) -> str:
        """
        Returns the model ID for the given model name.

        :param model_name: The name of the model.
        :return: The model ID.
        """
        model_id = None
        asset_models = self.iot_sitewise_wrapper.list_asset_models()
        for asset_model in asset_models:
            if asset_model["name"] == model_name:
                model_id = asset_model["id"]
                break
        return model_id

if __name__ == "__main__":
    demo: IoTSitewiseGettingStarted = None
    try:
        an_iot_sitewise_wrapper = IoTSitewiseWrapper.from_client()
        cloud_formation_resource = resource("cloudformation")
        demo = IoTSitewiseGettingStarted(an_iot_sitewise_wrapper, cloud_formation_resource)
        demo.run()

    except Exception as exception:
        logging.exception("Something went wrong with the demo!")
        if demo is not None:
            demo.cleanup()

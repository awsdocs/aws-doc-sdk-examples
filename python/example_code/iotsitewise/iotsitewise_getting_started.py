# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS IoT SiteWise to manage IoT physical assets.
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
from iotsitewise_wrapper import IoTSitewiseWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../../.."))
import demo_tools.question as q

DASHES = "-" * 80

logger = logging.getLogger(__name__)

# snippet-start:[python.example_code.scheduler.FeatureScenario]
class IoTSitewiseGettingStarted:
    """
    A scenario that demonstrates how to use Boto3 to manage IoT physical assets using
    the AWS IoT SiteWise.
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
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(f"")
        print(f"Use AWS CloudFormation to create an IAM role that is required for this scenario.")
        print(f"The ARN of the IAM role is {iamRole}")
        print(DASHES)
        print(DASHES)
        print(f"1. Create an AWS SiteWise Asset Model")
        print("""
        An AWS IoT SiteWise Asset Model is a way to represent the physical assets, such as equipment,
        processes, and systems, that exist in an industrial environment. This model provides a structured and
        hierarchical representation of these assets, allowing users to define the relationships and properties
        of each asset.
        
        This scenario creates two asset model properties: temperature and humidity.
        """);
        q.ask("Press Enter to continue...")
        print(f"Asset Model successfully created. Asset Model ID: {assetModelId}. ")
        print(f"The Asset Model {assetModelName} already exists. The id of the existing model is {assetModelId}. Moving on...")
        logging.error("An unexpected error occurred: " + cause.getMessage(), cause);
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(f"2. Create an AWS IoT SiteWise Asset")
        print("""
        The IoT SiteWise model that we just created defines the structure and metadata for your physical assets.
        Now we create an asset from the asset model.
        
        """);
        print(f"Let's wait 30 seconds for the asset to be ready.")
        q.ask("Press Enter to continue...")
        print(f"Asset created with ID: {assetId}")
        print(f"The asset model id was not found: {cause.getMessage()}")
        logging.error("An unexpected error occurred: {}", cause.getMessage(), cause);
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(DASHES)
        print(f"3. Retrieve the property ID values")
        print("""
        To send data to an asset, we need to get the property ID values. In this scenario, we access the
        temperature and humidity property ID values.
        """);
        q.ask("Press Enter to continue...")
        print(f"The Humidity property Id is {humPropId}")
        print(f"The Temperature property Id is {tempPropId}")
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(DASHES)
        print(f"4. Send data to an AWS IoT SiteWise Asset")
        print("""
        By sending data to an IoT SiteWise Asset, you can aggregate data from
        multiple sources, normalize the data into a standard format, and store it in a
        centralized location. This makes it easier to analyze and gain insights from the data.
        
        In this example, we generate sample temperature and humidity data and send it to the AWS IoT SiteWise asset.
        
        """);
        q.ask("Press Enter to continue...")
        print(f"Data sent successfully.")
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(DASHES)
        print(f"5. Retrieve the value of the IoT SiteWise Asset property")
        print("""
        IoT SiteWise is an AWS service that allows you to collect, process, and analyze industrial data
        from connected equipment and sensors. One of the key benefits of reading an IoT SiteWise property
        is the ability to gain valuable insights from your industrial data.
        
        """);
        q.ask("Press Enter to continue...")
        print(f"The value of this property is: {assetVal}")
        q.ask("Press Enter to continue...")
        print(f"The value of this property is: {assetVal}")
        print(f"The AWS resource was not found: {cause.getMessage()}")
        logging.error("An unexpected error occurred: {}", cause.getMessage(), cause);
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(DASHES)
        print(f"6. Create an IoT SiteWise Portal")
        print("""
        An IoT SiteWise Portal allows you to aggregate data from multiple industrial sources,
        such as sensors, equipment, and control systems, into a centralized platform.
        """);
        q.ask("Press Enter to continue...")
        print(f"Portal created successfully. Portal ID {portalId}")
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(DASHES)
        print(f"7. Describe the Portal")
        print("""
        In this step, we get a description of the portal and display the portal URL.
        """);
        q.ask("Press Enter to continue...")
        print(f"Portal URL: {portalUrl}")
        q.ask("Press Enter to continue...")
        print(DASHES)
        print(DASHES)
        print(f"8. Create an IoT SiteWise Gateway")
        q.ask("Press Enter to continue...")
        print(f"Gateway creation completed successfully. id is {gatewayId}")
        print(DASHES)
        print(DASHES)
        print(f"9. Describe the IoT SiteWise Gateway")
        q.ask("Press Enter to continue...")
        print(f"Gateway Name: {response.gatewayName()}")
        print(f"Gateway ARN: {response.gatewayArn()}")
        print(f"Gateway Platform: {response.gatewayPlatform()}")
        print(f"Gateway Creation Date: {response.creationDate()}")
        print(DASHES)
        print(DASHES)
        print(f"10. Delete the AWS IoT SiteWise Assets")
        print(f"Would you like to delete the IoT SiteWise Assets? (y/n)")
        print(f"You selected to delete the SiteWise assets.")
        q.ask("Press Enter to continue...")
        print(f"Portal {portalId} was deleted successfully.")
        print(f"Gateway {gatewayId} was deleted successfully.")
        print(f"Request to delete asset {assetId} sent successfully")
        print(f"Let's wait 1 minute for the asset to be deleted.")
        q.ask("Press Enter to continue...")
        print(f"Delete the AWS IoT SiteWise Asset Model")
        print(f"Asset model deleted successfully.")
        q.ask("Press Enter to continue...")
        print(f"The resources will not be deleted.")
        print(DASHES)
        print(DASHES)
        print(f"This concludes the AWS IoT SiteWise Scenario")
        print(DASHES)
        print(f"Enter 'c' followed by <ENTER> to continue:")
        print(f"Continuing with the program...")
        print(f"Invalid input. Please try again.")
        print(f"Countdown complete!")
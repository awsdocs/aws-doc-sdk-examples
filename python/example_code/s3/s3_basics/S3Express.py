# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
import sys
import os
import uuid
from boto3.resources.base import ServiceResource
from boto3 import resource
from boto3 import client
from botocore.exceptions import ClientError, ParamValidationError

script_dir = os.path.dirname(os.path.abspath(__file__))

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../../.."))
import demo_tools.question as q

no_art = False  # 'no_art' suppresses 'art' to improve accessibility.
logger = logging.getLogger(__name__)

def print_dashes():
    """
    Print a line of dashes to separate sections of the output.
    """
    if not no_art:
        print("-" * 80)


# snippet-start:[python.example_code.s3.s3_express_basics]
class S3ExpressScenario:
    """Runs an interactive scenario that shows how to get started with S3 Express."""

    def __init__(
        self,
        cloud_formation_resource: ServiceResource,
        ec2_client: client,
        iam_client: client,
            region: str = None
    ):
        self.cloud_formation_resource = cloud_formation_resource
        self.ec2_client = ec2_client
        self.iam_client = iam_client
        self.region = region if region else ec2_client.meta.region_name
        self.stack = None
        self.vpc_id  = None
        self.vcp_endpoint_id  = None
        self.regular_bucket_name  = None
        self.directory_bucket_name = None
        self.s3_express_client = None
        self.s3_regular_client = None


    def s3_express_scenario(self):
        """
        Runs the scenario.
        """
        print("")
        print_dashes()
        print("Welcome to the Amazon S3 Express Basics demo using Python (Boto 3)!")
        print_dashes()
        print(
            """
Let's get started! First, please note that S3 Express One Zone works best when working within the AWS infrastructure,
specifically when working in the same Availability Zone. To see the best results in this example, and when you implement
Directory buckets into your infrastructure, it is best to put your Compute resources in the same AZ as your Directory
bucket.
    """
        )
        q.ask("Press Enter to continue...")

        # 1. Configure a gateway VPC endpoint. This is the recommended method to allow S3 Express One Zone traffic without
        # the need to pass through an internet gateway or NAT device.
        print("")
        print(
            "1. First, we'll set up a new VPC and VPC Endpoint if this program is running in an EC2 instance in the same AZ as your Directory buckets will be."
        )
        print(
            "Are you running this in an EC2 instance located in the same AZ as your intended Directory buckets?"
        )
        if q.ask("Do you want to setup a VPC Endpoint? (y/n) "):
            print(
                "Great! Let's set up a VPC, retrieve the Route Table from it, and create a VPC Endpoint to connect the S3 Client to."
            )
            self.setup_vpc()
            q.ask("Press Enter to continue...")
        else:
            print("Skipping the VPC setup. Don't forget to use this in production!")

        print("")
        print("2. Policies, users, and roles with CDK.")
        print(
            "Now, we'll set up some policies, roles, and a user. This user will only have permissions to do S3 Express One Zone actions."
        )
        q.ask("Press Enter to continue...")
        stack_name = f"cfn-stack-s3-express-basics--{uuid.uuid4()}"
        template_as_string = S3ExpressScenario.get_template_as_string()
        self.stack = self.deploy_cloudformation_stack(stack_name, template_as_string)
        regular_user_name = None
        express_user_name = None

        outputs = self.stack.outputs
        for output in outputs:
            if output.get("OutputKey") == "RegularUser":
                regular_user_name = output.get("OutputValue")
            elif output.get("OutputKey") == "ExpressUser":
                express_user_name = output.get("OutputValue")

        if not regular_user_name or not express_user_name:
            error_string = f"""
            Failed to retrieve required outputs from CloudFormation stack.
            'regular_user_name'={regular_user_name}, 'express_user_name'={express_user_name}
            """
            logger.error(error_string)
            raise ValueError(error_string)

        regular_credentials = self.create_access_key(regular_user_name)
        express_credentials = self.create_access_key(express_user_name)

        # 3. Create an additional client using the credentials with S3 Express permissions.
        print("")
        print(
            "3. Create an additional client using the credentials with S3 Express permissions."
        )
        print(
            "This client is created with the credentials associated with the user account with the S3 Express policy attached, so it can perform S3 Express operations."
        )
        q.ask("Press Enter to continue...")
        self.s3_regular_client = client(service_name = "s3", region_name = self.region,
                                   aws_access_key_id=regular_credentials['AccessKeyId'],
                                   aws_secret_access_key=regular_credentials['SecretAccessKey'])
        self.s3_express_client = client(service_name="s3", region_name=self.region,
                                     aws_access_key_id=express_credentials['AccessKeyId'],
                                     aws_secret_access_key=express_credentials['SecretAccessKey'])
        print(
            "All the roles and policies were created an attached to the user. Then, a new S3 Client and Service were created using that user's credentials."
        )
        print(
            "We can now use this client to make calls to S3 Express operations. Keeping permissions in mind (and adhering to least-privilege) is crucial to S3 Express."
        )
        q.ask("Press Enter to continue...")

        # 4. Create two buckets.
        print("")
        print("3. Create two buckets.")
        print(
            "Now we will create a Directory bucket, which is the linchpin of the S3 Express One Zone service."
        )
        print(
            "Directory buckets behave in different ways from regular S3 buckets, which we will explore here."
        )
        print(
            "We'll also create a normal bucket, put an object into the normal bucket, and copy it over to the Directory bucket."
        )
        # Create a directory bucket. These are different from normal S3 buckets in subtle ways.
        bucket_prefix = q.ask("Enter a bucket name prefix that will be used for both buckets: ", q.non_empty)
        availability_zone = self.select_availability_zone_id(self.region)

        # Construct the parts of a directory bucket name that is made unique with a UUID string.
        directory_bucket_suffix = f"--{availability_zone['ZoneId']}--x-s3"
        max_uuid_length = 63 - len(bucket_prefix) - len(directory_bucket_suffix) - 1
        bucket_uuid = str(uuid.uuid4()).replace('-', '')[:max_uuid_length]

        directory_bucket_name = f"{bucket_prefix}-{bucket_uuid}{directory_bucket_suffix}"
        regular_bucket_name = f"{bucket_prefix}-regular-{bucket_uuid}"
        configuration = { 'Bucket': { 'Type' : 'Directory',
                                      'DataRedundancy' : 'SingleAvailabilityZone'},
                          'Location' : { 'Name' : availability_zone['ZoneId'],
                                         'Type' :  'AvailabilityZone'}
                          }

        self.create_bucket(self.s3_express_client, directory_bucket_name, configuration)
        print(f"Created directory bucket, '{directory_bucket_name}'")
        self.directory_bucket_name = directory_bucket_name
        self.create_bucket(self.s3_regular_client, regular_bucket_name)
        print(f"Created regular bucket, '{regular_bucket_name}'")
        self.regular_bucket_name = regular_bucket_name

        q.ask("Press Enter to continue...")
        print(
            "Now, let's create the actual Directory bucket, as well as a regular bucket."
        )
        q.ask("Press Enter to continue...")
        print("Great! Both buckets were created.")
        q.ask("Press Enter to continue...")
        print("")
        print("5. Create an object and copy it over.")
        print(
            "We'll create a basic object consisting of some text and upload it to the normal bucket."
        )
        print(
            "Next, we'll copy the object into the Directory bucket using the regular client."
        )
        print(
            "This works fine, because Copy operations are not restricted for Directory buckets."
        )
        q.ask("Press Enter to continue...")
        print(
            "It worked! It's important to remember the user permissions when interacting with Directory buckets."
        )
        print(
            "Instead of validating permissions on every call as normal buckets do, Directory buckets utilize the user credentials and session token to validate."
        )
        print(
            "This allows for much faster connection speeds on every call. For single calls, this is low, but for many concurrent calls, this adds up to a lot of time saved."
        )
        q.ask("Press Enter to continue...")
        print("")
        print("6. Demonstrate performance difference.")
        print(
            "Now, let's do a performance test. We'll download the same object from each bucket $downloads times and compare the total time needed. Note: the performance difference will be much more pronounced if this example is run in an EC2 instance in the same AZ as the bucket."
        )

        # $downloadChoice = testable_readline(
        #     "If you would like to download each object $downloads times, press enter. Otherwise, enter a custom amount and press enter.");

        print(
            "The directory bucket took $directoryTimeDiff nanoseconds, while the normal bucket took $normalTimeDiff."
        )
        # print("That's a difference of ".($normalTimeDiff - $directoryTimeDiff)." nanoseconds, or ".(
        #     ($normalTimeDiff - $directoryTimeDiff) / 1000000000).
        # " seconds.")
        q.ask("Press Enter to continue...")
        print("")
        print("7. Populate the buckets to show the lexicographical difference.")
        print(
            "Now let's explore how Directory buckets store objects in a different manner to regular buckets."
        )
        print('The key is in the name "Directory!"')
        print(
            "Where regular buckets store their key/value pairs in a flat manner, Directory buckets use actual directories/folders."
        )
        print(
            "This allows for more rapid indexing, traversing, and therefore retrieval times!"
        )
        print(
            "The more segmented your bucket is, with lots of directories, sub-directories, and objects, the more efficient it becomes."
        )
        print(
            "This structural difference also causes ListObjects to behave differently, which can cause unexpected results."
        )
        print(
            "Let's add a few more objects with layered directories as see how the output of ListObjects changes."
        )
        q.ask("Press Enter to continue...")
        print("Directory bucket content")
        # print($result['Key'].
        # "")
        print("Normal bucket content")
        # print($result['Key'].
        # "")
        print(
            'Notice how the normal bucket lists objects in lexicographical order, while the directory bucket does not. This is because the normal bucket considers the whole "key" to be the object identifies, while the directory bucket actually creates directories and uses the object "key" as a path to the object.'
        )
        q.ask("Press Enter to continue...")
        print("")
        print(
            "That's it for our tour of the basic operations for S3 Express One Zone."
        )

        # $cleanUp = testable_readline(
        #     "Would you like to delete all the resources created during this demo? Enter Y/y to delete all the resources.");
        self.cleanup()

    def cleanup(self):
        """
        Delete resources created by this scenario.
        """
        if self.directory_bucket_name is not None:
            self.delete_bucket_and_objects(self.s3_express_client, self.directory_bucket_name)
            self.directory_bucket_name = None

        if self.regular_bucket_name  is not None:
            self.delete_bucket_and_objects(self.s3_regular_client, self.regular_bucket_name)
            self.regular_bucket_name = None

        if self.stack is not None:
            self.destroy_cloudformation_stack(self.stack)
            self.stack = None

        self.tear_done_vpc()

    def create_access_key(self, user_name:str ) -> dict[str, any]:
        """
        Creates an access key for the user.
        :param user_name: The name of the user.
        :return: The access key for the user.
        """
        try:
            access_key = self.iam_client.create_access_key(UserName=user_name)
            return access_key["AccessKey"]
        except ClientError as client_error:
            logging.error(
                "Couldn't create the access key. Here's why: %s",
                client_error.response["Error"]["Message"],
            )
            raise

    @staticmethod
    def create_bucket(s3_client : client, bucket_name: str, bucket_configuration : dict[str, any] = None) -> None:
        """
        Creates a bucket.
        :param s3_client: The S3 client to use.
        :param bucket_name: The name of the bucket.
        :param bucket_configuration: The optional configuration for the bucket.
        """
        try:
            params = {"Bucket": bucket_name}
            if bucket_configuration:
                params["CreateBucketConfiguration"] = bucket_configuration

            s3_client.create_bucket(**params)
        except ClientError as client_error:
            logging.error(
                "Couldn't create the bucket %s. Here's why: %s",
                bucket_name,
                client_error.response["Error"]["Message"]
            )
            raise

    @staticmethod
    def delete_bucket_and_objects(s3_client : client, bucket_name: str) -> None:
        """
        Deletes a bucket and its objects.
        :param s3_client: The S3 client to use.
        :param bucket_name: The name of the bucket.
        """
        try:
            # Delete the objects in the bucket first. This is required for a bucket to be deleted.
            paginator = s3_client.get_paginator('list_objects_v2')
            page_iterator = paginator.paginate(Bucket=bucket_name)
            for page in page_iterator:
                if 'Contents' in page:
                    delete_keys = {'Objects': [{'Key': obj['Key']} for obj in page['Contents']]}
                    s3_client.delete_objects(Bucket=bucket_name, Delete=delete_keys)

            s3_client.delete_bucket(Bucket=bucket_name)
        except ClientError as client_error:
            logging.error(
                "Couldn't delete the bucket %s. Here's why: %s",
                bucket_name,
                client_error.response["Error"]["Message"]
            )

    def select_availability_zone_id(self, region :str) -> dict[str, any]:
        """
        Selects an availability zone.
        :param region: The region to select the availability zone from.
        :return: The availability zone dictionary.
        """
        try:
            response = self.ec2_client.describe_availability_zones(Filters=[
                {
                    'Name': 'region-name',
                    'Values': [region]
                }
            ])
            availability_zones = response["AvailabilityZones"]
            zone_names = [zone["ZoneName"] for zone in availability_zones]
            index = q.choose("Select an availability zone: ", zone_names)
            return availability_zones[index]
        except  ClientError as client_error:
            logging.error(
                "Couldn't describe availability zones. Here's why: %s",
                client_error.response["Error"]["Message"],
            )
            raise


    def deploy_cloudformation_stack(
        self, stack_name: str, cfn_template: str
    ) -> ServiceResource:
        """
        Deploys prerequisite resources used by the scenario. The resources are
        defined in the associated `cfn_template.yaml` AWS CloudFormation script and are deployed
        as a CloudFormation stack, so they can be easily managed and destroyed.

        :param stack_name: The name of the CloudFormation stack.
        :param cfn_template: The CloudFormation template as a string.
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
        script_directory = os.path.dirname(os.path.abspath(__file__))
        template_file_path = os.path.join(script_directory, "s3_express_template.yaml")
        file = open(template_file_path, "r")
        return file.read()

    def setup_vpc(self):
        cidr = "10.0.0.0/16"
        try:
            response = self.ec2_client.create_vpc(CidrBlock=cidr)
            self.vpc_id = response["Vpc"]["VpcId"]

            waiter = self.ec2_client.get_waiter("vpc_available")
            waiter.wait(VpcIds=[self.vpc_id])
            print(f"Created vpc {self.vpc_id}")

        except ClientError as client_error:
            logging.error(
                "Couldn't create the vpc. Here's why: %s",
                client_error.response["Error"]["Message"],
            )
            raise
        try:
            response = self.ec2_client.describe_route_tables(
                Filters=[{"Name": "vpc-id", "Values": [self.vpc_id]}]
            )
            route_table_id = response["RouteTables"][0]["RouteTableId"]
            service_name = f"com.amazonaws.{self.ec2_client.meta.region_name}.s3express"

            response = self.ec2_client.create_vpc_endpoint(VpcId=self.vpc_id, RouteTableIds=[route_table_id], ServiceName=service_name)
            self.vcp_endpoint_id = response["VpcEndpoint"]["VpcEndpointId"]
            print(f"Created vpc endpoint {self.vcp_endpoint_id}")

        except ClientError as client_error:
            logging.error(
                "Couldn't create the vpc endpoint. Here's why: %s",
                client_error.response["Error"]["Message"],
            )
            raise


    def tear_done_vpc(self):
        if self.vcp_endpoint_id is not None:
            try:
                self.ec2_client.delete_vpc_endpoints(VpcEndpointIds=[self.vcp_endpoint_id])
                print(f"Deleted vpc endpoint {self.vcp_endpoint_id}.")
                self.vcp_endpoint_id = None
            except ClientError as client_error:
                logging.error(
                    "Couldn't delete the vpc endpoint %s. Here's why: %s",
                    self.vcp_endpoint_id,
                    client_error.response["Error"]["Message"],
                )
        if self.vpc_id is not None:
            try:
                self.ec2_client.delete_vpc(VpcId=self.vpc_id)
                print(f"Deleted vpc {self.vpc_id}")
                self.vpc_id = None
            except ClientError as client_error:
                logging.error(
                    "Couldn't delete the vpc %s. Here's why: %s",
                    self.vpc_id,
                    client_error.response["Error"]["Message"],
                )

if __name__ == "__main__":
    s3_express_scenario = None
    try:
        a_cloud_formation_resource = resource("cloudformation")
        an_ec2_client = client("ec2")
        an_iam_client = client("iam")
        s3_express_scenario = S3ExpressScenario(a_cloud_formation_resource, an_ec2_client,
                                                an_iam_client)
        s3_express_scenario.s3_express_scenario()
    except ClientError as error:
        logging.exception("Something went wrong with the demo!")
        if s3_express_scenario is not None:
            s3_express_scenario.cleanup()
    except ParamValidationError as err:
        logging.exception("Parameter validation error in demo!")
        if s3_express_scenario is not None:
            s3_express_scenario.cleanup()

# snippet-end:[python.example_code.s3.s3_express_basics]

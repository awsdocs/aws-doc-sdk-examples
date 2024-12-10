# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
import sys
import os
import uuid
import time
import argparse
from boto3.resources.base import ServiceResource
from boto3 import resource
from boto3 import client
from botocore.exceptions import ClientError, ParamValidationError

import boto3

script_dir = os.path.dirname(os.path.abspath(__file__))

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../.."))
import demo_tools.question as q

logger = logging.getLogger(__name__)


def print_dashes():
    """
    Print a line of dashes to separate sections of the output.
    """
    if not no_art:
        print("-" * 80)


use_press_enter_to_continue = True


def press_enter_to_continue():
    if use_press_enter_to_continue:
        q.ask("Press Enter to continue...")


# snippet-start:[python.example_code.s3.s3_express_basics]
class S3ExpressScenario:
    """Runs an interactive scenario that shows how to get started with S3 Express."""

    def __init__(
        self,
        cloud_formation_resource: ServiceResource,
        ec2_client: client,
        iam_client: client,
    ):
        self.cloud_formation_resource = cloud_formation_resource
        self.ec2_client = ec2_client
        self.iam_client = iam_client
        self.region = ec2_client.meta.region_name
        self.stack = None
        self.vpc_id = None
        self.vpc_endpoint_id = None
        self.regular_bucket_name = None
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
        press_enter_to_continue()

        # Create an optional VPC and create 2 IAM users.
        express_user_name, regular_user_name = self.create_vpc_and_users()

        # Set up two S3 clients, one regular and one express, and two buckets, one regular and one express.
        self.setup_clients_and_buckets(express_user_name, regular_user_name)

        # Create an S3 session for the express S3 client and add objects to the buckets.
        bucket_object = self.create_session_and_add_objects()

        # Demonstrate performance differences between regular and express buckets.
        self.demonstrate_performance(bucket_object)

        # Populate the buckets to show the lexicographical difference between regular and express buckets.
        self.show_lexicographical_differences(bucket_object)

        print("")
        print("That's it for our tour of the basic operations for S3 Express One Zone.")

        if q.ask(
            "Would you like to delete all the resources created during this demo (y/n)? ",
            q.is_yesno,
        ):
            self.cleanup()

    def create_vpc_and_users(self) -> None:
        """
        Optionally create a VPC.
        Create two IAM users, one with S3 Express One Zone permissions and one without.
        """
        # Configure a gateway VPC endpoint. This is the recommended method to allow S3 Express One Zone traffic without
        # the need to pass through an internet gateway or NAT device.
        print("")
        print(
            "1. First, we'll set up a new VPC and VPC Endpoint if this program is running in an EC2 instance in the same AZ as your Directory buckets will be."
        )
        print(
            "Are you running this in an EC2 instance located in the same AZ as your intended Directory buckets?"
        )
        if q.ask("Do you want to setup a VPC Endpoint? (y/n) ", q.is_yesno):
            print(
                "Great! Let's set up a VPC, retrieve the Route Table from it, and create a VPC Endpoint to connect the S3 Client to."
            )
            self.setup_vpc()
            press_enter_to_continue()
        else:
            print("Skipping the VPC setup. Don't forget to use this in production!")
        print("")
        print("2. Policies, users, and roles with CDK.")
        print(
            "Now, we'll set up some policies, roles, and a user. This user will only have permissions to do S3 Express One Zone actions."
        )
        press_enter_to_continue()
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
        return express_user_name, regular_user_name

    def setup_clients_and_buckets(
        self, express_user_name: str, regular_user_name: str
    ) -> None:
        """
        Set up two S3 clients, one regular and one express, and two buckets, one regular and one express.
        :param express_user_name: The name of the user with S3 Express permissions.
        :param regular_user_name: The name of the user with regular S3 permissions.
        """
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
        press_enter_to_continue()
        self.s3_regular_client = self.create_s3__client_with_access_key_credentials(
            regular_credentials
        )
        self.s3_express_client = self.create_s3__client_with_access_key_credentials(
            express_credentials
        )
        print(
            "All the roles and policies were created an attached to the user. Then, a new S3 Client and Service were created using that user's credentials."
        )
        print(
            "We can now use this client to make calls to S3 Express operations. Keeping permissions in mind (and adhering to least-privilege) is crucial to S3 Express."
        )
        press_enter_to_continue()
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
        bucket_prefix = q.ask(
            "Enter a bucket name prefix that will be used for both buckets: ",
            q.non_empty,
        )
        availability_zone = self.select_availability_zone_id(self.region)
        # Construct the parts of a directory bucket name that is made unique with a UUID string.
        directory_bucket_suffix = f"--{availability_zone['ZoneId']}--x-s3"
        max_uuid_length = 63 - len(bucket_prefix) - len(directory_bucket_suffix) - 1
        bucket_uuid = str(uuid.uuid4()).replace("-", "")[:max_uuid_length]
        directory_bucket_name = (
            f"{bucket_prefix}-{bucket_uuid}{directory_bucket_suffix}"
        )
        regular_bucket_name = f"{bucket_prefix}-regular-{bucket_uuid}"
        configuration = {
            "Bucket": {"Type": "Directory", "DataRedundancy": "SingleAvailabilityZone"},
            "Location": {
                "Name": availability_zone["ZoneId"],
                "Type": "AvailabilityZone",
            },
        }
        press_enter_to_continue()
        print(
            "Now, let's create the actual Directory bucket, as well as a regular bucket."
        )
        press_enter_to_continue()
        self.create_bucket(self.s3_express_client, directory_bucket_name, configuration)
        print(f"Created directory bucket, '{directory_bucket_name}'")
        self.directory_bucket_name = directory_bucket_name
        self.create_bucket(self.s3_regular_client, regular_bucket_name)
        print(f"Created regular bucket, '{regular_bucket_name}'")
        self.regular_bucket_name = regular_bucket_name
        print("Great! Both buckets were created.")
        press_enter_to_continue()

    def create_session_and_add_objects(self) -> None:
        """
        Create a session for the express S3 client and add objects to the buckets.
        """
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
        press_enter_to_continue()
        bucket_object = "basic-text-object"
        S3ExpressScenario.put_object(
            self.s3_regular_client,
            self.regular_bucket_name,
            bucket_object,
            "Look Ma, I'm a bucket!",
        )
        self.create_express_session()
        self.copy_object(
            self.s3_express_client,
            self.regular_bucket_name,
            bucket_object,
            self.directory_bucket_name,
            bucket_object,
        )
        print(
            "It worked! It's important to remember the user permissions when interacting with Directory buckets."
        )
        print(
            "Instead of validating permissions on every call as normal buckets do, Directory buckets utilize the user credentials and session token to validate."
        )
        print(
            "This allows for much faster connection speeds on every call. For single calls, this is low, but for many concurrent calls, this adds up to a lot of time saved."
        )
        press_enter_to_continue()
        return bucket_object

    def demonstrate_performance(self, bucket_object: str) -> None:
        """
        Demonstrate performance differences between regular and Directory buckets.
        :param bucket_object: The name of the object to download from each bucket.
        """
        print("")
        print("6. Demonstrate performance difference.")
        print(
            "Now, let's do a performance test. We'll download the same object from each bucket $downloads times and compare the total time needed. Note: the performance difference will be much more pronounced if this example is run in an EC2 instance in the same AZ as the bucket."
        )
        downloads = 1000
        print(
            f"The number of downloads of the same object for this example is set at {downloads}."
        )
        if q.ask("Would you like to download a different number? (y/n) ", q.is_yesno):
            max_downloads = 1000000
            downloads = q.ask(
                f"Enter a number between 1 and {max_downloads} for the number of downloads: ",
                q.is_int,
                q.in_range(1, max_downloads),
            )
        # Download the object $downloads times from each bucket and time it to demonstrate the speed difference.
        print("Downloading from the Directory bucket.")
        directory_time_start = time.time_ns()
        for index in range(downloads):
            if index % 10 == 0:
                print(f"Download {index} of {downloads}")
            S3ExpressScenario.get_object(
                self.s3_express_client, self.directory_bucket_name, bucket_object
            )
        directory_time_difference = time.time_ns() - directory_time_start
        print("Downloading from the normal bucket.")
        normal_time_start = time.time_ns()
        for index in range(downloads):
            if index % 10 == 0:
                print(f"Download {index} of {downloads}")
            S3ExpressScenario.get_object(
                self.s3_regular_client, self.regular_bucket_name, bucket_object
            )
        normal_time_difference = time.time_ns() - normal_time_start
        print(
            f"The directory bucket took {directory_time_difference} nanoseconds, while the normal bucket took {normal_time_difference}."
        )
        difference = normal_time_difference - directory_time_difference
        print(f"That's a difference of {difference} nanoseconds, or")
        print(f"{(difference) / 1000000000} seconds.")
        if difference < 0:
            print(
                "The directory buckets were slower. This can happen if you are not running on the cloud within a vpc."
            )
        press_enter_to_continue()

    def show_lexicographical_differences(self, bucket_object: str) -> None:
        """
        Show the lexicographical difference between Directory buckets and regular buckets.
        This is done by creating a few objects in each bucket and listing them to show the difference.
        :param bucket_object: The object to use for the listing operations.
        """
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
        press_enter_to_continue()
        # Populate a few more files in each bucket so that we can use ListObjects and show the difference.
        other_object = f"other/{bucket_object}"
        alt_object = f"alt/{bucket_object}"
        other_alt_object = f"other/alt/{bucket_object}"
        S3ExpressScenario.put_object(
            self.s3_regular_client, self.regular_bucket_name, other_object, ""
        )
        S3ExpressScenario.put_object(
            self.s3_express_client, self.directory_bucket_name, other_object, ""
        )
        S3ExpressScenario.put_object(
            self.s3_regular_client, self.regular_bucket_name, alt_object, ""
        )
        S3ExpressScenario.put_object(
            self.s3_express_client, self.directory_bucket_name, alt_object, ""
        )
        S3ExpressScenario.put_object(
            self.s3_regular_client, self.regular_bucket_name, other_alt_object, ""
        )
        S3ExpressScenario.put_object(
            self.s3_express_client, self.directory_bucket_name, other_alt_object, ""
        )
        directory_bucket_objects = S3ExpressScenario.list_objects(
            self.s3_express_client, self.directory_bucket_name
        )
        regular_bucket_objects = S3ExpressScenario.list_objects(
            self.s3_regular_client, self.regular_bucket_name
        )
        print("Directory bucket content")
        for bucket_object in directory_bucket_objects:
            print(f"   {bucket_object['Key']}")
        print("Normal bucket content")
        for bucket_object in regular_bucket_objects:
            print(f"   {bucket_object['Key']}")
        print(
            "Notice how the normal bucket lists objects in lexicographical order, while the directory bucket does not."
        )
        print(
            'This is because the normal bucket considers the whole "key" to be the object identifies, while the'
        )
        print(
            'directory bucket actually creates directories and uses the object "key" as a path to the object.'
        )
        press_enter_to_continue()

    def cleanup(self) -> None:
        """
        Delete resources created by this scenario.
        """
        if self.directory_bucket_name is not None:
            self.delete_bucket_and_objects(
                self.s3_express_client, self.directory_bucket_name
            )
            print(f"Deleted directory bucket, '{self.directory_bucket_name}'")
            self.directory_bucket_name = None

        if self.regular_bucket_name is not None:
            self.delete_bucket_and_objects(
                self.s3_regular_client, self.regular_bucket_name
            )
            print(f"Deleted regular bucket, '{self.regular_bucket_name}'")
            self.regular_bucket_name = None

        if self.stack is not None:
            self.destroy_cloudformation_stack(self.stack)
            self.stack = None

        self.tear_done_vpc()

    def create_access_key(self, user_name: str) -> dict[str, any]:
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

    def create_s3__client_with_access_key_credentials(
        self, access_key: dict[str, any]
    ) -> client:
        """
        Creates an S3 client with access key credentials.
        :param access_key: The access key for the user.
        :return: The S3 Express One Zone client.
        """
        try:
            s3_express_client = boto3.client(
                "s3",
                aws_access_key_id=access_key["AccessKeyId"],
                aws_secret_access_key=access_key["SecretAccessKey"],
                region_name=self.region,
            )
            return s3_express_client
        except ClientError as client_error:
            logging.error(
                "Couldn't create the S3 Express One Zone client. Here's why: %s",
                client_error.response["Error"]["Message"],
            )
            raise

    @staticmethod
    def create_bucket(
        s3_client: client, bucket_name: str, bucket_configuration: dict[str, any] = None
    ) -> None:
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
                client_error.response["Error"]["Message"],
            )
            raise

    @staticmethod
    def delete_bucket_and_objects(s3_client: client, bucket_name: str) -> None:
        """
        Deletes a bucket and its objects.
        :param s3_client: The S3 client to use.
        :param bucket_name: The name of the bucket.
        """
        try:
            # Delete the objects in the bucket first. This is required for a bucket to be deleted.
            paginator = s3_client.get_paginator("list_objects_v2")
            page_iterator = paginator.paginate(Bucket=bucket_name)
            for page in page_iterator:
                if "Contents" in page:
                    delete_keys = {
                        "Objects": [{"Key": obj["Key"]} for obj in page["Contents"]]
                    }
                    response = s3_client.delete_objects(
                        Bucket=bucket_name, Delete=delete_keys
                    )
                    if "Errors" in response:
                        for error in response["Errors"]:
                            logging.error(
                                "Couldn't delete object %s. Here's why: %s",
                                error["Key"],
                                error["Message"],
                            )

            s3_client.delete_bucket(Bucket=bucket_name)
        except ClientError as client_error:
            logging.error(
                "Couldn't delete the bucket %s. Here's why: %s",
                bucket_name,
                client_error.response["Error"]["Message"],
            )

    @staticmethod
    def put_object(
        s3_client: client, bucket_name: str, object_key: str, content: str
    ) -> None:
        """
        Puts an object into a bucket.
        :param s3_client: The S3 client to use.
        :param bucket_name: The name of the bucket.
        :param object_key: The key of the object.
        :param content: The content of the object.
        """
        try:
            s3_client.put_object(Body=content, Bucket=bucket_name, Key=object_key)
        except ClientError as client_error:
            logging.error(
                "Couldn't put the object %s into bucket %s. Here's why: %s",
                object_key,
                bucket_name,
                client_error.response["Error"]["Message"],
            )
            raise

    @staticmethod
    def get_object(s3_client: client, bucket_name: str, object_key: str) -> None:
        """
        Gets an object from a bucket.
        :param s3_client: The S3 client to use.
        :param bucket_name: The name of the bucket.
        :param object_key: The key of the object.
        """
        try:
            s3_client.get_object(Bucket=bucket_name, Key=object_key)
        except ClientError as client_error:
            logging.error(
                "Couldn't get the object %s from bucket %s. Here's why: %s",
                object_key,
                bucket_name,
                client_error.response["Error"]["Message"],
            )
            raise

    @staticmethod
    def list_objects(s3_client: client, bucket: str) -> list[str]:
        """
        Lists objects in a bucket.
        :param s3_client: The S3 client to use.
        :param bucket: The name of the bucket.
        :return: The list of objects in the bucket.
        """
        try:
            response = s3_client.list_objects_v2(Bucket=bucket)
            return response.get("Contents", [])
        except ClientError as client_error:
            logging.error(
                "Couldn't list objects in bucket %s. Here's why: %s",
                bucket,
                client_error.response["Error"]["Message"],
            )
            raise

    def create_express_session(self) -> None:
        """
        Creates an express session.
        """
        try:
            response = self.s3_express_client.create_session(
                Bucket=self.directory_bucket_name
            )
        except ClientError as client_error:
            logging.error(
                "Couldn't create the express session for bucket %s. Here's why: %s",
                self.directory_bucket_name,
                client_error.response["Error"]["Message"],
            )
            raise

    def copy_object(
        self,
        s3_client: client,
        source_bucket: str,
        source_key: str,
        destination_bucket: str,
        destination_key: str,
    ) -> None:
        """
        Copies an object from one bucket to another.
        :param s3_client: The S3 client to use.
        :param source_bucket: The source bucket.
        :param source_key: The source key.
        :param destination_bucket: The destination bucket.
        :param destination_key: The destination key.
        :return: None
        """
        try:
            s3_client.copy_object(
                CopySource={"Bucket": source_bucket, "Key": source_key},
                Bucket=destination_bucket,
                Key=destination_key,
            )
        except ClientError as client_error:
            logging.error(
                "Couldn't copy object %s from bucket %s to bucket %s. Here's why: %s",
                source_key,
                source_bucket,
                destination_bucket,
                client_error.response["Error"]["Message"],
            )
            raise

    def select_availability_zone_id(self, region: str) -> dict[str, any]:
        """
        Selects an availability zone.
        :param region: The region to select the availability zone from.
        :return: The availability zone dictionary.
        """
        try:
            response = self.ec2_client.describe_availability_zones(
                Filters=[{"Name": "region-name", "Values": [region]}]
            )
            availability_zones = response["AvailabilityZones"]
            zone_names = [zone["ZoneName"] for zone in availability_zones]
            index = q.choose("Select an availability zone: ", zone_names)
            return availability_zones[index]
        except ClientError as client_error:
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
        try:
            print(
                f"CloudFormation stack '{stack.name}' is being deleted. This may take a few minutes."
            )
            stack.delete()
            waiter = self.cloud_formation_resource.meta.client.get_waiter(
                "stack_delete_complete"
            )
            waiter.wait(StackName=stack.name)
            print(f"CloudFormation stack '{stack.name}' has been deleted.")
        except ClientError as client_error:
            logging.error(
                "Couldn't delete the CloudFormation stack. Here's why: %s",
                client_error.response["Error"]["Message"],
            )

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

            response = self.ec2_client.create_vpc_endpoint(
                VpcId=self.vpc_id,
                RouteTableIds=[route_table_id],
                ServiceName=service_name,
            )
            self.vpc_endpoint_id = response["VpcEndpoint"]["VpcEndpointId"]
            print(f"Created vpc endpoint {self.vpc_endpoint_id}")

        except ClientError as client_error:
            logging.error(
                "Couldn't create the vpc endpoint. Here's why: %s",
                client_error.response["Error"]["Message"],
            )
            raise

    def tear_done_vpc(self) -> None:
        if self.vpc_endpoint_id is not None:
            try:
                self.ec2_client.delete_vpc_endpoints(
                    VpcEndpointIds=[self.vpc_endpoint_id]
                )
                print(f"Deleted vpc endpoint {self.vpc_endpoint_id}.")
                self.vpc_endpoint_id = None
            except ClientError as client_error:
                logging.error(
                    "Couldn't delete the vpc endpoint %s. Here's why: %s",
                    self.vpc_endpoint_id,
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
# snippet-end:[python.example_code.s3.s3_express_basics]

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Run S3 Express getting started scenario."
    )
    parser.add_argument(
        "--no-art",
        action="store_true",
        help="accessibility setting that suppresses art in the console output.",
    )
    args = parser.parse_args()
    no_art = args.no_art

    s3_express_scenario = None

    try:
        a_cloud_formation_resource = resource("cloudformation")
        an_ec2_client = client("ec2")
        an_iam_client = client("iam")
        s3_express_scenario = S3ExpressScenario(
            a_cloud_formation_resource, an_ec2_client, an_iam_client
        )
        s3_express_scenario.s3_express_scenario()
    except ClientError as error:
        logging.exception("Something went wrong with the demo!")
        if s3_express_scenario is not None:
            s3_express_scenario.cleanup()
    except ParamValidationError as err:
        logging.exception("Parameter validation error in demo!")
        if s3_express_scenario is not None:
            s3_express_scenario.cleanup()
    except TypeError as err:
        logging.exception("Type error in demo!")
        if s3_express_scenario is not None:
            s3_express_scenario.cleanup()



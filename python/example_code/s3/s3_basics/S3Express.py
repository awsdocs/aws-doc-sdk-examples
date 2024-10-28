# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
import sys
import os
import uuid
from boto3.resources.base import ServiceResource
from boto3 import resource
from boto3 import client
from botocore.exceptions import ClientError

script_dir = os.path.dirname(os.path.abspath(__file__))

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../../.."))
import demo_tools.question as q

no_art = False  # 'no_art' suppresses 'art' to improve accessibility.


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
    ):
        self.cloud_formation_resource = cloud_formation_resource
        self.ec2_client = ec2_client
        self.iam_client = iam_client
        self.stack: ServiceResource = None
        self.vpc_id: str = None
        self.vcp_endpoint_id :str = None
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
bucket.\n
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
            "Now, we'll set up some policies, roles, and a user. This user will only have permissions to do S3 Express One Zone actions.\n"
        )
        q.ask("Press Enter to continue...")
        stackName = f"cfn-stack-s3-express-basics--{uuid.uuid4()}"
        template_as_string = S3ExpressScenario.get_template_as_string()
        self.stack = self.deploy_cloudformation_stack(stackName, template_as_string)

        print("\n")
        print(
            "3. Create an additional client using the credentials with S3 Express permissions.\n"
        )
        print(
            "This client is created with the credentials associated with the user account with the S3 Express policy attached, so it can perform S3 Express operations.\n"
        )
        q.ask("Press Enter to continue...")
        print(
            "All the roles and policies were created an attached to the user. Then, a new S3 Client and Service were created using that user's credentials.\n"
        )
        print(
            "We can now use this client to make calls to S3 Express operations. Keeping permissions in mind (and adhering to least-privilege) is crucial to S3 Express.\n"
        )
        q.ask("Press Enter to continue...")
        print("\n")
        print("3. Create two buckets.\n")
        print(
            "Now we will create a Directory bucket, which is the linchpin of the S3 Express One Zone service.\n"
        )
        print(
            "Directory buckets behave in different ways from regular S3 buckets, which we will explore here.\n"
        )
        print(
            "We'll also create a normal bucket, put an object into the normal bucket, and copy it over to the Directory bucket.\n"
        )
        q.ask("Press Enter to continue...")
        print(
            "Now, let's create the actual Directory bucket, as well as a regular bucket.\n"
        )
        q.ask("Press Enter to continue...")
        print("Great! Both buckets were created.\n")
        q.ask("Press Enter to continue...")
        print("\n")
        print("5. Create an object and copy it over.\n")
        print(
            "We'll create a basic object consisting of some text and upload it to the normal bucket.\n"
        )
        print(
            "Next, we'll copy the object into the Directory bucket using the regular client.\n"
        )
        print(
            "This works fine, because Copy operations are not restricted for Directory buckets.\n"
        )
        q.ask("Press Enter to continue...")
        print(
            "It worked! It's important to remember the user permissions when interacting with Directory buckets.\n"
        )
        print(
            "Instead of validating permissions on every call as normal buckets do, Directory buckets utilize the user credentials and session token to validate.\n"
        )
        print(
            "This allows for much faster connection speeds on every call. For single calls, this is low, but for many concurrent calls, this adds up to a lot of time saved.\n"
        )
        q.ask("Press Enter to continue...")
        print("\n")
        print("6. Demonstrate performance difference.\n")
        print(
            "Now, let's do a performance test. We'll download the same object from each bucket $downloads times and compare the total time needed. Note: the performance difference will be much more pronounced if this example is run in an EC2 instance in the same AZ as the bucket.\n"
        )

        # $downloadChoice = testable_readline(
        #     "If you would like to download each object $downloads times, press enter. Otherwise, enter a custom amount and press enter.");

        print(
            "The directory bucket took $directoryTimeDiff nanoseconds, while the normal bucket took $normalTimeDiff.\n"
        )
        # print("That's a difference of ".($normalTimeDiff - $directoryTimeDiff)." nanoseconds, or ".(
        #     ($normalTimeDiff - $directoryTimeDiff) / 1000000000).
        # " seconds.\n")
        q.ask("Press Enter to continue...")
        print("\n")
        print("7. Populate the buckets to show the lexicographical difference.\n")
        print(
            "Now let's explore how Directory buckets store objects in a different manner to regular buckets.\n"
        )
        print('The key is in the name "Directory!"\n')
        print(
            "Where regular buckets store their key/value pairs in a flat manner, Directory buckets use actual directories/folders.\n"
        )
        print(
            "This allows for more rapid indexing, traversing, and therefore retrieval times!\n"
        )
        print(
            "The more segmented your bucket is, with lots of directories, sub-directories, and objects, the more efficient it becomes.\n"
        )
        print(
            "This structural difference also causes ListObjects to behave differently, which can cause unexpected results.\n"
        )
        print(
            "Let's add a few more objects with layered directories as see how the output of ListObjects changes.\n"
        )
        q.ask("Press Enter to continue...")
        print("Directory bucket content\n")
        # print($result['Key'].
        # "\n")
        print("\nNormal bucket content\n")
        # print($result['Key'].
        # "\n")
        print(
            'Notice how the normal bucket lists objects in lexicographical order, while the directory bucket does not. This is because the normal bucket considers the whole "key" to be the object identifies, while the directory bucket actually creates directories and uses the object "key" as a path to the object.\n'
        )
        q.ask("Press Enter to continue...")
        print("\n")
        print(
            "That's it for our tour of the basic operations for S3 Express One Zone.\n"
        )

        # $cleanUp = testable_readline(
        #     "Would you like to delete all the resources created during this demo? Enter Y/y to delete all the resources.");
        self.cleanup()

    def cleanup(self):
        """
        Delete resources created by this scenario.
        """

        if self.stack is not None:
            self.destroy_cloudformation_stack(self.stack)

        self.tear_done_vpc()

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

        except ClientError as err:
            logging.error(
                "Couldn't create the vpc. Here's why: %s",
                err.response["Error"]["Message"],
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

        except ClientError as err:
            logging.error(
                "Couldn't create the vpc endpoint. Here's why: %s",
                err.response["Error"]["Message"],
            )
            raise


    def tear_done_vpc(self):
        if self.vcp_endpoint_id is not None:
            try:
                self.ec2_client.delete_vpc_endpoints(VpcEndpointIds=[self.vcp_endpoint_id])
                print(f"Deleted vpc endpoint {self.vcp_endpoint_id}.")
                self.vcp_endpoint_id = None
            except ClientError as err:
                logging.error(
                    "Couldn't delete the vpc endpoint %s. Here's why: %s",
                    self.vcp_endpoint_id,
                    err.response["Error"]["Message"],
                )
        if self.vpc_id is not None:
            try:
                self.ec2_client.delete_vpc(VpcId=self.vpc_id)
                print(f"Deleted vpc {self.vpc_id}")
                self.vpc_id = None
            except ClientError as err:
                logging.error(
                    "Couldn't delete the vpc %s. Here's why: %s",
                    self.vpc_id,
                    err.response["Error"]["Message"],
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
    except Exception:
        logging.exception("Something went wrong with the demo!")
        if s3_express_scenario is not None:
            s3_express_scenario.cleanup()

# snippet-end:[python.example_code.s3.s3_express_basics]

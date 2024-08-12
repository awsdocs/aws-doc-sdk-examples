import logging
import sys
import urllib.request

import boto3
from botocore.exceptions import ClientError
from elastic_ip import ElasticIpWrapper
from instance import EC2InstanceWrapper
from key_pair import KeyPairWrapper
from security_group import SecurityGroupWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append("../..")

logger = logging.getLogger(__name__)


class Ec2InstanceScenario:
    """Runs an automated scenario that shows how to get started using EC2 instances."""

    def __init__(self, inst_wrapper, key_wrapper, sg_wrapper, eip_wrapper, ssm_client):
        self.inst_wrapper = inst_wrapper
        self.key_wrapper = key_wrapper
        self.sg_wrapper = sg_wrapper
        self.eip_wrapper = eip_wrapper
        self.ssm_client = ssm_client

    def create_and_list_key_pairs(self):
        print(
            "Creating an RSA key pair named 'default-key' to securely connect to your EC2 instance."
        )
        key_name = "default-key"
        self.key_wrapper.create(key_name)
        print(
            f"Created a key pair {self.key_wrapper.key_pair.key_name} and saved the "
            f"private key to {self.key_wrapper.key_file_path}."
        )
        print("Listing the first five key pairs for the current account:")
        self.key_wrapper.list(5)

    def create_security_group(self):
        print(
            "Creating a security group named 'default-sg' to manage access to your instance."
        )
        sg_name = "default-sg"
        security_group = self.sg_wrapper.create(
            sg_name, "Security group for example: get started with instances."
        )
        print(
            f"Created security group {security_group.group_name} in your default "
            f"VPC {security_group.vpc_id}."
        )

        ip_response = urllib.request.urlopen("http://checkip.amazonaws.com")
        current_ip_address = ip_response.read().decode("utf-8").strip()
        print(
            f"Adding a rule to allow SSH only from your current IP address: {current_ip_address}."
        )
        response = self.sg_wrapper.authorize_ingress(current_ip_address)
        if response["Return"]:
            print("Security group rules updated.")
        else:
            print("Couldn't update security group rules.")
        self.sg_wrapper.describe()

    def create_instance(self):
        ami_paginator = self.ssm_client.get_paginator("get_parameters_by_path")
        ami_options = []
        for page in ami_paginator.paginate(Path="/aws/service/ami-amazon-linux-latest"):
            ami_options += page["Parameters"]
        amzn2_images = self.inst_wrapper.get_images(
            [opt["Value"] for opt in ami_options if "amzn2" in opt["Name"]]
        )
        print("Using the first available Amazon Linux 2 AMI for creating the instance.")
        image_choice = 0  # Select the first AMI in the list

        print(
            f"Selected instance type 't2.micro' that supports the "
            f"{amzn2_images[image_choice].architecture} architecture of the image."
        )
        inst_type_choice = "t2.micro"  # Default instance type

        print("Creating your instance and waiting for it to start...")
        self.inst_wrapper.create(
            amzn2_images[image_choice],
            inst_type_choice,
            self.key_wrapper.key_pair,
            [self.sg_wrapper.security_group],
        )
        print(f"Your instance is ready:\n")
        self.inst_wrapper.display()

        print("You can use SSH to connect to your instance.")
        print(
            "If the connection attempt times out, you might have to manually update "
            "the SSH ingress rule for your IP address in the AWS Management Console."
        )
        self._display_ssh_info()

    def _display_ssh_info(self):
        print("To connect, open another command prompt and run the following command:")
        if self.eip_wrapper.elastic_ip is None:
            print(
                f"\tssh -i {self.key_wrapper.key_file_path} "
                f"ec2-user@{self.inst_wrapper.instance.public_ip_address}"
            )
        else:
            print(
                f"\tssh -i {self.key_wrapper.key_file_path} "
                f"ec2-user@{self.eip_wrapper.elastic_ip.public_ip}"
            )

    def associate_elastic_ip(self):
        print(
            "Allocating an Elastic IP address and associating it with your instance "
            "to keep a consistent IP address even when your instance restarts."
        )
        elastic_ip = self.eip_wrapper.allocate()
        print(f"Allocated static Elastic IP address: {elastic_ip.public_ip}.")
        self.eip_wrapper.associate(self.inst_wrapper.instance)
        print(f"Associated your Elastic IP with your instance.")
        print(
            "You can now use SSH to connect to your instance by using the Elastic IP."
        )
        self._display_ssh_info()

    def stop_and_start_instance(self):
        print("Stopping your instance and waiting until it's stopped...")
        self.inst_wrapper.stop()
        print("Your instance is stopped. Restarting...")
        self.inst_wrapper.start()
        print("Your instance is running.")
        self.inst_wrapper.display()
        if self.eip_wrapper.elastic_ip is None:
            print(
                "Every time your instance is restarted, its public IP address changes."
            )
        else:
            print(
                "Because you have associated an Elastic IP with your instance, you can \n"
                "connect by using a consistent IP address after the instance restarts."
            )
        self._display_ssh_info()

    def cleanup(self):
        print("Cleaning up resources created by the example:")
        print(f"\tElastic IP: {self.eip_wrapper.elastic_ip.allocation_id}")
        print(f"\tInstance: {self.inst_wrapper.instance.id}")
        print(f"\tSecurity group: {self.sg_wrapper.security_group.id}")
        print(f"\tKey pair: {self.key_wrapper.key_pair.name}")
        print("Deleting these resources...")
        self.eip_wrapper.disassociate()
        print("Disassociated the Elastic IP from the instance.")
        self.eip_wrapper.release()
        print("Released the Elastic IP.")
        print("Terminating the instance and waiting for it to terminate...")
        self.inst_wrapper.terminate()
        print("Instance terminated.")
        self.sg_wrapper.delete()
        print("Deleted security group.")
        self.key_wrapper.delete()
        print("Deleted key pair.")

    def run_scenario(self):
        logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

        print("-" * 88)
        print(
            "Welcome to the Amazon Elastic Compute Cloud (Amazon EC2) get started with instances demo."
        )
        print("-" * 88)

        self.create_and_list_key_pairs()
        self.create_security_group()
        self.create_instance()
        self.stop_and_start_instance()
        self.associate_elastic_ip()
        self.stop_and_start_instance()
        self.cleanup()

        print("\nThanks for watching!")
        print("-" * 88)


if __name__ == "__main__":
    try:
        scenario = Ec2InstanceScenario(
            EC2InstanceWrapper.from_client(),
            KeyPairWrapper.from_client(),
            SecurityGroupWrapper.from_client(),
            ElasticIpWrapper.from_client(),
            boto3.client("ssm"),
        )
        scenario.run_scenario()
    except ClientError as err:
        logger.error(
            "Couldn't complete the scenario. Here's why: %s: %s",
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
    except Exception:
        logging.exception("Something went wrong with the demo.")

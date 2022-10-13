# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Elastic Compute Cloud
(Amazon EC2) to do the following:

* Create a key pair that is used to secure SSH communication between your computer and
  an EC2 instance.
* Create a security group that acts as a virtual firewall for your EC2 instances to
  control incoming and outgoing traffic.
* Find an Amazon Machine Image and a compatible instance type.
* Create an instance that is created from the instance type and AMI you select, and
  is configured to use the security group and key pair created in this example.
* Stop and restart the instance.
* Create an Elastic IP address and associate it as a consistent IP address for your instance.
* Connect to your instance with SSH, using both its public IP address and your Elastic IP
  address.
* Clean up all of the resources created by this example.
"""

import logging
import urllib.request
import sys

import boto3

from elastic_ip import ElasticIpWrapper
from instance import InstanceWrapper
from key_pair import KeyPairWrapper
from security_group import SecurityGroupWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append('../..')
from demo_tools import demo_func
import demo_tools.question as q

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ec2.Scenario_GetStartedInstances]
class Ec2InstanceScenario:
    """Runs an interactive scenario that shows how to get started using Amazon EC2 instances."""
    def __init__(self, inst_wrapper, key_wrapper, sg_wrapper, eip_wrapper, ssm_client):
        """
        :param inst_wrapper: An object that wraps instance actions.
        :param key_wrapper: An object that wraps key pair actions.
        :param sg_wrapper: An object that wraps security group actions.
        :param eip_wrapper: An object that wraps Elastic IP actions.
        :param ssm_client: A Boto3 AWS Systems Manager client.
        """
        self.inst_wrapper = inst_wrapper
        self.key_wrapper = key_wrapper
        self.sg_wrapper = sg_wrapper
        self.eip_wrapper = eip_wrapper
        self.ssm_client = ssm_client

    @demo_func
    def create_and_list_key_pairs(self):
        """
        1. Creates an RSA key pair and saves its private key data as a .pem file in secure
           temporary storage. The private key data is deleted after the example completes.
        2. Lists the first five key pairs for the current account.
        """
        print("Let's create an RSA key pair that you can be use to securely connect to "
              "your Amazon EC2 instance.")
        key_name = q.ask("Enter a unique name for your key: ", q.non_empty)
        self.key_wrapper.create(key_name)
        print(f"Created a key pair {self.key_wrapper.key_pair.key_name} and saved the "
              f"private key to {self.key_wrapper.key_file_path}.\n")
        if q.ask("Do you want to list some of your key pairs? (y/n) ", q.is_yesno):
            self.key_wrapper.list(5)

    @demo_func
    def create_security_group(self):
        """
        1. Creates a security group for the default VPC.
        2. Adds an inbound rule to allow SSH. The SSH rule allows only
           inbound traffic from the current computer’s public IPv4 address.
        3. Displays information about the security group.

        This function uses 'http://checkip.amazonaws.com' to get the current public IP
        address of the computer that is running the example. This method works in most
        cases. However, depending on how your computer connects to the internet, you
        may have to manually add your public IP address to the security group by using
        the AWS Management Console.
        """
        print("Let's create a security group to manage access to your instance.")
        sg_name = q.ask("Enter a unique name for your security group: ", q.non_empty)
        security_group = self.sg_wrapper.create(
            sg_name, "Security group for example: get started with instances.")
        print(f"Created security group {security_group.group_name} in your default "
              f"VPC {security_group.vpc_id}.\n")

        ip_response = urllib.request.urlopen('http://checkip.amazonaws.com')
        current_ip_address = ip_response.read().decode('utf-8').strip()
        print("Let's add a rule to allow SSH only from your current IP address.")
        print(f"Your public IP address is {current_ip_address}.")
        q.ask("Press Enter to add this rule to your security group.")
        response = self.sg_wrapper.authorize_ingress(current_ip_address)
        if response['Return']:
            print("Security group rules updated.")
        else:
            print("Couldn't update security group rules.")
        self.sg_wrapper.describe()

    @demo_func
    def create_instance(self):
        """
        1. Gets a list of Amazon Linux 2 AMIs from Systems Manager. Specifying the
           '/aws/service/ami-amazon-linux-latest' path ensures that only the latest AMIs
           are returned.
        2. Gets and displays information about the available AMIs and lets you select one.
        3. Gets a list of instance types that are compatible with the selected AMI and
           lets you select one.
        4. Creates an instance with the previously created key pair and security group,
           and the selected AMI and instance type.
        5. Waits for the instance to be running and then displays its information.
        """
        ami_paginator = self.ssm_client.get_paginator('get_parameters_by_path')
        ami_options = []
        for page in ami_paginator.paginate(Path='/aws/service/ami-amazon-linux-latest'):
            ami_options += page['Parameters']
        amzn2_images = self.inst_wrapper.get_images(
            [opt['Value'] for opt in ami_options if 'amzn2' in opt['Name']])
        print("Let's create an instance from an Amazon Linux 2 AMI. Here are some options:")
        image_choice = q.choose(
            "Which one do you want to use? ", [opt.description for opt in amzn2_images])
        print("Great choice!\n")

        print(f"Here are some instance types that support the "
              f"{amzn2_images[image_choice].architecture} architecture of the image:")
        inst_types = self.inst_wrapper.get_instance_types(amzn2_images[image_choice].architecture)
        inst_type_choice = q.choose(
            "Which one do you want to use? ", [it['InstanceType'] for it in inst_types])
        print("Another great choice.\n")

        print("Creating your instance and waiting for it to start...")
        self.inst_wrapper.create(
            amzn2_images[image_choice],
            inst_types[inst_type_choice]['InstanceType'],
            self.key_wrapper.key_pair,
            [self.sg_wrapper.security_group])
        print(f"Your instance is ready:\n")
        self.inst_wrapper.display()

        print("You can use SSH to connect to your instance.")
        print("If the connection attempt times out, you might have to manually update "
              "the SSH ingress rule for your IP address in the AWS Management Console.")
        self._display_ssh_info()

    def _display_ssh_info(self):
        """
        Displays an SSH connection string that can be used to connect to a running
        instance.
        """
        print("To connect, open another command prompt and run the following command:")
        if self.eip_wrapper.elastic_ip is None:
            print(f"\tssh -i {self.key_wrapper.key_file_path} "
                  f"ec2-user@{self.inst_wrapper.instance.public_ip_address}")
        else:
            print(f"\tssh -i {self.key_wrapper.key_file_path} "
                  f"ec2-user@{self.eip_wrapper.elastic_ip.public_ip}")
        q.ask("Press Enter when you're ready to continue the demo.")

    @demo_func
    def associate_elastic_ip(self):
        """
        1. Allocates an Elastic IP address and associates it with the instance.
        2. Displays an SSH connection string that uses the Elastic IP address.
        """
        print("You can allocate an Elastic IP address and associate it with your instance\n"
              "to keep a consistent IP address even when your instance restarts.")
        elastic_ip = self.eip_wrapper.allocate()
        print(f"Allocated static Elastic IP address: {elastic_ip.public_ip}.")
        self.eip_wrapper.associate(self.inst_wrapper.instance)
        print(f"Associated your Elastic IP with your instance.")
        print("You can now use SSH to connect to your instance by using the Elastic IP.")
        self._display_ssh_info()

    @demo_func
    def stop_and_start_instance(self):
        """
        1. Stops the instance and waits for it to stop.
        2. Starts the instance and waits for it to start.
        3. Displays information about the instance.
        4. Displays an SSH connection string. When an Elastic IP address is associated
           with the instance, the IP address stays consistent when the instance stops
           and starts.
        """
        print("Let's stop and start your instance to see what changes.")
        print("Stopping your instance and waiting until it's stopped...")
        self.inst_wrapper.stop()
        print("Your instance is stopped. Restarting...")
        self.inst_wrapper.start()
        print("Your instance is running.")
        self.inst_wrapper.display()
        if self.eip_wrapper.elastic_ip is None:
            print("Every time your instance is restarted, its public IP address changes.")
        else:
            print("Because you have associated an Elastic IP with your instance, you can \n"
                  "connect by using a consistent IP address after the instance restarts.")
        self._display_ssh_info()

    @demo_func
    def cleanup(self):
        """
        1. Disassociate and delete the previously created Elastic IP.
        2. Terminate the previously created instance.
        3. Delete the previously created security group.
        4. Delete the previously created key pair.
        """
        print("Let's clean everything up. This example created these resources:")
        print(f"\tElastic IP: {self.eip_wrapper.elastic_ip.allocation_id}")
        print(f"\tInstance: {self.inst_wrapper.instance.id}")
        print(f"\tSecurity group: {self.sg_wrapper.security_group.id}")
        print(f"\tKey pair: {self.key_wrapper.key_pair.name}")
        if q.ask("Ready to delete these resources? (y/n) ", q.is_yesno):
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
        logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

        print('-'*88)
        print("Welcome to the Amazon Elastic Compute Cloud (Amazon EC2) get started with instances demo.")
        print('-'*88)

        self.create_and_list_key_pairs()
        self.create_security_group()
        self.create_instance()
        self.stop_and_start_instance()
        self.associate_elastic_ip()
        self.stop_and_start_instance()
        self.cleanup()

        print("\nThanks for watching!")
        print('-'*88)


if __name__ == '__main__':
    try:
        scenario = Ec2InstanceScenario(
            InstanceWrapper.from_resource(), KeyPairWrapper.from_resource(),
            SecurityGroupWrapper.from_resource(), ElasticIpWrapper.from_resource(),
            boto3.client('ssm'))
        scenario.run_scenario()
    except Exception:
        logging.exception("Something went wrong with the demo.")
# snippet-end:[python.example_code.ec2.Scenario_GetStartedInstances]

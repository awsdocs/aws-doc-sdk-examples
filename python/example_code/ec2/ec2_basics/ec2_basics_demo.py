# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Elastic Compute Cloud
(Amazon EC2) API to create an instance, perform some management tasks on the instance,
and clean up everything created during the demo.
"""

import logging
import pprint
import time
import urllib.request
import boto3

import ec2_setup
import ec2_instance_management
import ec2_teardown

logger = logging.getLogger(__name__)


def make_unique_name(name):
    return f'demo-ec2-{name}-{time.time_ns()}'


def setup_demo(current_ip_address, ami_image_id, key_file_name):
    """
    Sets up prerequisites and creates instances used in the demo.
    When this function returns, the instances are running and ready to use.

    :param current_ip_address: The public IP address of the current computer.
    :param ami_image_id: The Amazon Machine Image (AMI) that is used to create the
                         instances for the demo.
    :param key_file_name: The name of a local file that contains the private key
                          that is used to connect to the instances using SSH.
    :return: The newly created instances, security groups, key pair, and
             Elastic IP object.
    """
    key_pair = ec2_setup.create_key_pair(make_unique_name('key'), key_file_name)
    print(f"Created a key pair {key_pair.key_name} and saved the private key to "
          f"{key_file_name}")

    ssh_sec_group = ec2_setup.setup_security_group(
        make_unique_name('ssh-group'),
        f'Demo group that allows SSH from {current_ip_address}.',
        current_ip_address)
    print(f"Created security group {ssh_sec_group.group_name} that allows SSH "
          f"access from {current_ip_address}.")

    no_ssh_sec_group = ec2_setup.setup_security_group(
        make_unique_name('no-ssh-group'), 'Demo group that does not allow SSH.')
    print(f"Created security group {no_ssh_sec_group.group_name} that does not allow "
          f"SSH access.")

    ssh_instance = ec2_setup.create_instance(
        ami_image_id, 't2.micro', key_pair.key_name, [ssh_sec_group.group_name])
    print(f"Created instance {ssh_instance.instance_id} that can be accessed "
          f"through SSH.")

    no_ssh_instance = ec2_setup.create_instance(
        ami_image_id, 't2.micro', key_pair.key_name, [no_ssh_sec_group.group_name])
    print(f"Created instance {no_ssh_instance.instance_id} that cannot be accessed "
          f"through SSH.")

    elastic_ip = ec2_instance_management.allocate_elastic_ip()
    print(f"Allocated static Elastic IP address: {elastic_ip.public_ip}.")

    print(f"Waiting for instances to start...")
    ssh_instance.wait_until_running()
    print(f"Instance {ssh_instance.instance_id} is running.")

    no_ssh_instance.wait_until_running()
    print(f"Instance {no_ssh_instance.instance_id} is running.")

    return (ssh_instance, no_ssh_instance), (no_ssh_sec_group, ssh_sec_group),\
        key_pair, elastic_ip


def management_demo(ssh_instance, no_ssh_instance, key_file_name, elastic_ip):
    """
    Shows how to perform management actions on an Amazon EC2 instance.

    * Associate an Elastic IP address with an instance.
    * Stop and start an instance.
    * Allow one instance to connect to another by setting an inbound rule
      in the target instance's security group that allows traffic from the
      source instance's security group.
    * Change an instance's security group to another security group.

    :param ssh_instance: An instance that is associated with a security group that
                         allows access from this computer using SSH.
    :param no_ssh_instance: An instance that is associated with a security group
                            that does not allow access using SSH.
    :param key_file_name: The name of a local file that contains the private key
                          for the demonstration instances.
    :param elastic_ip: The Elastic IP that is used in the demo.
    """
    ssh_instance.load()
    no_ssh_instance.load()

    print(f"At this point, you can SSH to instance {ssh_instance.instance_id} "
          f"at another command prompt by running")
    print(f"\tssh -i {key_file_name} ec2-user@{ssh_instance.public_ip_address}")
    print("If the connection attempt times out, you might have to manually update "
          "the SSH ingress rule for your IP address in the AWS Management Console.")
    input("Press Enter when you're ready to continue the demo.")

    ec2_instance_management.associate_elastic_ip(
        elastic_ip.allocation_id, ssh_instance.instance_id)
    print(f"Associated the Elastic IP with instance {ssh_instance.instance_id}.")
    print(f"You can now SSH to the instance at another command prompt by running")
    print(f"\t'ssh -i {key_file_name} ec2-user@{elastic_ip.public_ip}'")
    input("Press Enter when you're ready to continue the demo.")

    print("Just for fun, let's stop the instance.")
    ec2_instance_management.stop_instance(ssh_instance.instance_id)
    print("Waiting for stop...")
    ssh_instance.wait_until_stopped()
    ssh_instance.load()
    print(f"Instance {ssh_instance.instance_id} is {ssh_instance.state['Name']}.")

    print("Okay, now let's start it again.")
    ec2_instance_management.start_instance(ssh_instance.instance_id)
    print("Waiting for start...")
    ssh_instance.wait_until_running()
    ssh_instance.load()
    print(f"Instance {ssh_instance.instance_id} is {ssh_instance.state['Name']}.")

    ec2_instance_management.allow_security_group_ingress(
        no_ssh_instance.security_groups[0]['GroupId'],
        ssh_instance.security_groups[0]['GroupName'])
    print(f"Granted instances in {ssh_instance.security_groups[0]['GroupName']} "
          f"access to instances in {no_ssh_instance.security_groups[0]['GroupName']}.")
    print(f"You can now SSH from {ssh_instance.instance_id} to "
          f"{no_ssh_instance.instance_id}, so try this:")
    print("Copy the private key file to the first instance by running the following "
          "at another command prompt:")
    print(f"\tscp -i {key_file_name} {key_file_name} "
          f"ec2-user@{elastic_ip.public_ip}:{key_file_name}")
    print(f"Create an SSH connection to {ssh_instance.instance_id} by running the "
          f"following at another command prompt:")
    print(f"\tssh -i {key_file_name} ec2-user@{elastic_ip.public_ip}")
    print(f"Then, in the SSH console that's connected to {ssh_instance.instance_id}, "
          f"update the permissions of the private key file by running:")
    print(f"\tchmod 400 {key_file_name}")
    print("Then connect to the second instance by running:")
    print(f"\tssh -i {key_file_name} ec2-user@{no_ssh_instance.private_ip_address}")
    input("When you're done, come back here and press Enter to continue.")

    ec2_instance_management.change_security_group(
        no_ssh_instance.instance_id,
        no_ssh_instance.security_groups[0]['GroupId'],
        ssh_instance.security_groups[0]['GroupId'])
    print(f"Changed the security group for {no_ssh_instance.instance_id} to "
          f"allow SSH from this computer.")
    print("Connect to it at another command prompt:")
    print(f"\tssh -i {key_file_name} ec2-user@{no_ssh_instance.public_ip_address}")
    input("When you're done, come back here and press Enter to continue.")


def teardown_demo(instances, security_groups, key_pair, key_file_name, elastic_ip):
    """
    Cleans up all resources created during the demo, including terminating the
    demo instances.

    After an instance is terminated, it persists in the list
    of instances in your account for up to an hour before it is ultimately removed.

    :param instances: The demo instances to terminate.
    :param security_groups: The security groups to delete.
    :param key_pair: The security key pair to delete.
    :param key_file_name: The private key file to delete.
    :param elastic_ip: The Elastic IP to release.
    """
    ec2_instance_management.disassociate_elastic_ip(elastic_ip.allocation_id)
    ec2_instance_management.release_elastic_ip(elastic_ip.allocation_id)
    print("Released the demo elastic IP.")

    for instance in instances:
        ec2_teardown.terminate_instance(instance.instance_id)
        instance.wait_until_terminated()
    print("Terminated the demo instances.")

    for security_group in security_groups:
        ec2_teardown.delete_security_group(security_group.group_id)
    print("Deleted the demo security groups.")

    ec2_teardown.delete_key_pair(key_pair.name, key_file_name)
    print("Deleted demo key.")


def run_demos():
    """
    """
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    print('-'*88)
    print("Welcome to the Amazon Elastic Compute Cloud (Amazon EC2) basic usage demo.")
    print('-'*88)

    # Get the current public IP address of this computer to allow SSH connections
    # to created instances. This method works in most cases, however, depending on
    # how your computer connects to the internet, you may have to manually add your
    # public IP address to the security group by using the AWS Management Console.
    current_ip_address = urllib.request.urlopen('http://checkip.amazonaws.com')\
        .read().decode('utf-8').strip()
    print(f"Your public IP address is {current_ip_address}. This will be "
          f"used to grant SSH access to the Amazon EC2 instance created for this demo.")

    # Use AWS Systems Manager to find the latest AMI with Amazon Linux 2, x64, and a
    # general-purpose EBS volume.
    ssm = boto3.client('ssm')
    ami_params = ssm.get_parameters_by_path(
        Path='/aws/service/ami-amazon-linux-latest')
    amzn2_amis = [ap for ap in ami_params['Parameters'] if
                  all(query in ap['Name'] for query
                      in ('amzn2', 'x86_64', 'gp2'))]
    if len(amzn2_amis) > 0:
        ami_image_id = amzn2_amis[0]['Value']
        print("Found an Amazon Machine Image (AMI) that includes Amazon Linux 2, "
              "an x64 architecture, and a general-purpose EBS volume.")
        pprint.pprint(amzn2_amis[0])
    elif len(ami_params) > 0:
        ami_image_id = ami_params['Parameters'][0]['Value']
        print("Found an Amazon Machine Image (AMI) to use for the demo.")
        pprint.pprint(ami_params[0])
    else:
        raise RuntimeError(
            "Couldn't find any AMIs. Try a different path or find one in the "
            "AWS Management Console.")

    key_file_name = 'demo-key-file.pem'
    instances, security_groups, key_pair, elastic_ip = setup_demo(
        current_ip_address, ami_image_id, key_file_name)
    management_demo(*instances, key_file_name, elastic_ip)
    teardown_demo(instances, security_groups, key_pair, key_file_name, elastic_ip)
    print("Thanks for watching!")


if __name__ == '__main__':
    run_demos()

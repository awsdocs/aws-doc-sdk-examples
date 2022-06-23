# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use AWS SDK for Python (Boto3) with the Amazon Elastic Compute Cloud
(Amazon EC2) API to manage aspects of an Amazon EC2 instance.
"""

import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)
ec2 = boto3.resource('ec2')


# snippet-start:[python.example_code.ec2.StartInstances]
def start_instance(instance_id):
    """
    Starts an instance. The request returns immediately. To wait for the instance
    to start, use the Instance.wait_until_running() function.

    :param instance_id: The ID of the instance to start.
    :return: The response to the start request. This includes both the previous and
             current state of the instance.
    """
    try:
        response = ec2.Instance(instance_id).start()
        logger.info("Started instance %s.", instance_id)
    except ClientError:
        logger.exception("Couldn't start instance %s.", instance_id)
        raise
    else:
        return response
# snippet-end:[python.example_code.ec2.StartInstances]


# snippet-start:[python.example_code.ec2.StopInstances]
def stop_instance(instance_id):
    """
    Stops an instance. The request returns immediately. To wait for the instance
    to stop, use the Instance.wait_until_stopped() function.

    :param instance_id: The ID of the instance to stop.
    :return: The response to the stop request. This includes both the previous and
             current state of the instance.
    """
    try:
        response = ec2.Instance(instance_id).stop()
        logger.info("Stopped instance %s.", instance_id)
    except ClientError:
        logger.exception("Couldn't stop instance %s.", instance_id)
        raise
    else:
        return response
# snippet-end:[python.example_code.ec2.StopInstances]


# snippet-start:[python.example_code.ec2.AllocateAddress]
def allocate_elastic_ip():
    """
    Allocates an Elastic IP address that can be associated with an instance. By using
    an Elastic IP address, you can keep the public IP address constant even when you
    change the associated instance.

    :return: The newly created Elastic IP object. By default, the address is not
             associated with any instance.
    """
    try:
        response = ec2.meta.client.allocate_address(Domain='vpc')
        elastic_ip = ec2.VpcAddress(response['AllocationId'])
        logger.info("Allocated Elastic IP %s.", elastic_ip.public_ip)
    except ClientError:
        logger.exception("Couldn't allocate Elastic IP.")
        raise
    else:
        return elastic_ip
# snippet-end:[python.example_code.ec2.AllocateAddress]


# snippet-start:[python.example_code.ec2.AssociateAddress]
def associate_elastic_ip(allocation_id, instance_id):
    """
    Associates an Elastic IP address with an instance. When this association is
    created, the Elastic IP's public IP address is immediately used as the public
    IP address of the associated instance.

    :param allocation_id: The allocation ID assigned to the Elastic IP when it was
                          created.
    :param instance_id: The ID of the instance to associate with the Elastic IP.
    :return: The Elastic IP object.
    """
    try:
        elastic_ip = ec2.VpcAddress(allocation_id)
        elastic_ip.associate(InstanceId=instance_id)
        logger.info("Associated Elastic IP %s with instance %s, got association ID %s",
                    elastic_ip.public_ip, instance_id, elastic_ip.association_id)
    except ClientError:
        logger.exception(
            "Couldn't associate Elastic IP %s with instance %s.",
            allocation_id, instance_id)
        raise
    return elastic_ip
# snippet-end:[python.example_code.ec2.AssociateAddress]


# snippet-start:[python.example_code.ec2.DisassociateAddress]
def disassociate_elastic_ip(allocation_id):
    """
    Removes an association between an Elastic IP address and an instance. When the
    association is removed, the instance is assigned a new public IP address.

    :param allocation_id: The allocation ID assigned to the Elastic IP address when
                          it was created.
    """
    try:
        elastic_ip = ec2.VpcAddress(allocation_id)
        elastic_ip.association.delete()
        logger.info(
            "Disassociated Elastic IP %s from its instance.", elastic_ip.public_ip)
    except ClientError:
        logger.exception(
            "Couldn't disassociate Elastic IP %s from its instance.", allocation_id)
        raise
# snippet-end:[python.example_code.ec2.DisassociateAddress]


# snippet-start:[python.example_code.ec2.ReleaseAddress]
def release_elastic_ip(allocation_id):
    """
    Releases an Elastic IP address. After the Elastic IP address is released,
    it can no longer be used.

    :param allocation_id: The allocation ID assigned to the Elastic IP address when
                          it was created.
    """
    try:
        elastic_ip = ec2.VpcAddress(allocation_id)
        elastic_ip.release()
        logger.info("Released Elastic IP address %s.", allocation_id)
    except ClientError:
        logger.exception(
            "Couldn't release Elastic IP address %s.", allocation_id)
        raise
# snippet-end:[python.example_code.ec2.ReleaseAddress]


# snippet-start:[python.example_code.ec2.GetConsoleOutput]
def get_console_output(instance_id):
    """
    Gets the console output of the specified instance.

    :param instance_id: The ID of the instance.
    :return: The console output as a string.
    """
    try:
        output = ec2.Instance(instance_id).console_output()['Output']
        logger.info("Got console output for instance %s.", instance_id)
    except ClientError:
        logger.exception(("Couldn't get console output for instance %s.", instance_id))
        raise
    else:
        return output
# snippet-end:[python.example_code.ec2.GetConsoleOutput]


# snippet-start:[python.example_code.ec2.ModifyNetworkInterfaceAttribute]
def change_security_group(instance_id, old_security_group_id, new_security_group_id):
    """
    Changes the security group of an instance. Security groups are associated with
    the instance's network interfaces, so this function iterates the list of
    network interfaces associated with the specified instance and updates each
    network interface by replacing the old security group with the new security group.

    :param instance_id: The ID of the instance to update.
    :param old_security_group_id: The ID of the security group to replace.
    :param new_security_group_id: The ID of the new security group.
    """
    try:
        for network_interface in ec2.Instance(instance_id).network_interfaces:
            group_ids = [group['GroupId'] for group in network_interface.groups]
            if old_security_group_id in group_ids:
                try:
                    network_interface.modify_attribute(
                        Groups=[new_security_group_id
                                if old_security_group_id == group_id else group_id
                                for group_id in group_ids])
                    logger.info(
                        "Replaced %s with %s for %s.", old_security_group_id,
                        new_security_group_id, network_interface.id)
                except ClientError:
                    logger.exception(
                        "Couldn't update security groups for %s.", network_interface.id)
    except ClientError:
        logger.exception(
            "Couldn't get network interfaces for instance %s.", instance_id)
        raise
# snippet-end:[python.example_code.ec2.ModifyNetworkInterfaceAttribute]


# snippet-start:[python.example_code.ec2.AuthorizeSecurityGroupIngress]
def allow_security_group_ingress(target_security_group_id, source_security_group_name):
    """
    Sets an inbound rule in a security group. The rule lets instances that are in
    the target security group accept incoming traffic that comes from instances
    that are in the source security group. Traffic is accepted over UDP, ICMP, and TCP.

    Note that this does *not* add the source security group's rules to the target
    security group.

    :param target_security_group_id: The security group to update to accept traffic.
    :param source_security_group_name: The security group that will be allowed to send
                                       data to instances in the target security group.
    """
    try:
        ec2.SecurityGroup(target_security_group_id).authorize_ingress(
            SourceSecurityGroupName=source_security_group_name)
        logger.info("Added rule to group %s to allow traffic from instances in "
                    "group %s.", target_security_group_id, source_security_group_name)
    except ClientError:
        logger.exception("Couldn't add rule to group %s to allow traffic from "
                         "instances in %s.",
                         target_security_group_id, source_security_group_name)
        raise
# snippet-end:[python.example_code.ec2.AuthorizeSecurityGroupIngress]

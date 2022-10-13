# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Elastic Compute Cloud (Amazon EC2)
unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from botocore.stub import ANY
from test_tools.example_stubber import ExampleStubber


class Ec2Stubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    EC2 unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 EC2 client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_key_pair(self, key_name, key_material=None, error_code=None):
        expected_params = {'KeyName': key_name}
        response = {'KeyName': key_name}
        if key_material is not None:
            response['KeyMaterial'] = key_material
        self._stub_bifurcator(
            'create_key_pair', expected_params, response, error_code=error_code)

    def stub_describe_key_pairs(self, key_pairs, error_code=None):
        expected_params = {}
        response = {'KeyPairs': key_pairs}
        self._stub_bifurcator(
            'describe_key_pairs', expected_params, response, error_code=error_code)

    def stub_delete_key_pair(self, key_name, error_code=None):
        expected_params = {'KeyName': key_name}
        self._stub_bifurcator(
            'delete_key_pair', expected_params, error_code=error_code)

    def stub_describe_vpcs(self, vpcs, vpc_filters=None, error_code=None):
        expected_params = {}
        if vpc_filters is not None:
            expected_params['Filters'] = vpc_filters
        response = {'Vpcs': [{
            'VpcId': key,
            'InstanceTenancy': 'default' if value else 'dedicated',
            'IsDefault': value
        } for key, value in vpcs.items()]}
        self._stub_bifurcator(
            'describe_vpcs', expected_params, response, error_code=error_code)

    def stub_create_security_group(
            self, group_name, group_id, group_description=ANY, error_code=None):
        expected_params = {
            'GroupName': group_name,
            'Description': group_description,
        }
        response = {'GroupId': group_id}
        self._stub_bifurcator(
            'create_security_group', expected_params, response, error_code=error_code)

    def stub_delete_security_group(self, group_id, error_code=None):
        expected_params = {'GroupId': group_id}
        self._stub_bifurcator(
            'delete_security_group', expected_params, error_code=error_code)

    def stub_authorize_security_group_ingress(
            self, group_id, ip_permissions=None, source_group_name=None,
            error_code=None):
        expected_params = {'GroupId': group_id}
        if ip_permissions is not None:
            expected_params['IpPermissions'] = ip_permissions
        if source_group_name is not None:
            expected_params['SourceSecurityGroupName'] = source_group_name
        response = {'Return': True}
        self._stub_bifurcator(
            'authorize_security_group_ingress', expected_params, response, error_code=error_code)

    def stub_create_instances(
            self, image_id, instance_type, key_name, instance_count, instance_id,
            security_group_ids=None, subnet_id=None, error_code=None):
        expected_params = {
            'ImageId': image_id, 'InstanceType': instance_type, 'KeyName': key_name,
            'MinCount': instance_count, 'MaxCount': instance_count}
        if security_group_ids is not None:
            expected_params['SecurityGroupIds'] = security_group_ids
        if subnet_id is not None:
            expected_params['SubnetId'] = subnet_id
        response = {'Instances': [{'InstanceId': instance_id}]}
        self._stub_bifurcator(
            'run_instances', expected_params, response, error_code=error_code)

    def stub_describe_instances(self, instances, error_code=None):
        expected_params = {'InstanceIds': [instance.id for instance in instances]}
        response = {
            'Reservations': [{
                'Instances': [{
                    'InstanceId': instance.id,
                    'State': instance.state
                } for instance in instances]
            }]
        }
        self._stub_bifurcator(
            'describe_instances', expected_params, response, error_code=error_code)

    def stub_start_instances(self, instance_ids, error_code=None):
        expected_params = {'InstanceIds': instance_ids}
        response = {'StartingInstances': [{
            'InstanceId': instance_id,
            'CurrentState': {'Code': 0, 'Name': 'pending'},
            'PreviousState': {'Code': 80, 'Name': 'stopped'}
        } for instance_id in instance_ids]}
        self._stub_bifurcator(
            'start_instances', expected_params, response, error_code=error_code)

    def stub_stop_instances(self, instance_ids, error_code=None):
        expected_params = {'InstanceIds': instance_ids}
        response = {'StoppingInstances': [{
            'InstanceId': instance_id,
            'CurrentState': {'Code': 80, 'Name': 'stopped'},
            'PreviousState': {'Code': 0, 'Name': 'pending'}
        } for instance_id in instance_ids]}
        self._stub_bifurcator(
            'stop_instances', expected_params, response, error_code=error_code)

    def stub_reboot_instances(self, instance_ids, error_code=None):
        expected_params = {'InstanceIds': instance_ids}
        self._stub_bifurcator(
            'reboot_instances', expected_params, error_code=error_code)

    def stub_terminate_instances(self, instance_ids, error_code=None):
        expected_params = {'InstanceIds': instance_ids}
        self._stub_bifurcator(
            'terminate_instances', expected_params, error_code=error_code)

    def stub_describe_addresses(self, addresses, error_code=None):
        expected_params = {
            'AllocationIds': [address.allocation_id for address in addresses]}
        response = {
            'Addresses': [{
                'InstanceId': address.instance_id,
                'PublicIp': address.public_ip,
                'AllocationId': address.allocation_id,
                'AssociationId': address.association_id,
                'Domain': address.domain,
                'NetworkInterfaceId': address.network_interface_id
            } for address in addresses]
        }
        self._stub_bifurcator(
            'describe_addresses', expected_params, response, error_code=error_code)

    def stub_allocate_elastic_ip(self, address, error_code=None):
        expected_params = {'Domain': address.domain}
        response = {
            'PublicIp': address.public_ip, 'AllocationId': address.allocation_id}
        self._stub_bifurcator(
            'allocate_address', expected_params, response, error_code=error_code)

    def stub_associate_elastic_ip(self, address, instance_id, error_code=None):
        expected_params = {
            'AllocationId': address.allocation_id, 'InstanceId': instance_id}
        response = {'AssociationId': address.association_id}
        self._stub_bifurcator(
            'associate_address', expected_params, response, error_code=error_code)

    def stub_disassociate_elastic_ip(self, association_id, error_code=None):
        expected_params = {'AssociationId': association_id}
        self._stub_bifurcator(
            'disassociate_address', expected_params, error_code=error_code)

    def stub_release_elastic_ip(self, allocation_id, error_code=None):
        expected_params = {'AllocationId': allocation_id}
        self._stub_bifurcator(
            'release_address', expected_params, error_code=error_code)

    def stub_get_console_output(self, instance_id, output, error_code=None):
        expected_params = {'InstanceId': instance_id}
        response = {'InstanceId': instance_id, 'Output': output}
        self._stub_bifurcator(
            'get_console_output', expected_params, response, error_code=error_code)

    def stub_describe_network_interfaces(
            self, instance_id, interfaces, error_code=None):
        expected_params = {'Filters': [{
            'Name': 'attachment.instance-id',
            'Values': [instance_id]}]}
        response = {'NetworkInterfaces': [{
            'NetworkInterfaceId': interface.network_interface_id,
            'Groups': [{'GroupName': group.group_name, 'GroupId': group.group_id}
                       for group in interface.groups]
        } for interface in interfaces]}
        self._stub_bifurcator(
            'describe_network_interfaces', expected_params, response,
            error_code=error_code)

    def stub_modify_network_interface_attribute(
            self, interface_id, group_ids, error_code=None):
        expected_params = {
            'NetworkInterfaceId': interface_id, 'Groups': group_ids}
        self._stub_bifurcator(
            'modify_network_interface_attribute', expected_params,
            error_code=error_code)

    def stub_describe_security_groups(self, groups, error_code=None):
        expected_params = {'GroupIds': [group['id'] for group in groups]}
        response = {'SecurityGroups': [{
            'GroupId': group['id'],
            'GroupName': group['group_name'],
            'IpPermissions': group['ip_permissions']
        } for group in groups]}
        self._stub_bifurcator(
            'describe_security_groups', expected_params, response,
            error_code=error_code)

    def stub_revoke_security_group_ingress(
            self, sec_group, error_code=None):
        expected_params = {
            'GroupId': sec_group['id'],
            'IpPermissions': sec_group['ip_permissions'],
        }
        self._stub_bifurcator(
            'revoke_security_group_ingress', expected_params, error_code=error_code)

    def stub_describe_launch_templates(self, template_names, templates, error_code=None):
        expected_params = {'LaunchTemplateNames': template_names}
        response = {'LaunchTemplates': templates}
        self._stub_bifurcator(
            'describe_launch_templates', expected_params, response, error_code=error_code)

    def stub_create_launch_template(self, template_name, inst_type, ami_id, error_code=None):
        expected_params = {
            'LaunchTemplateName': template_name,
            'LaunchTemplateData': {
                'InstanceType': inst_type, 'ImageId': ami_id}}
        response = {'LaunchTemplate': {'LaunchTemplateName': template_name}}
        self._stub_bifurcator(
            'create_launch_template', expected_params, response, error_code=error_code)

    def stub_delete_launch_template(self, template_name, error_code=None):
        expected_params = {'LaunchTemplateName': template_name}
        response = {}
        self._stub_bifurcator(
            'delete_launch_template', expected_params, response, error_code=error_code)

    def stub_describe_availability_zones(self, zones, error_code=None):
        expected_params = {}
        response = {
            'AvailabilityZones': [{'ZoneName': zone} for zone in zones]
        }
        self._stub_bifurcator(
            'describe_availability_zones', expected_params, response, error_code=error_code)

    def stub_describe_images(self, images, error_code=None):
        expected_params = {'ImageIds': [i.id for i in images]}
        response = {'Images': [{
            'ImageId': image.id, 'Description': image.description, 'Architecture': image.architecture}
            for image in images]}
        self._stub_bifurcator(
            'describe_images', expected_params, response, error_code=error_code)

    def stub_describe_instance_types(self, inst_types, filters=ANY, error_code=None):
        expected_params = {'Filters': filters}
        response = {'InstanceTypes': [{'InstanceType': inst_type} for inst_type in inst_types]}
        self._stub_bifurcator(
            'describe_instance_types', expected_params, response, error_code=error_code)

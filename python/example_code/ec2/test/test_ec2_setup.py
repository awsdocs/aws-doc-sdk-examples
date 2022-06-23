# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for ec2_setup.py functions.
"""

from unittest.mock import patch, mock_open
import pytest
from botocore.exceptions import ClientError

import ec2_setup


@pytest.mark.parametrize(
    'pk_file_name,error_code', [
        (None, None),
        ('test_pk_file.pem', None),
        (None, 'TestException')
    ])
def test_create_key_pair(
        make_stubber, make_unique_name, pk_file_name, error_code):
    ec2_stubber = make_stubber(ec2_setup.ec2.meta.client)
    key_name = make_unique_name('key')
    key_material = 'test-key-material'

    ec2_stubber.stub_create_key_pair(key_name, key_material, error_code)

    if error_code is None:
        with patch('builtins.open', mock_open()) as mock_file:
            key_pair = ec2_setup.create_key_pair(key_name, pk_file_name)
            if pk_file_name is not None:
                mock_file.assert_called_with(pk_file_name, 'w')
        assert key_pair.name == key_name
        assert key_pair.key_material == key_material
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_setup.create_key_pair(key_name, pk_file_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize(
    'has_vpcs,include_ssh,error_code,stop_on_method', [
        (True, True, None, None),
        (True, False, None, None),
        (False, True, None, 'stub_describe_vpcs'),
        (True, True, 'TestException', 'stub_describe_vpcs'),
        (True, True, 'TestException', 'stub_create_security_group'),
        (True, True, 'TestException', 'stub_authorize_security_group_ingress')
    ])
def test_setup_security_group(
        make_stubber, make_unique_name, stub_runner, has_vpcs, include_ssh, error_code,
        stop_on_method):
    ec2_stubber = make_stubber(ec2_setup.ec2.meta.client)
    vpc_filter = {'Name': 'isDefault', 'Values': ['true']}
    if has_vpcs:
        vpc_id = make_unique_name('vpc-')
        vpcs = {vpc_id: True}
    else:
        vpc_id = None
        vpcs = {}
    group_name = make_unique_name('group')
    group_description = "Just a test group."
    group_id = 'test-group-id'
    test_ip = '1.2.3.4' if include_ssh else None
    ip_permissions = [
        {'protocol': 'tcp', 'port': 80, 'ip_ranges': [{'CidrIp': '0.0.0.0/0'}]},
        {'protocol': 'tcp', 'port': 443, 'ip_ranges': [{'CidrIp': '0.0.0.0/0'}]}]
    if include_ssh:
        ip_permissions.append(
            {'protocol': 'tcp', 'port': 22, 'ip_ranges': [{'CidrIp': f'{test_ip}/32'}]})

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(ec2_stubber.stub_describe_vpcs, vpcs, vpc_filters=[vpc_filter])
        runner.add(
            ec2_stubber.stub_create_security_group, group_name, group_description,
            vpc_id, group_id)
        runner.add(
            ec2_stubber.stub_authorize_security_group_ingress, group_id,
            ip_permissions)

    if error_code is None:
        if has_vpcs:
            got_group = ec2_setup.setup_security_group(
                group_name, group_description, test_ip)
            assert got_group.id == group_id
        else:
            with pytest.raises(IndexError):
                ec2_setup.setup_security_group(
                    group_name, group_description, test_ip)
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_setup.setup_security_group(
                group_name, group_description, test_ip)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize(
    'security_groups,error_code',
    [(None, None),
     (['test-security-group'], None),
     (None, 'TestException')])
def test_create_instances(
        make_stubber, make_unique_name, security_groups, error_code):
    ec2_stubber = make_stubber(ec2_setup.ec2.meta.client)
    image_id = 'ami-1234567890EXAMPLE'
    instance_type = 'test-instance-type'
    key_name = 'test-key-name'
    instance_id = 'test-instance-id'

    ec2_stubber.stub_create_instances(
        image_id, instance_type, key_name, 1, instance_id, security_groups,
        error_code=error_code)

    if error_code is None:
        instance = ec2_setup.create_instance(
            image_id, instance_type, key_name, security_groups)
        assert instance.id == instance_id
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_setup.create_instance(
                image_id, instance_type, key_name, security_groups)
        assert exc_info.value.response['Error']['Code'] == error_code

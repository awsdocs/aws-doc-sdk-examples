# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for ec2_instance_management.py functions.
"""

import unittest.mock
import pytest
from botocore.exceptions import ClientError

import ec2_instance_management


def mock_address():
    return unittest.mock.MagicMock(
        allocation_id='mock-allocation-id',
        public_ip='1.2.3.4',
        domain='vpc',
        instance_id='mock-instance-id',
        association_id='mock-association-id',
        network_interface_id='mock-network-interface-id'
    )


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_start_instance(make_stubber, make_unique_name, error_code):
    ec2_stubber = make_stubber(ec2_instance_management.ec2.meta.client)
    instance_id = make_unique_name('instance')

    ec2_stubber.stub_start_instances([instance_id], error_code=error_code)

    if error_code is None:
        response = ec2_instance_management.start_instance(instance_id)
        assert response['StartingInstances'][0]['InstanceId'] == instance_id
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_instance_management.start_instance(instance_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_stop_instance(make_stubber, make_unique_name, error_code):
    ec2_stubber = make_stubber(ec2_instance_management.ec2.meta.client)
    instance_id = make_unique_name('instance')

    ec2_stubber.stub_stop_instances([instance_id], error_code=error_code)

    if error_code is None:
        response = ec2_instance_management.stop_instance(instance_id)
        assert response['StoppingInstances'][0]['InstanceId'] == instance_id
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_instance_management.stop_instance(instance_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize(
    'error_code,stop_on_method',[
        (None, None), ('TestException', 'stub_allocate_elastic_ip')])
def test_allocate_elastic_ip(
        make_stubber, stub_runner, error_code, stop_on_method):
    ec2_stubber = make_stubber(ec2_instance_management.ec2.meta.client)
    address = mock_address()

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(ec2_stubber.stub_allocate_elastic_ip, address)
        runner.add(ec2_stubber.stub_describe_addresses, [address])

    if error_code is None:
        elastic_ip = ec2_instance_management.allocate_elastic_ip()
        assert elastic_ip.public_ip == address.public_ip
        assert elastic_ip.allocation_id == address.allocation_id
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_instance_management.allocate_elastic_ip()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None), ('TestException', 'stub_associate_elastic_ip')])
def test_associate_elastic_ip(
        make_stubber, make_unique_name, stub_runner, error_code, stop_on_method):
    ec2_stubber = make_stubber(ec2_instance_management.ec2.meta.client)
    address = mock_address()
    instance_id = make_unique_name('instance-')

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(ec2_stubber.stub_associate_elastic_ip, address, instance_id)
        runner.add(ec2_stubber.stub_describe_addresses, [address])

    if error_code is None:
        elastic_ip = ec2_instance_management.associate_elastic_ip(
            address.allocation_id, instance_id)
        assert elastic_ip.association_id == address.association_id
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_instance_management.associate_elastic_ip(
                address.allocation_id, instance_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_disassociate_elastic_ip(make_stubber, make_unique_name, error_code):
    ec2_stubber = make_stubber(ec2_instance_management.ec2.meta.client)
    address = mock_address()

    ec2_stubber.stub_describe_addresses([address], error_code=None)
    ec2_stubber.stub_disassociate_elastic_ip(
        address.association_id, error_code=error_code)

    if error_code is None:
        ec2_instance_management.disassociate_elastic_ip(address.allocation_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_instance_management.disassociate_elastic_ip(address.allocation_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_release_elastic_ip(make_stubber, make_unique_name, error_code):
    ec2_stubber = make_stubber(ec2_instance_management.ec2.meta.client)
    address = mock_address()

    ec2_stubber.stub_describe_addresses([address], error_code=None)
    ec2_stubber.stub_release_elastic_ip(
        address.allocation_id, error_code=error_code)

    if error_code is None:
        ec2_instance_management.release_elastic_ip(address.allocation_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_instance_management.release_elastic_ip(address.allocation_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_console_output(make_stubber, make_unique_name, error_code):
    ec2_stubber = make_stubber(ec2_instance_management.ec2.meta.client)
    instance_id = make_unique_name('instance')
    output = 'Hi there, this is test output.'

    ec2_stubber.stub_get_console_output(instance_id, output, error_code=error_code)

    if error_code is None:
        got_output = ec2_instance_management.get_console_output(instance_id)
        assert got_output == output
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_instance_management.get_console_output(instance_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize(
    'interface_count,group_count,error_code,stop_on_method,modify_network_error',
    [(1, 1, None, None, None),
     (1, 1, 'TestException', 'stub_describe_instances', None),
     (1, 1, None, None, 'TestException'),
     (3, 2, None, None, None),
     (1, 4, None, None, None)])
def test_change_security_group(
        make_stubber, make_unique_name, stub_runner, interface_count, group_count,
        error_code, stop_on_method, modify_network_error):
    ec2_stubber = make_stubber(ec2_instance_management.ec2.meta.client)
    instance_id = make_unique_name('instance-')
    old_security_group_id = make_unique_name('old-sg-')
    new_security_group_id = make_unique_name('new-sg-')
    interfaces = [unittest.mock.MagicMock(
        network_interface_id=f'interface-{index}',
        groups=[unittest.mock.MagicMock(
            group_name=f'gname-{gdex}', group_id=f'group-{gdex}')
            for gdex in range(group_count)]
    ) for index in range(interface_count)]
    interfaces[0].groups.append(unittest.mock.MagicMock(
        group_name='replaceable-group', group_id=old_security_group_id))
    instance = unittest.mock.MagicMock(id=instance_id, network_interfaces=interfaces)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(ec2_stubber.stub_describe_instances, [instance])
        runner.add(
            ec2_stubber.stub_modify_network_interface_attribute,
            interfaces[0].network_interface_id,
            [new_security_group_id
             if old_security_group_id == group.group_id else group.group_id
             for group in interfaces[0].groups], error_code=modify_network_error)

    if error_code is None:
        ec2_instance_management.change_security_group(
            instance_id, old_security_group_id, new_security_group_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_instance_management.change_security_group(
                instance_id, old_security_group_id, new_security_group_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_allow_security_group_ingress(make_stubber, make_unique_name, error_code):
    ec2_stubber = make_stubber(ec2_instance_management.ec2.meta.client)
    target_group_id = make_unique_name('target-group-id-')
    source_group_name = make_unique_name('source-group-name-')

    ec2_stubber.stub_authorize_security_group_ingress(
        target_group_id, source_group_name=source_group_name, error_code=error_code)

    if error_code is None:
        ec2_instance_management.allow_security_group_ingress(
            target_group_id, source_group_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_instance_management.allow_security_group_ingress(
                target_group_id, source_group_name)
        assert exc_info.value.response['Error']['Code'] == error_code

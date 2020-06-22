# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for ec2_teardown.py functions.
"""

import os
import pytest
from botocore.exceptions import ClientError

import ec2_teardown


@pytest.mark.parametrize(
    'pk_file_name,error_code', [
        (None, None),
        ('test_pk_file.pem', None),
        (None, 'TestException')
    ])
def test_delete_key_pair(
        make_stubber, monkeypatch, make_unique_name, pk_file_name, error_code):
    ec2_stubber = make_stubber(ec2_teardown.ec2.meta.client)
    key_name = make_unique_name('key')

    monkeypatch.setattr(os, 'remove', lambda x: None)
    ec2_stubber.stub_delete_key_pair(key_name, error_code)

    if error_code is None:
        ec2_teardown.delete_key_pair(key_name, pk_file_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_teardown.delete_key_pair(key_name, pk_file_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_security_group(make_stubber, make_unique_name, error_code):
    ec2_stubber = make_stubber(ec2_teardown.ec2.meta.client)
    group_id = make_unique_name('group-id')

    ec2_stubber.stub_delete_security_group(group_id, error_code)

    if error_code is None:
        ec2_teardown.delete_security_group(group_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_teardown.delete_security_group(group_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_terminate_instance(make_stubber, make_unique_name, error_code):
    ec2_stubber = make_stubber(ec2_teardown.ec2.meta.client)
    instance_id = make_unique_name('group-id')

    ec2_stubber.stub_terminate_instances([instance_id], error_code)

    if error_code is None:
        ec2_teardown.terminate_instance(instance_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            ec2_teardown.terminate_instance(instance_id)
        assert exc_info.value.response['Error']['Code'] == error_code

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for install_libraries.py functions.
"""

import time
import uuid
import pytest
import boto3
from botocore.exceptions import ClientError

import install_libraries


@pytest.mark.parametrize('error_code,stop_on_method,status_details', [
    (None, None, 'Success'),
    ('TestException', 'stub_list_instances', 'Success'),
    ('TestException', 'stub_send_command', 'Success'),
    (None, None, 'InProgress'),
    (None, None, 'Failed')])
def test_install_libraries_on_core_nodes(
        make_stubber, stub_runner, monkeypatch, error_code, stop_on_method,
        status_details):
    emr_client = boto3.client('emr')
    emr_stubber = make_stubber(emr_client)
    ssm_client = boto3.client('ssm')
    ssm_stubber = make_stubber(ssm_client)
    cluster_id = 'j-123456789'
    script_path = 's3://test-bucket/test-script.sh'
    instance_ids = ['i-123456789', 'i-111111111']
    commands = [
        # Copy the shell script from Amazon S3 to each node instance.
        f"aws s3 cp {script_path} /home/hadoop",
        # Run the shell script to install libraries on each node instance.
        "bash /home/hadoop/install_libraries.sh"]
    command_id = str(uuid.uuid4())

    monkeypatch.setattr(time, 'sleep', lambda x: None)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            emr_stubber, 'stub_list_instances', cluster_id, ['CORE'], instance_ids)
        for command in commands:
            runner.add(
                ssm_stubber, 'stub_send_command', instance_ids, [command], command_id)
            runner.add(ssm_stubber, 'stub_list_commands', command_id, status_details)
            if status_details == 'InProgress':
                runner.add(ssm_stubber, 'stub_list_commands', command_id, 'Success')
            elif status_details == 'Failed':
                break

    if error_code is None:
        if status_details == 'Failed':
            with pytest.raises(RuntimeError):
                install_libraries.install_libraries_on_core_nodes(
                    cluster_id, script_path, emr_client, ssm_client)
        else:
            install_libraries.install_libraries_on_core_nodes(
                cluster_id, script_path, emr_client, ssm_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            install_libraries.install_libraries_on_core_nodes(
                cluster_id, script_path, emr_client, ssm_client)
        assert exc_info.value.response['Error']['Code'] == error_code

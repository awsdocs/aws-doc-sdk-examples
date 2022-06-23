# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for security_hub_custom_framework.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from security_hub_custom_framework import SecurityHub


@pytest.mark.parametrize('tokens, error_code, stop_on_action', [
    ([None, None], None, None),
    ([None, '1', None], None, None),
    ([None, None], 'TestException', 'stub_list_controls')])
def test_get_sechub_controls(
        make_stubber, stub_runner, tokens, error_code, stop_on_action):
    auditmanager_client = boto3.client('auditmanager')
    auditmanager_stubber = make_stubber(auditmanager_client)
    sechub = SecurityHub(auditmanager_client)
    control_list = [f'ctl-{"1"*36}', f'ctl-{"2"*36}']
    ctl_sets = 0

    with stub_runner(error_code, stop_on_action) as runner:
        for i_token in range(len(tokens) - 1):
            ctl_sets += 1
            runner.add(
                auditmanager_stubber.stub_list_controls,
                'Standard', 100, tokens[i_token:i_token+2], control_list)
            for ctl in control_list:
                runner.add(
                    auditmanager_stubber.stub_get_control,
                    ctl, 'AWS Security Hub')

    if error_code is None:
        got_control_list = sechub.get_sechub_controls()
        assert [ctl['id'] for ctl in got_control_list] == control_list * ctl_sets
    else:
        with pytest.raises(ClientError) as exc_info:
            sechub.get_sechub_controls()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_custom_framework(make_stubber, error_code):
    auditmanager_client = boto3.client('auditmanager')
    auditmanager_stubber = make_stubber(auditmanager_client)
    sechub = SecurityHub(auditmanager_client)
    controls = [{'id': f'ctl-{index*36}'} for index in ['1', '2']]
    control_sets = [{'name': 'Security-Hub', 'controls': controls}]

    fw = {'name': 'All Security Hub Controls Framework', 'id': f'fw-{"1"*36}'}

    auditmanager_stubber.stub_create_assessment_framework(
        fw['name'], control_sets, fw['id'], error_code=error_code)

    if error_code is None:
        sechub.create_custom_framework(controls)
    else:
        with pytest.raises(ClientError) as exc_info:
            sechub.create_custom_framework(controls)
        assert exc_info.value.response['Error']['Code'] == error_code

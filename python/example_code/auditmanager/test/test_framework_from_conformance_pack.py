# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for framework_from_conformance_pack.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from framework_from_conformance_pack import ConformancePack


@pytest.mark.parametrize('in_name, error_code', [
    ('test-name', None),
    ('garbage', None),
    ('test-name', 'TestException')])
def test_get_conformance_pack(make_stubber, monkeypatch, in_name, error_code):
    config_client = boto3.client('config')
    config_stubber = make_stubber(config_client)
    pack = ConformancePack(config_client, None)
    cpack_name = 'test-name'

    monkeypatch.setattr('builtins.input', lambda x: in_name)

    config_stubber.stub_describe_conformance_packs([cpack_name], error_code=error_code)

    if error_code is None:
        if in_name != 'garbage':
            got_cpack_name = pack.get_conformance_pack()
            assert got_cpack_name == cpack_name
        else:
            with pytest.raises(Exception):
                pack.get_conformance_pack()
    else:
        with pytest.raises(ClientError) as exc_info:
            pack.get_conformance_pack()
        assert exc_info.value.response['Error']['Code'] == error_code


def test_create_custom_controls(make_stubber):
    config_client = boto3.client('config')
    config_stubber = make_stubber(config_client)
    auditmanager_client = boto3.client('auditmanager')
    auditmanager_stubber = make_stubber(auditmanager_client)
    pack = ConformancePack(config_client, auditmanager_client)
    pack_name = 'test-pack_name'
    rule_names = ['rule-1', 'rule-2']
    source_ids = ['src-1', 'src-2']
    control_ids = [f'ctl-{"1"*36}', f'ctl-{"2"*36}']

    config_stubber.stub_describe_conformance_pack_compliance(pack_name, rule_names)
    for rule_name, source_id, control_id in zip(rule_names, source_ids, control_ids):
        config_stubber.stub_describe_config_rules([rule_name], source_ids=[source_id])
        auditmanager_stubber.stub_create_control(
            f'Config-{rule_name}', source_id, control_id)

    got_control_ids = pack.create_custom_controls(pack_name)
    assert [got['id'] for got in got_control_ids] == control_ids


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_custom_framework(make_stubber, error_code):
    auditmanager_client = boto3.client('auditmanager')
    auditmanager_stubber = make_stubber(auditmanager_client)
    pack = ConformancePack(None, auditmanager_client)
    pack_name = 'test-pack_name'
    control_ids = [{'id': f'ctl-{"1"*36}'}, {'id': f'ctl-{"2"*36}'}]
    framework = {'name': f'Config-Conformance-pack-{pack_name}', 'id': f'fw-{"1"*36}'}

    auditmanager_stubber.stub_create_assessment_framework(
        framework['name'], [{'name': pack_name, 'controls': control_ids}],
        framework['id'], error_code=error_code)

    if error_code is None:
        pack.create_custom_framework(pack_name, control_ids)
    else:
        with pytest.raises(ClientError) as exc_info:
            pack.create_custom_framework(pack_name, control_ids)
        assert exc_info.value.response['Error']['Code'] == error_code

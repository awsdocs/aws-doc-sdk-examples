# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
Unit tests for config_rules.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

import sys
import os
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))
from config_rules import ConfigWrapper


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_put_config_rule(make_stubber, error_code):
    config_client = boto3.client("config")
    config_stubber = make_stubber(config_client)
    config = ConfigWrapper(config_client)
    rule_name = "test-rule_name"
    rule = {
        "ConfigRuleName": rule_name,
        "Description": "S3 Public Read Prohibited Bucket Rule",
        "Scope": {"ComplianceResourceTypes": ["AWS::S3::Bucket"]},
        "Source": {
            "Owner": "AWS",
            "SourceIdentifier": "S3_BUCKET_PUBLIC_READ_PROHIBITED",
        },
        "InputParameters": "{}",
        "ConfigRuleState": "ACTIVE",
    }

    config_stubber.stub_put_config_rule(rule, error_code=error_code)

    if error_code is None:
        config.put_config_rule(rule_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            config.put_config_rule(rule_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_config_rule(make_stubber, error_code):
    config_client = boto3.client("config")
    config_stubber = make_stubber(config_client)
    config = ConfigWrapper(config_client)
    rule_name = "test-rule_name"
    rules = [{"ConfigRuleName": rule_name}]

    config_stubber.stub_describe_config_rules([rule_name], error_code=error_code)

    if error_code is None:
        got_rule = config.describe_config_rule(rule_name)
        assert [gr["ConfigRuleName"] for gr in got_rule] == [
            r["ConfigRuleName"] for r in rules
        ]
    else:
        with pytest.raises(ClientError) as exc_info:
            config.describe_config_rule(rule_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_config_rule(make_stubber, error_code):
    config_client = boto3.client("config")
    config_stubber = make_stubber(config_client)
    config = ConfigWrapper(config_client)
    rule_name = "test-rule_name"

    config_stubber.stub_delete_config_rule(rule_name, error_code=error_code)

    if error_code is None:
        config.delete_config_rule(rule_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            config.delete_config_rule(rule_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_put_configuration_recorder(make_stubber, error_code):
    config_client = boto3.client("config")
    config_stubber = make_stubber(config_client)
    config = ConfigWrapper(config_client)
    recorder_name = "test-recorder"
    role_arn = "arn:aws:iam::123456789012:role/config-role"
    
    recorder = {
        'name': recorder_name,
        'roleARN': role_arn,
        'recordingGroup': {
            'allSupported': True,
            'includeGlobalResourceTypes': True
        }
    }

    config_stubber.stub_put_configuration_recorder(recorder, error_code=error_code)

    if error_code is None:
        config.put_configuration_recorder(recorder_name, role_arn)
    else:
        with pytest.raises(ClientError) as exc_info:
            config.put_configuration_recorder(recorder_name, role_arn)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_configuration_recorders(make_stubber, error_code):
    config_client = boto3.client("config")
    config_stubber = make_stubber(config_client)
    config = ConfigWrapper(config_client)
    recorder_name = "test-recorder"
    recorders = [{"name": recorder_name}]

    config_stubber.stub_describe_configuration_recorders([recorder_name], recorders, error_code=error_code)

    if error_code is None:
        got_recorders = config.describe_configuration_recorders([recorder_name])
        assert [gr["name"] for gr in got_recorders] == [r["name"] for r in recorders]
    else:
        with pytest.raises(ClientError) as exc_info:
            config.describe_configuration_recorders([recorder_name])
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_start_configuration_recorder(make_stubber, error_code):
    config_client = boto3.client("config")
    config_stubber = make_stubber(config_client)
    config = ConfigWrapper(config_client)
    recorder_name = "test-recorder"

    config_stubber.stub_start_configuration_recorder(recorder_name, error_code=error_code)

    if error_code is None:
        config.start_configuration_recorder(recorder_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            config.start_configuration_recorder(recorder_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_discovered_resources(make_stubber, error_code):
    config_client = boto3.client("config")
    config_stubber = make_stubber(config_client)
    config = ConfigWrapper(config_client)
    resource_type = "AWS::S3::Bucket"
    resources = [{"resourceType": resource_type, "resourceId": "test-bucket"}]

    config_stubber.stub_list_discovered_resources(resource_type, resources, error_code=error_code)

    if error_code is None:
        got_resources = config.list_discovered_resources(resource_type)
        assert len(got_resources) == len(resources)
    else:
        with pytest.raises(ClientError) as exc_info:
            config.list_discovered_resources(resource_type)
        assert exc_info.value.response["Error"]["Code"] == error_code

@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_configuration_recorder(make_stubber, error_code):
    config_client = boto3.client("config")
    config_stubber = make_stubber(config_client)
    config = ConfigWrapper(config_client)
    recorder_name = "test-recorder"

    config_stubber.stub_delete_configuration_recorder(recorder_name, error_code=error_code)

    if error_code is None:
        config.delete_configuration_recorder(recorder_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            config.delete_configuration_recorder(recorder_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_delivery_channel(make_stubber, error_code):
    config_client = boto3.client("config")
    config_stubber = make_stubber(config_client)
    config = ConfigWrapper(config_client)
    channel_name = "test-channel"

    config_stubber.stub_delete_delivery_channel(channel_name, error_code=error_code)

    if error_code is None:
        config.delete_delivery_channel(channel_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            config.delete_delivery_channel(channel_name)
        assert exc_info.value.response["Error"]["Code"] == error_code
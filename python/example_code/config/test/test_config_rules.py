"""
Unit tests for config_rules.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from config_rules import ConfigWrapper


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_put_config_rule(make_stubber, error_code):
    config_client = boto3.client('config')
    config_stubber = make_stubber(config_client)
    config = ConfigWrapper(config_client)
    rule_name = 'test-rule_name'
    rule = {
        'ConfigRuleName': rule_name,
        'Description': 'S3 Public Read Prohibited Bucket Rule',
        'Scope': {
            'ComplianceResourceTypes': [
                'AWS::S3::Bucket']},
        'Source': {
            'Owner': 'AWS',
            'SourceIdentifier': 'S3_BUCKET_PUBLIC_READ_PROHIBITED'},
        'InputParameters': '{}',
        'ConfigRuleState': 'ACTIVE'}

    config_stubber.stub_put_config_rule(rule, error_code=error_code)

    if error_code is None:
        config.put_config_rule(rule_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            config.put_config_rule(rule_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe_config_rule(make_stubber, error_code):
    config_client = boto3.client('config')
    config_stubber = make_stubber(config_client)
    config = ConfigWrapper(config_client)
    rule_name = 'test-rule_name'
    rules = [{'ConfigRuleName': rule_name}]

    config_stubber.stub_describe_config_rules([rule_name], error_code=error_code)

    if error_code is None:
        got_rule = config.describe_config_rule(rule_name)
        assert ([gr['ConfigRuleName'] for gr in got_rule] ==
                [r['ConfigRuleName'] for r in rules])
    else:
        with pytest.raises(ClientError) as exc_info:
            config.describe_config_rule(rule_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_config_rule(make_stubber, error_code):
    config_client = boto3.client('config')
    config_stubber = make_stubber(config_client)
    config = ConfigWrapper(config_client)
    rule_name = 'test-rule_name'

    config_stubber.stub_delete_config_rule(rule_name, error_code=error_code)

    if error_code is None:
        config.delete_config_rule(rule_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            config.delete_config_rule(rule_name)
        assert exc_info.value.response['Error']['Code'] == error_code

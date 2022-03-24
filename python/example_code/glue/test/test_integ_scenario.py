# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for scenario_getting_started_crawlers_and_jobs.py.
"""

from unittest.mock import patch
import boto3
from botocore.exceptions import ClientError
import pytest

import scaffold
import scenario_getting_started_crawlers_and_jobs as scenario_script


@pytest.mark.integ
def test_run_integ(monkeypatch):
    cf_resource = boto3.resource('cloudformation')
    s3_resource = boto3.resource('s3')
    glue_client = boto3.client('glue')
    iam_resource = boto3.resource('iam')
    stack = cf_resource.Stack('doc-example-glue-integ-test-stack')

    inputs = ['y', '1', 'y', '1', '1', '1', 'y', 'y', 'y']
    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))

    with patch('builtins.print') as mock_print:
        outputs = scaffold.deploy('setup_scenario_getting_started.yaml', stack.name, cf_resource)
        args = scenario_script.parse_args([outputs['RoleName'], outputs['BucketName']])
        monkeypatch.setattr(scenario_script, 'parse_args', lambda x: args)
        scenario_script.main()
        scaffold.destroy(stack, cf_resource, s3_resource)
        mock_print.assert_any_call("\nThanks for watching!")
        with pytest.raises(ClientError) as exc_info:
            glue_client.get_crawler(Name='doc-example-crawler')
        assert exc_info.value.response['Error']['Code'] == 'EntityNotFoundException'
        with pytest.raises(ClientError) as exc_info:
            glue_client.get_database(Name='doc-example-database')
        assert exc_info.value.response['Error']['Code'] == 'EntityNotFoundException'
        with pytest.raises(ClientError) as exc_info:
            s3_resource.meta.client.head_bucket(Bucket=outputs['BucketName'])
        assert exc_info.value.response['Error']['Code'] == '404'
        with pytest.raises(ClientError) as exc_info:
            role = iam_resource.Role(outputs['RoleName'])
            role.load()
        assert exc_info.value.response['Error']['Code'] == 'NoSuchEntity'
        with pytest.raises(ClientError) as exc_info:
            stack.load()
        assert exc_info.value.response['Error']['Code'] == 'ValidationError'

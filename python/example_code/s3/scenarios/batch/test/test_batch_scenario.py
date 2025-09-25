# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest

import boto3

from s3_batch_wrapper import S3BatchWrapper
from cloudformation_helper import CloudFormationHelper
from s3_batch_scenario import S3BatchScenario


@pytest.mark.integ
def test_run_batch_wrapper_scenario_integ(input_mocker, capsys):
    s3_client = boto3.client('s3')
    s3control_client = boto3.client('s3control')
    sts_client = boto3.client('sts')
    cfn_client = boto3.client('cloudformation')

    batch_wrapper = S3BatchWrapper(s3_client, s3control_client, sts_client)
    cf_helper = CloudFormationHelper(cfn_client)
    scenario = S3BatchScenario(batch_wrapper, cf_helper)

    input_mocker.mock_answers(
        ["y", "y", "y", "n", "y", "y", "y", "y", "y", "y", "y"] # yes to proceed, no to cancel, yes to cleanup
    )

    scenario.run_scenario()

    capt = capsys.readouterr()
    assert "has successfully completed" in capt.out

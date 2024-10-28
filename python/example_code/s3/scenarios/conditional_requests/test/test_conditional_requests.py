# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest

import boto3

from s3_conditional_requests import S3ConditionalRequests
from scenario import ConditionalRequestsScenario


@pytest.mark.integ
def test_run_conditional_requests_scenario_integ(input_mocker, capsys):
    conditional_requests = S3ConditionalRequests.from_client()
    scenario = ConditionalRequestsScenario(conditional_requests, boto3.client("s3"))

    input_mocker.mock_answers(
        ["cr-integ-tests", "1", "2", "1", "3", "1", "test-copy", "4", "test-write", "5"]
    )

    scenario.run_scenario()

    capt = capsys.readouterr()
    assert "Thanks for watching." in capt.out

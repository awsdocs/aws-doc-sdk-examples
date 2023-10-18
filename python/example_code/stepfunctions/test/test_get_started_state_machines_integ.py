# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import pytest

from activities import Activity
from state_machines import StateMachine
from get_started_state_machines import StateMachineScenario


@pytest.fixture
def mock_wait(monkeypatch):
    return


@pytest.mark.skip(
    reason="Skip until shared resources are part of the Docker environment."
)
@pytest.mark.integ
def test_run_get_started_state_machines_integ(input_mocker, capsys):
    stepfunctions_client = boto3.client("stepfunctions")
    iam_client = boto3.client("iam")
    scenario = StateMachineScenario(
        Activity(stepfunctions_client), StateMachine(stepfunctions_client), iam_client
    )

    input_mocker.mock_answers(
        [
            "Testerson",  # Username.
            4,  # 'done' action.
            "y",  # Cleanup.
        ]
    )

    scenario.prerequisites("doc-example-test-state-machine-chat")
    scenario.run_scenario("doc-example-test-activity", "doc-example-test-state-machine")

    capt = capsys.readouterr()
    assert "Thanks for watching!" in capt.out

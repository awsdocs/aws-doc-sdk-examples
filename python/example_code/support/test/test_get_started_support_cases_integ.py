# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import pytest

from support_wrapper import SupportWrapper
from get_started_support_cases import SupportCasesScenario


@pytest.fixture
def mock_wait(monkeypatch):
    return


@pytest.mark.integ
def test_run_get_started_scenario_integ(input_mocker, capsys):
    support_client = boto3.client("support")
    support_wrapper = SupportWrapper(support_client)
    scenario = SupportCasesScenario(support_wrapper)

    input_mocker.mock_answers(
        [
            1,  # Support service choice.
            1,  # Category choice.
            1,  # Severity choice.
        ]
    )

    scenario.run_scenario()

    capt = capsys.readouterr()
    assert "Thanks for watching!" in capt.out

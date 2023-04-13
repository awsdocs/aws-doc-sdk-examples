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
def test_run_get_started_support_cases_integ(input_mocker, capsys):
    support_client = boto3.client('support')
    scenario = SupportCasesScenario(
        SupportWrapper(support_client))

    input_mocker.mock_answers([
        10,        # Support service choice.
        5,         # Category choice.
        1,         # Severity choice.
    ])

    scenario.run_scenario()

    capt = capsys.readouterr()
    assert "Thanks for watching!" in capt.out

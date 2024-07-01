# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest

from document import DocumentWrapper
from maintenance_window import MaintenanceWindowWrapper
from ops_item import OpsItemWrapper
from ssm_getting_started import SystemsManagerScenario


@pytest.fixture
def mock_wait(monkeypatch):
    return


@pytest.mark.integ
def test_run_ssm_scenario_integ(input_mocker, capsys):
    scenario = SystemsManagerScenario(
        DocumentWrapper.from_client(),
        MaintenanceWindowWrapper.from_client(),
        OpsItemWrapper.from_client(),
    )

    input_mocker.mock_answers(
        [
            "",  # Please hit Enter.
            "python-scenario-test",  # Please enter the maintenance window name (default is ssm-maintenance-window):.
            "",  # Please hit Enter.
            "python-scenario-test",  # Please enter the document name (default is ssmdocument).
            "n",  # Would you like to run a command on an EC2 instance?
            "",  # Please hit Enter.
            "",  # Please hit Enter.
            "",  # Please hit Enter.
            "",  # Please hit Enter.
            "y",  # Would you like to delete the Systems Manager resources? (y/n).
        ]
    )

    scenario.run()

    capt = capsys.readouterr()
    assert "This concludes the Systems Manager SDK Getting Started scenario" in capt.out

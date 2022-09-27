# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest

from instance_wrapper import InstanceWrapper
from scenario_get_started_instances import RdsInstanceScenario


@pytest.mark.integ
def test_run_instance_scenario_integ(input_mocker, capsys):
    scenario = RdsInstanceScenario(InstanceWrapper.from_client())

    input_mocker.mock_answers(
        [1, '1', '1', 'admin', 'password', 1, 1, 'y', 'y'])

    scenario.run_scenario(
        'mysql', 'doc-example-test-parameter-group', 'doc-example-test-instance', 'docexampletestdb')

    capt = capsys.readouterr()
    assert "Thanks for watching!" in capt.out

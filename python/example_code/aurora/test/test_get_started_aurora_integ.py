# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest

from aurora_wrapper import AuroraWrapper
from scenario_get_started_aurora import AuroraClusterScenario


@pytest.mark.integ
def test_run_cluster_scenario_integ(input_mocker, capsys):
    scenario = AuroraClusterScenario(AuroraWrapper.from_client())

    input_mocker.mock_answers(
        [1, '1', '1', 'admin', 'password', 1, 1, 'y', 'y'])

    scenario.run_scenario(
        'aurora-mysql', 'doc-example-test-cluster-group',
        'doc-example-test-aurora', 'docexampletestdb')

    capt = capsys.readouterr()
    assert "Thanks for watching!" in capt.out

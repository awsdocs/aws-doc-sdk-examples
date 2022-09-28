# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest

from scenario_get_started_aurora import AuroraClusterScenario


def test_display_connection(capsys):
    cluster = {'Endpoint': 'test-endpoint', 'Port': 1313, 'MasterUsername': 'test-user'}

    AuroraClusterScenario.display_connection(cluster)

    capt = capsys.readouterr()
    assert cluster['Endpoint'] in capt.out
    assert str(cluster['Port']) in capt.out
    assert cluster['MasterUsername'] in capt.out

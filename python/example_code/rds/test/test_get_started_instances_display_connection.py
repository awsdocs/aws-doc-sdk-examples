# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from scenario_get_started_instances import RdsInstanceScenario


def test_display_connection(capsys):
    db_inst = {'Endpoint': {'Address': 'test-endpoint', 'Port': 1313}, 'MasterUsername': 'test-user'}

    RdsInstanceScenario.display_connection(db_inst)

    capt = capsys.readouterr()
    assert db_inst['Endpoint']['Address'] in capt.out
    assert str(db_inst['Endpoint']['Port']) in capt.out
    assert db_inst['MasterUsername'] in capt.out

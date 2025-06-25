# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
import boto3
from botocore.exceptions import ClientError
from test_tools.neptune_stubber import Neptune
from example_code.neptune.neptune_scenario import create_db_instance

class DummyWaiter:
    def __init__(self, name):
        self.name = name
    def wait(self, **kwargs):
        return None

def test_create_db_instance():
    boto_client = boto3.client("neptune")
    stubber = Neptune(boto_client)

    instance_id = "my-instance"
    cluster_id = "my-cluster"

    stubber.client.get_waiter = lambda name: DummyWaiter(name)

    stubber.stub_create_db_instance(instance_id, cluster_id)
    result = create_db_instance(stubber.client, instance_id, cluster_id)
    assert result == instance_id

    stubber.stubber.add_response(
        "create_db_instance",
        {"DBInstance": {}},
        expected_params={
            "DBInstanceIdentifier": "no-id-instance",
            "DBInstanceClass": "db.r5.large",
            "Engine": "neptune",
            "DBClusterIdentifier": cluster_id
        }
    )
    with pytest.raises(RuntimeError, match="no ID returned"):
        create_db_instance(stubber.client, "no-id-instance", cluster_id)

    stubber.stub_create_db_instance("fail-instance", cluster_id, error_code="AccessDenied")
    with pytest.raises(ClientError) as e:
        create_db_instance(stubber.client, "fail-instance", cluster_id)
    assert "AccessDenied error" in str(e.value)

    def broken_call(**kwargs):
        raise Exception("DB is on fire")
    stubber.client.create_db_instance = broken_call
    with pytest.raises(RuntimeError, match="Unexpected error creating DB instance 'boom-instance'"):
        create_db_instance(stubber.client, "boom-instance", cluster_id)

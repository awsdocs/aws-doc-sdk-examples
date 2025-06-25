# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
from botocore.exceptions import ClientError
from test_tools.neptune_stubber import Neptune
from example_code.neptune.neptune_scenario import delete_db_instance

def test_delete_db_instance_success():
    import boto3
    neptune_client = boto3.client("neptune", region_name="us-east-1")
    stubber = Neptune(neptune_client)

    instance_id = "instance-1"
    stubber.stub_delete_db_instance(instance_id, statuses=["deleting", "deleted"])

    delete_db_instance(neptune_client, instance_id)
    stubber.stubber.assert_no_pending_responses()


def test_delete_db_instance_client_error():
    import boto3
    neptune_client = boto3.client("neptune", region_name="us-east-1")
    stubber = Neptune(neptune_client)

    instance_id = "bad-instance"
    stubber.stub_delete_db_instance(instance_id, error_code="InvalidDBInstanceState")

    with pytest.raises(ClientError) as exc_info:
        delete_db_instance(neptune_client, instance_id)

    assert "InvalidDBInstanceState" in str(exc_info.value)
    stubber.stubber.assert_no_pending_responses()

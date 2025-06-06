# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import builtins
import pytest
from unittest.mock import patch

# Import your scenario file; ensure Python can locate it.
# If your file is named `neptune_scenario.py`, this import will work:
import NeptuneScenario


@pytest.mark.integration
def test_neptune_run_scenario(monkeypatch):
    # Patch input() to simulate all required inputs
    input_sequence = iter([
        "c",  # Step 1: create subnet group
        "c",  # Step 2: create cluster
        "c",  # Step 3: create instance
        "c",  # Step 4: wait for instance
        "c",  # Step 5: describe cluster
        "c",  # Step 6: stop cluster
        "c",  # Step 7: start cluster
        "y"   # Step 8: delete resources
    ])

    monkeypatch.setattr(builtins, "input", lambda: next(input_sequence))

    # You can override these to make test-friendly unique names
    subnet_group_name = "test-subnet-group-inte112"
    cluster_name = "test-cluster-integ11"
    db_instance_id = "test-db-instance-integ11"

    neptune_client = boto3.client("neptune", region_name="us-east-1")

    # Run the full scenario
    NeptuneScenario.run_scenario(
        neptune_client,
        subnet_group_name=subnet_group_name,
        db_instance_id=db_instance_id,
        cluster_name=cluster_name,
    )

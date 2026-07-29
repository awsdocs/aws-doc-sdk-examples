# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for Amazon ECS wrapper.
These tests call real AWS services and require valid credentials.

Run with: pytest test_ecs_integ_real.py -v
Requires: AWS credentials with ECS permissions (create/delete cluster, register/deregister task def)
"""

import sys
import os
import uuid

import pytest
import boto3
from botocore.exceptions import ClientError

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from ecs_wrapper import EcsWrapper

# Unique suffix to avoid collisions between parallel test runs
UNIQUE_ID = uuid.uuid4().hex[:8]
CLUSTER_NAME = f"sdk-example-integ-test-{UNIQUE_ID}"
TASK_FAMILY = f"sdk-example-integ-task-{UNIQUE_ID}"


@pytest.fixture(scope="module")
def ecs_wrapper():
    """Create an EcsWrapper with a real ECS client."""
    client = boto3.client("ecs", region_name="us-east-1")
    return EcsWrapper(client)


@pytest.fixture(scope="module")
def cluster(ecs_wrapper):
    """Create a cluster for tests, then delete it after all tests complete."""
    cluster_info = ecs_wrapper.create_cluster(CLUSTER_NAME)
    yield cluster_info
    # Cleanup
    try:
        ecs_wrapper.delete_cluster(CLUSTER_NAME)
    except ClientError:
        pass


class TestEcsIntegration:
    """Lightweight integration tests for ECS wrapper methods."""

    def test_create_cluster(self, cluster):
        """Verify cluster was created successfully."""
        assert cluster["clusterName"] == CLUSTER_NAME
        assert cluster["status"] == "ACTIVE"
        assert "clusterArn" in cluster

    def test_list_clusters(self, ecs_wrapper, cluster):
        """Verify the created cluster appears in the list."""
        cluster_arns = ecs_wrapper.list_clusters()
        matching = [arn for arn in cluster_arns if CLUSTER_NAME in arn]
        assert len(matching) == 1

    def test_register_and_describe_task_definition(self, ecs_wrapper, cluster):
        """Register a task definition, describe it, then deregister."""
        # Use a dummy execution role ARN — registration succeeds even if role
        # doesn't exist (validation happens at task run time, not registration)
        dummy_role_arn = f"arn:aws:iam::{boto3.client('sts').get_caller_identity()['Account']}:role/ecsTaskExecutionRole"

        try:
            task_def = ecs_wrapper.register_task_definition(
                family=TASK_FAMILY,
                execution_role_arn=dummy_role_arn,
                container_name="test-container",
                container_image="public.ecr.aws/amazonlinux/amazonlinux:minimal",
                cpu="256",
                memory="512",
                container_port=80,
            )

            assert task_def["family"] == TASK_FAMILY
            assert task_def["status"] == "ACTIVE"
            assert task_def["revision"] == 1
            assert task_def["networkMode"] == "awsvpc"
            assert task_def["requiresCompatibilities"] == ["FARGATE"]

            # Describe it
            described = ecs_wrapper.describe_task_definition(f"{TASK_FAMILY}:1")
            assert described["family"] == TASK_FAMILY
            assert described["taskDefinitionArn"] == task_def["taskDefinitionArn"]

        finally:
            # Deregister the task definition
            try:
                ecs_wrapper.ecs_client.deregister_task_definition(
                    taskDefinition=f"{TASK_FAMILY}:1"
                )
            except ClientError:
                pass

    def test_delete_cluster(self, ecs_wrapper):
        """Create and immediately delete a cluster to test deletion."""
        temp_name = f"sdk-example-integ-temp-{UNIQUE_ID}"
        ecs_wrapper.create_cluster(temp_name)
        result = ecs_wrapper.delete_cluster(temp_name)
        assert result["clusterName"] == temp_name
        assert result["status"] == "INACTIVE"

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for ecs_wrapper.py — Amazon ECS Basics.

These tests use botocore.stub.Stubber ONLY. No real AWS calls are made.
All tests run offline without AWS credentials.

Usage:
    pytest test_ecs_basics.py -v
"""

import pytest
import boto3
from botocore.stub import Stubber
from botocore.exceptions import ClientError

from ecs_wrapper import EcsWrapper


# ---------------------------------------------------------------------------
# Shared constants
# ---------------------------------------------------------------------------
CLUSTER_NAME = "test-ecs-cluster"
CLUSTER_ARN = "arn:aws:ecs:us-west-2:123456789012:cluster/test-ecs-cluster"
TASK_FAMILY = "test-task-def"
TASK_DEF_ARN = "arn:aws:ecs:us-west-2:123456789012:task-definition/test-task-def:1"
EXECUTION_ROLE_ARN = "arn:aws:iam::123456789012:role/ecsTaskExecutionRole"
TASK_ARN = "arn:aws:ecs:us-west-2:123456789012:task/test-ecs-cluster/abc123def456"
SERVICE_NAME = "test-ecs-service"
SERVICE_ARN = "arn:aws:ecs:us-west-2:123456789012:service/test-ecs-cluster/test-ecs-service"
SUBNET_1 = "subnet-11111111"
SUBNET_2 = "subnet-22222222"
SECURITY_GROUP = "sg-33333333"


# ---------------------------------------------------------------------------
# Fixtures
# ---------------------------------------------------------------------------
@pytest.fixture()
def ecs_stubber():
    """Creates an ECS client with Stubber, returns (wrapper, stubber)."""
    client = boto3.client("ecs", region_name="us-west-2")
    stubber = Stubber(client)
    wrapper = EcsWrapper(client)
    stubber.activate()
    yield wrapper, stubber
    stubber.deactivate()


# ---------------------------------------------------------------------------
# Test class — ordered so each test is independent
# ---------------------------------------------------------------------------
class TestEcsBasicsScenario:
    """Unit tests for the ECS wrapper using botocore Stubber."""

    # ---- 01: CreateCluster ----
    def test_01_create_cluster(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        expected_params = {
            "clusterName": CLUSTER_NAME,
            "settings": [{"name": "containerInsights", "value": "enabled"}],
        }
        response = {
            "cluster": {
                "clusterArn": CLUSTER_ARN,
                "clusterName": CLUSTER_NAME,
                "status": "ACTIVE",
                "registeredContainerInstancesCount": 0,
                "runningTasksCount": 0,
                "pendingTasksCount": 0,
                "activeServicesCount": 0,
                "settings": [{"name": "containerInsights", "value": "enabled"}],
                "capacityProviders": [],
                "defaultCapacityProviderStrategy": [],
                "statistics": [],
                "tags": [],
                "attachments": [],
            }
        }
        stubber.add_response("create_cluster", response, expected_params)
        result = wrapper.create_cluster(CLUSTER_NAME)
        assert result["clusterName"] == CLUSTER_NAME
        assert result["status"] == "ACTIVE"
        stubber.assert_no_pending_responses()

    # ---- 02: RegisterTaskDefinition ----
    def test_02_register_task_definition(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        expected_params = {
            "family": TASK_FAMILY,
            "networkMode": "awsvpc",
            "requiresCompatibilities": ["FARGATE"],
            "cpu": "256",
            "memory": "512",
            "executionRoleArn": EXECUTION_ROLE_ARN,
            "containerDefinitions": [
                {
                    "name": "sample-app",
                    "image": "public.ecr.aws/docker/library/httpd:2.4",
                    "essential": True,
                    "portMappings": [
                        {
                            "containerPort": 80,
                            "hostPort": 80,
                            "protocol": "tcp",
                        }
                    ],
                    "cpu": 256,
                    "memory": 512,
                }
            ],
        }
        response = {
            "taskDefinition": {
                "taskDefinitionArn": TASK_DEF_ARN,
                "family": TASK_FAMILY,
                "revision": 1,
                "status": "ACTIVE",
                "networkMode": "awsvpc",
                "containerDefinitions": [
                    {
                        "name": "sample-app",
                        "image": "public.ecr.aws/docker/library/httpd:2.4",
                        "cpu": 256,
                        "memory": 512,
                        "essential": True,
                        "portMappings": [
                            {
                                "containerPort": 80,
                                "hostPort": 80,
                                "protocol": "tcp",
                            }
                        ],
                    }
                ],
                "requiresCompatibilities": ["FARGATE"],
                "cpu": "256",
                "memory": "512",
                "compatibilities": ["EC2", "FARGATE"],
            }
        }
        stubber.add_response("register_task_definition", response, expected_params)
        result = wrapper.register_task_definition(
            family=TASK_FAMILY,
            execution_role_arn=EXECUTION_ROLE_ARN,
        )
        assert result["family"] == TASK_FAMILY
        assert result["revision"] == 1
        assert result["status"] == "ACTIVE"
        stubber.assert_no_pending_responses()

    # ---- 03: DescribeClusters ----
    def test_03_describe_clusters(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        expected_params = {
            "clusters": [CLUSTER_NAME],
            "include": ["STATISTICS"],
        }
        response = {
            "clusters": [
                {
                    "clusterArn": CLUSTER_ARN,
                    "clusterName": CLUSTER_NAME,
                    "status": "ACTIVE",
                    "registeredContainerInstancesCount": 0,
                    "runningTasksCount": 0,
                    "pendingTasksCount": 0,
                    "activeServicesCount": 0,
                    "settings": [],
                    "statistics": [],
                    "tags": [],
                    "capacityProviders": [],
                    "defaultCapacityProviderStrategy": [],
                    "attachments": [],
                }
            ],
            "failures": [],
        }
        stubber.add_response("describe_clusters", response, expected_params)
        result = wrapper.describe_clusters([CLUSTER_NAME])
        assert len(result) == 1
        assert result[0]["clusterName"] == CLUSTER_NAME
        assert result[0]["status"] == "ACTIVE"
        stubber.assert_no_pending_responses()

    # ---- 04: RunTask ----
    def test_04_run_task(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        expected_params = {
            "cluster": CLUSTER_NAME,
            "taskDefinition": f"{TASK_FAMILY}:1",
            "launchType": "FARGATE",
            "count": 1,
            "networkConfiguration": {
                "awsvpcConfiguration": {
                    "subnets": [SUBNET_1, SUBNET_2],
                    "securityGroups": [SECURITY_GROUP],
                    "assignPublicIp": "ENABLED",
                }
            },
        }
        response = {
            "tasks": [
                {
                    "taskArn": TASK_ARN,
                    "clusterArn": CLUSTER_ARN,
                    "taskDefinitionArn": TASK_DEF_ARN,
                    "lastStatus": "PROVISIONING",
                    "desiredStatus": "RUNNING",
                    "cpu": "256",
                    "memory": "512",
                    "launchType": "FARGATE",
                    "containers": [
                        {
                            "containerArn": f"{TASK_ARN}/sample-app",
                            "name": "sample-app",
                            "lastStatus": "PENDING",
                            "networkInterfaces": [],
                        }
                    ],
                    "attachments": [],
                    "tags": [],
                    "overrides": {
                        "containerOverrides": [
                            {
                                "name": "sample-app",
                            }
                        ],
                    },
                }
            ],
            "failures": [],
        }
        stubber.add_response("run_task", response, expected_params)
        result = wrapper.run_task(
            cluster=CLUSTER_NAME,
            task_definition=f"{TASK_FAMILY}:1",
            subnets=[SUBNET_1, SUBNET_2],
            security_groups=[SECURITY_GROUP],
        )
        assert len(result) == 1
        assert result[0]["taskArn"] == TASK_ARN
        assert result[0]["lastStatus"] == "PROVISIONING"
        stubber.assert_no_pending_responses()

    # ---- 05: DescribeTasks ----
    def test_05_describe_tasks(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        expected_params = {
            "cluster": CLUSTER_NAME,
            "tasks": [TASK_ARN],
        }
        response = {
            "tasks": [
                {
                    "taskArn": TASK_ARN,
                    "clusterArn": CLUSTER_ARN,
                    "taskDefinitionArn": TASK_DEF_ARN,
                    "lastStatus": "RUNNING",
                    "desiredStatus": "RUNNING",
                    "cpu": "256",
                    "memory": "512",
                    "launchType": "FARGATE",
                    "containers": [
                        {
                            "containerArn": f"{TASK_ARN}/sample-app",
                            "name": "sample-app",
                            "lastStatus": "RUNNING",
                            "networkInterfaces": [
                                {
                                    "attachmentId": "att-12345",
                                    "privateIpv4Address": "10.0.1.50",
                                }
                            ],
                        }
                    ],
                    "attachments": [],
                    "tags": [],
                    "overrides": {
                        "containerOverrides": [
                            {
                                "name": "sample-app",
                            }
                        ],
                    },
                }
            ],
            "failures": [],
        }
        stubber.add_response("describe_tasks", response, expected_params)
        result = wrapper.describe_tasks(CLUSTER_NAME, [TASK_ARN])
        assert len(result) == 1
        assert result[0]["lastStatus"] == "RUNNING"
        assert result[0]["containers"][0]["name"] == "sample-app"
        stubber.assert_no_pending_responses()

    # ---- 06: CreateService ----
    def test_06_create_service(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        expected_params = {
            "cluster": CLUSTER_NAME,
            "serviceName": SERVICE_NAME,
            "taskDefinition": f"{TASK_FAMILY}:1",
            "desiredCount": 1,
            "launchType": "FARGATE",
            "networkConfiguration": {
                "awsvpcConfiguration": {
                    "subnets": [SUBNET_1, SUBNET_2],
                    "securityGroups": [SECURITY_GROUP],
                    "assignPublicIp": "ENABLED",
                }
            },
        }
        response = {
            "service": {
                "serviceArn": SERVICE_ARN,
                "serviceName": SERVICE_NAME,
                "clusterArn": CLUSTER_ARN,
                "status": "ACTIVE",
                "desiredCount": 1,
                "runningCount": 0,
                "pendingCount": 0,
                "launchType": "FARGATE",
                "taskDefinition": TASK_DEF_ARN,
                "deployments": [
                    {
                        "id": "ecs-svc/1234567890",
                        "status": "PRIMARY",
                        "desiredCount": 1,
                        "runningCount": 0,
                        "pendingCount": 0,
                        "launchType": "FARGATE",
                    }
                ],
                "events": [],
                "roleArn": "",
                "loadBalancers": [],
                "serviceRegistries": [],
                "networkConfiguration": {
                    "awsvpcConfiguration": {
                        "subnets": [SUBNET_1, SUBNET_2],
                        "securityGroups": [SECURITY_GROUP],
                        "assignPublicIp": "ENABLED",
                    }
                },
            }
        }
        stubber.add_response("create_service", response, expected_params)
        result = wrapper.create_service(
            cluster=CLUSTER_NAME,
            service_name=SERVICE_NAME,
            task_definition=f"{TASK_FAMILY}:1",
            desired_count=1,
            subnets=[SUBNET_1, SUBNET_2],
            security_groups=[SECURITY_GROUP],
        )
        assert result["serviceName"] == SERVICE_NAME
        assert result["status"] == "ACTIVE"
        assert result["desiredCount"] == 1
        stubber.assert_no_pending_responses()

    # ---- 07: ListTasks (paginated) ----
    def test_07_list_tasks(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        # The paginator calls list_tasks under the hood.
        # We stub two pages to verify pagination works.
        task_arn_1 = f"{TASK_ARN}-1"
        task_arn_2 = f"{TASK_ARN}-2"
        expected_params_page1 = {
            "cluster": CLUSTER_NAME,
            "serviceName": SERVICE_NAME,
        }
        response_page1 = {
            "taskArns": [task_arn_1],
            "nextToken": "page2token",
        }
        expected_params_page2 = {
            "cluster": CLUSTER_NAME,
            "serviceName": SERVICE_NAME,
            "nextToken": "page2token",
        }
        response_page2 = {
            "taskArns": [task_arn_2],
        }
        stubber.add_response("list_tasks", response_page1, expected_params_page1)
        stubber.add_response("list_tasks", response_page2, expected_params_page2)
        result = wrapper.list_tasks(cluster=CLUSTER_NAME, service_name=SERVICE_NAME)
        assert len(result) == 2
        assert task_arn_1 in result
        assert task_arn_2 in result
        stubber.assert_no_pending_responses()

    # ---- 08: DescribeServices ----
    def test_08_describe_services(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        expected_params = {
            "cluster": CLUSTER_NAME,
            "services": [SERVICE_NAME],
        }
        response = {
            "services": [
                {
                    "serviceArn": SERVICE_ARN,
                    "serviceName": SERVICE_NAME,
                    "clusterArn": CLUSTER_ARN,
                    "status": "ACTIVE",
                    "desiredCount": 1,
                    "runningCount": 1,
                    "pendingCount": 0,
                    "launchType": "FARGATE",
                    "taskDefinition": TASK_DEF_ARN,
                    "deployments": [],
                    "events": [
                        {
                            "id": "event-1",
                            "message": "service has reached a steady state.",
                        }
                    ],
                    "roleArn": "",
                    "loadBalancers": [],
                    "serviceRegistries": [],
                    "networkConfiguration": {
                        "awsvpcConfiguration": {
                            "subnets": [SUBNET_1, SUBNET_2],
                            "securityGroups": [SECURITY_GROUP],
                            "assignPublicIp": "ENABLED",
                        }
                    },
                }
            ],
            "failures": [],
        }
        stubber.add_response("describe_services", response, expected_params)
        result = wrapper.describe_services(CLUSTER_NAME, [SERVICE_NAME])
        assert len(result) == 1
        assert result[0]["serviceName"] == SERVICE_NAME
        assert result[0]["runningCount"] == 1
        stubber.assert_no_pending_responses()

    # ---- 09: UpdateService ----
    def test_09_update_service(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        expected_params = {
            "cluster": CLUSTER_NAME,
            "service": SERVICE_NAME,
            "desiredCount": 2,
        }
        response = {
            "service": {
                "serviceArn": SERVICE_ARN,
                "serviceName": SERVICE_NAME,
                "clusterArn": CLUSTER_ARN,
                "status": "ACTIVE",
                "desiredCount": 2,
                "runningCount": 1,
                "pendingCount": 1,
                "launchType": "FARGATE",
                "taskDefinition": TASK_DEF_ARN,
                "deployments": [
                    {
                        "id": "ecs-svc/1234567890",
                        "status": "PRIMARY",
                        "desiredCount": 2,
                        "runningCount": 1,
                        "pendingCount": 1,
                        "launchType": "FARGATE",
                    }
                ],
                "events": [],
                "roleArn": "",
                "loadBalancers": [],
                "serviceRegistries": [],
                "networkConfiguration": {
                    "awsvpcConfiguration": {
                        "subnets": [SUBNET_1, SUBNET_2],
                        "securityGroups": [SECURITY_GROUP],
                        "assignPublicIp": "ENABLED",
                    }
                },
            }
        }
        stubber.add_response("update_service", response, expected_params)
        result = wrapper.update_service(
            cluster=CLUSTER_NAME,
            service=SERVICE_NAME,
            desired_count=2,
        )
        assert result["desiredCount"] == 2
        assert result["serviceName"] == SERVICE_NAME
        stubber.assert_no_pending_responses()

    # ---- 10: Cleanup — DeleteService, DeregisterTaskDefinition, DeleteCluster ----
    def test_10_cleanup(self, ecs_stubber):
        wrapper, stubber = ecs_stubber

        # 10a: DeleteService
        delete_svc_params = {
            "cluster": CLUSTER_NAME,
            "service": SERVICE_NAME,
            "force": True,
        }
        delete_svc_response = {
            "service": {
                "serviceArn": SERVICE_ARN,
                "serviceName": SERVICE_NAME,
                "clusterArn": CLUSTER_ARN,
                "status": "DRAINING",
                "desiredCount": 0,
                "runningCount": 0,
                "pendingCount": 0,
                "launchType": "FARGATE",
                "taskDefinition": TASK_DEF_ARN,
                "deployments": [],
                "events": [],
                "roleArn": "",
                "loadBalancers": [],
                "serviceRegistries": [],
                "networkConfiguration": {
                    "awsvpcConfiguration": {
                        "subnets": [SUBNET_1, SUBNET_2],
                        "securityGroups": [SECURITY_GROUP],
                        "assignPublicIp": "ENABLED",
                    }
                },
            }
        }
        stubber.add_response("delete_service", delete_svc_response, delete_svc_params)

        # 10b: DeregisterTaskDefinition
        dereg_params = {
            "taskDefinition": f"{TASK_FAMILY}:1",
        }
        dereg_response = {
            "taskDefinition": {
                "taskDefinitionArn": TASK_DEF_ARN,
                "family": TASK_FAMILY,
                "revision": 1,
                "status": "INACTIVE",
                "networkMode": "awsvpc",
                "containerDefinitions": [],
                "requiresCompatibilities": ["FARGATE"],
                "cpu": "256",
                "memory": "512",
                "compatibilities": ["EC2", "FARGATE"],
            }
        }
        stubber.add_response(
            "deregister_task_definition", dereg_response, dereg_params
        )

        # 10c: DeleteCluster
        delete_cluster_params = {
            "cluster": CLUSTER_NAME,
        }
        delete_cluster_response = {
            "cluster": {
                "clusterArn": CLUSTER_ARN,
                "clusterName": CLUSTER_NAME,
                "status": "INACTIVE",
                "registeredContainerInstancesCount": 0,
                "runningTasksCount": 0,
                "pendingTasksCount": 0,
                "activeServicesCount": 0,
                "settings": [],
                "statistics": [],
                "tags": [],
                "capacityProviders": [],
                "defaultCapacityProviderStrategy": [],
                "attachments": [],
            }
        }
        stubber.add_response(
            "delete_cluster", delete_cluster_response, delete_cluster_params
        )

        # Run cleanup operations
        svc = wrapper.delete_service(
            cluster=CLUSTER_NAME, service=SERVICE_NAME, force=True
        )
        assert svc["status"] == "DRAINING"

        task_def = wrapper.deregister_task_definition(f"{TASK_FAMILY}:1")
        assert task_def["status"] == "INACTIVE"

        cluster = wrapper.delete_cluster(CLUSTER_NAME)
        assert cluster["status"] == "INACTIVE"

        stubber.assert_no_pending_responses()

    # ---- 11: Error handling — CreateCluster InvalidParameterException ----
    def test_11_create_cluster_error(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        stubber.add_client_error(
            "create_cluster",
            service_error_code="InvalidParameterException",
            service_message="Cluster name is not valid.",
            expected_params={
                "clusterName": "bad!!cluster",
                "settings": [{"name": "containerInsights", "value": "enabled"}],
            },
        )
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_cluster("bad!!cluster")
        assert exc_info.value.response["Error"]["Code"] == "InvalidParameterException"

    # ---- 12: Error handling — DescribeClusters ClusterNotFoundException ----
    def test_12_describe_clusters_error(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        stubber.add_client_error(
            "describe_clusters",
            service_error_code="ClusterNotFoundException",
            service_message="Cluster not found.",
            expected_params={
                "clusters": ["nonexistent-cluster"],
                "include": ["STATISTICS"],
            },
        )
        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_clusters(["nonexistent-cluster"])
        assert exc_info.value.response["Error"]["Code"] == "ClusterNotFoundException"

    # ---- 13: Error handling — UpdateService ServiceNotFoundException ----
    def test_13_update_service_error(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        stubber.add_client_error(
            "update_service",
            service_error_code="ServiceNotFoundException",
            service_message="Service not found.",
            expected_params={
                "cluster": CLUSTER_NAME,
                "service": "nonexistent-service",
                "desiredCount": 1,
            },
        )
        with pytest.raises(ClientError) as exc_info:
            wrapper.update_service(
                cluster=CLUSTER_NAME,
                service="nonexistent-service",
                desired_count=1,
            )
        assert exc_info.value.response["Error"]["Code"] == "ServiceNotFoundException"

    # ---- 14: Error handling — DeleteCluster ClusterContainsServicesException ----
    def test_14_delete_cluster_error(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        stubber.add_client_error(
            "delete_cluster",
            service_error_code="ClusterContainsServicesException",
            service_message="Cluster still has active services.",
            expected_params={
                "cluster": CLUSTER_NAME,
            },
        )
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_cluster(CLUSTER_NAME)
        assert (
            exc_info.value.response["Error"]["Code"]
            == "ClusterContainsServicesException"
        )

    # ---- 15: ListTasks with no service filter ----
    def test_15_list_tasks_no_filter(self, ecs_stubber):
        wrapper, stubber = ecs_stubber
        expected_params = {
            "cluster": CLUSTER_NAME,
        }
        response = {
            "taskArns": [TASK_ARN],
        }
        stubber.add_response("list_tasks", response, expected_params)
        result = wrapper.list_tasks(cluster=CLUSTER_NAME)
        assert len(result) == 1
        assert result[0] == TASK_ARN
        stubber.assert_no_pending_responses()

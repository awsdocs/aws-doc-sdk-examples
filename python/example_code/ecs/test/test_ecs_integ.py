# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for Amazon ECS wrapper using botocore Stubber.
Tests run offline without AWS credentials.
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

import pytest
import boto3
from botocore.stub import Stubber, ANY
from botocore.exceptions import ClientError

from ecs_wrapper import EcsWrapper


# --- Success Tests ---


def test_create_cluster():
    """Test successful cluster creation."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)

    expected_params = {
        "clusterName": "test-cluster",
        "settings": [{"name": "containerInsights", "value": "enabled"}],
    }
    response = {
        "cluster": {
            "clusterArn": "arn:aws:ecs:us-east-1:123456789012:cluster/test-cluster",
            "clusterName": "test-cluster",
            "status": "ACTIVE",
            "registeredContainerInstancesCount": 0,
            "runningTasksCount": 0,
            "pendingTasksCount": 0,
            "activeServicesCount": 0,
            "settings": [{"name": "containerInsights", "value": "enabled"}],
        }
    }
    stubber.add_response("create_cluster", response, expected_params)
    stubber.activate()

    result = wrapper.create_cluster("test-cluster")
    assert result["clusterArn"] == "arn:aws:ecs:us-east-1:123456789012:cluster/test-cluster"
    assert result["status"] == "ACTIVE"

    stubber.deactivate()


def test_register_task_definition():
    """Test successful task definition registration."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)

    expected_params = {
        "family": "test-task-def",
        "networkMode": "awsvpc",
        "requiresCompatibilities": ["FARGATE"],
        "cpu": "256",
        "memory": "512",
        "executionRoleArn": "arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
        "containerDefinitions": [
            {
                "name": "web-container",
                "image": "public.ecr.aws/docker/library/httpd:latest",
                "essential": True,
                "portMappings": [{"containerPort": 80, "protocol": "tcp"}],
            }
        ],
    }
    response = {
        "taskDefinition": {
            "taskDefinitionArn": "arn:aws:ecs:us-east-1:123456789012:task-definition/test-task-def:1",
            "family": "test-task-def",
            "revision": 1,
            "status": "ACTIVE",
            "networkMode": "awsvpc",
            "cpu": "256",
            "memory": "512",
            "containerDefinitions": [
                {
                    "name": "web-container",
                    "image": "public.ecr.aws/docker/library/httpd:latest",
                    "essential": True,
                    "portMappings": [{"containerPort": 80, "protocol": "tcp"}],
                    "cpu": 0,
                    "memory": 0,
                }
            ],
            "requiresCompatibilities": ["FARGATE"],
            "executionRoleArn": "arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
        }
    }
    stubber.add_response("register_task_definition", response, expected_params)
    stubber.activate()

    result = wrapper.register_task_definition(
        family="test-task-def",
        execution_role_arn="arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
        container_name="web-container",
        container_image="public.ecr.aws/docker/library/httpd:latest",
        cpu="256",
        memory="512",
        container_port=80,
    )
    assert result["taskDefinitionArn"] == "arn:aws:ecs:us-east-1:123456789012:task-definition/test-task-def:1"
    assert result["revision"] == 1

    stubber.deactivate()


def test_describe_task_definition():
    """Test successful task definition describe."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)

    expected_params = {"taskDefinition": "test-task-def:1"}
    response = {
        "taskDefinition": {
            "taskDefinitionArn": "arn:aws:ecs:us-east-1:123456789012:task-definition/test-task-def:1",
            "family": "test-task-def",
            "revision": 1,
            "status": "ACTIVE",
            "networkMode": "awsvpc",
            "cpu": "256",
            "memory": "512",
            "containerDefinitions": [
                {
                    "name": "web-container",
                    "image": "public.ecr.aws/docker/library/httpd:latest",
                    "essential": True,
                    "portMappings": [{"containerPort": 80, "protocol": "tcp"}],
                    "cpu": 0,
                    "memory": 0,
                }
            ],
            "requiresCompatibilities": ["FARGATE"],
        }
    }
    stubber.add_response("describe_task_definition", response, expected_params)
    stubber.activate()

    result = wrapper.describe_task_definition("test-task-def:1")
    assert result["family"] == "test-task-def"
    assert result["status"] == "ACTIVE"
    assert result["cpu"] == "256"

    stubber.deactivate()


def test_create_service():
    """Test successful service creation."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)

    expected_params = {
        "cluster": "test-cluster",
        "serviceName": "test-service",
        "taskDefinition": "test-task-def:1",
        "desiredCount": 1,
        "launchType": "FARGATE",
        "networkConfiguration": {
            "awsvpcConfiguration": {
                "subnets": ["subnet-111", "subnet-222"],
                "securityGroups": ["sg-123"],
                "assignPublicIp": "ENABLED",
            }
        },
        "deploymentConfiguration": {
            "deploymentCircuitBreaker": {"enable": True, "rollback": True}
        },
    }
    response = {
        "service": {
            "serviceArn": "arn:aws:ecs:us-east-1:123456789012:service/test-cluster/test-service",
            "serviceName": "test-service",
            "clusterArn": "arn:aws:ecs:us-east-1:123456789012:cluster/test-cluster",
            "status": "ACTIVE",
            "desiredCount": 1,
            "runningCount": 0,
            "taskDefinition": "arn:aws:ecs:us-east-1:123456789012:task-definition/test-task-def:1",
            "launchType": "FARGATE",
        }
    }
    stubber.add_response("create_service", response, expected_params)
    stubber.activate()

    result = wrapper.create_service(
        cluster_name="test-cluster",
        service_name="test-service",
        task_definition="test-task-def:1",
        desired_count=1,
        subnets=["subnet-111", "subnet-222"],
        security_groups=["sg-123"],
        assign_public_ip=True,
    )
    assert result["serviceArn"] == "arn:aws:ecs:us-east-1:123456789012:service/test-cluster/test-service"
    assert result["status"] == "ACTIVE"

    stubber.deactivate()


def test_describe_services():
    """Test successful service describe."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)

    expected_params = {
        "cluster": "test-cluster",
        "services": ["test-service"],
    }
    response = {
        "services": [
            {
                "serviceArn": "arn:aws:ecs:us-east-1:123456789012:service/test-cluster/test-service",
                "serviceName": "test-service",
                "clusterArn": "arn:aws:ecs:us-east-1:123456789012:cluster/test-cluster",
                "status": "ACTIVE",
                "desiredCount": 1,
                "runningCount": 1,
                "taskDefinition": "arn:aws:ecs:us-east-1:123456789012:task-definition/test-task-def:1",
                "launchType": "FARGATE",
            }
        ],
        "failures": [],
    }
    stubber.add_response("describe_services", response, expected_params)
    stubber.activate()

    result = wrapper.describe_services("test-cluster", ["test-service"])
    assert len(result) == 1
    assert result[0]["runningCount"] == 1
    assert result[0]["status"] == "ACTIVE"

    stubber.deactivate()


def test_list_tasks():
    """Test successful task listing with paginator."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)

    expected_params = {
        "cluster": "test-cluster",
        "serviceName": "test-service",
        "desiredStatus": "RUNNING",
    }
    response = {
        "taskArns": [
            "arn:aws:ecs:us-east-1:123456789012:task/test-cluster/task-id-1"
        ],
    }
    stubber.add_response("list_tasks", response, expected_params)
    stubber.activate()

    result = wrapper.list_tasks("test-cluster", "test-service")
    assert len(result) == 1
    assert "task-id-1" in result[0]

    stubber.deactivate()


def test_describe_tasks():
    """Test successful task describe."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)

    task_arn = "arn:aws:ecs:us-east-1:123456789012:task/test-cluster/task-id-1"
    expected_params = {
        "cluster": "test-cluster",
        "tasks": [task_arn],
    }
    response = {
        "tasks": [
            {
                "taskArn": task_arn,
                "clusterArn": "arn:aws:ecs:us-east-1:123456789012:cluster/test-cluster",
                "lastStatus": "RUNNING",
                "desiredStatus": "RUNNING",
                "cpu": "256",
                "memory": "512",
                "containers": [
                    {
                        "containerArn": "arn:aws:ecs:us-east-1:123456789012:container/container-id-1",
                        "name": "web-container",
                        "lastStatus": "RUNNING",
                        "networkInterfaces": [
                            {
                                "attachmentId": "attach-id-1",
                                "privateIpv4Address": "10.0.1.100",
                            }
                        ],
                    }
                ],
                "attachments": [
                    {
                        "id": "attach-id-1",
                        "type": "ElasticNetworkInterface",
                        "status": "ATTACHED",
                        "details": [
                            {"name": "networkInterfaceId", "value": "eni-abc123"},
                            {"name": "privateIPv4Address", "value": "10.0.1.100"},
                        ],
                    }
                ],
            }
        ],
        "failures": [],
    }
    stubber.add_response("describe_tasks", response, expected_params)
    stubber.activate()

    result = wrapper.describe_tasks("test-cluster", [task_arn])
    assert len(result) == 1
    assert result[0]["lastStatus"] == "RUNNING"
    assert result[0]["cpu"] == "256"

    stubber.deactivate()


def test_update_service():
    """Test successful service update (scale up)."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)

    expected_params = {
        "cluster": "test-cluster",
        "service": "test-service",
        "desiredCount": 2,
    }
    response = {
        "service": {
            "serviceArn": "arn:aws:ecs:us-east-1:123456789012:service/test-cluster/test-service",
            "serviceName": "test-service",
            "clusterArn": "arn:aws:ecs:us-east-1:123456789012:cluster/test-cluster",
            "status": "ACTIVE",
            "desiredCount": 2,
            "runningCount": 1,
            "taskDefinition": "arn:aws:ecs:us-east-1:123456789012:task-definition/test-task-def:1",
            "launchType": "FARGATE",
        }
    }
    stubber.add_response("update_service", response, expected_params)
    stubber.activate()

    result = wrapper.update_service("test-cluster", "test-service", desired_count=2)
    assert result["desiredCount"] == 2

    stubber.deactivate()


def test_delete_service():
    """Test successful service deletion."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)

    expected_params = {
        "cluster": "test-cluster",
        "service": "test-service",
        "force": True,
    }
    response = {
        "service": {
            "serviceArn": "arn:aws:ecs:us-east-1:123456789012:service/test-cluster/test-service",
            "serviceName": "test-service",
            "clusterArn": "arn:aws:ecs:us-east-1:123456789012:cluster/test-cluster",
            "status": "DRAINING",
            "desiredCount": 0,
            "runningCount": 0,
            "taskDefinition": "arn:aws:ecs:us-east-1:123456789012:task-definition/test-task-def:1",
            "launchType": "FARGATE",
        }
    }
    stubber.add_response("delete_service", response, expected_params)
    stubber.activate()

    result = wrapper.delete_service("test-cluster", "test-service", force=True)
    assert result["status"] == "DRAINING"

    stubber.deactivate()


def test_delete_cluster():
    """Test successful cluster deletion."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)

    expected_params = {"cluster": "test-cluster"}
    response = {
        "cluster": {
            "clusterArn": "arn:aws:ecs:us-east-1:123456789012:cluster/test-cluster",
            "clusterName": "test-cluster",
            "status": "INACTIVE",
            "registeredContainerInstancesCount": 0,
            "runningTasksCount": 0,
            "pendingTasksCount": 0,
            "activeServicesCount": 0,
        }
    }
    stubber.add_response("delete_cluster", response, expected_params)
    stubber.activate()

    result = wrapper.delete_cluster("test-cluster")
    assert result["status"] == "INACTIVE"

    stubber.deactivate()


def test_list_clusters():
    """Test successful cluster listing."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)

    response = {
        "clusterArns": [
            "arn:aws:ecs:us-east-1:123456789012:cluster/cluster-1",
            "arn:aws:ecs:us-east-1:123456789012:cluster/cluster-2",
        ],
    }
    stubber.add_response("list_clusters", response)
    stubber.activate()

    result = wrapper.list_clusters()
    assert len(result) == 2
    assert "cluster-1" in result[0]

    stubber.deactivate()


# --- Error Tests ---


def test_create_cluster_client_exception():
    """Test cluster creation with ClientException."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)
    stubber.add_client_error(
        "create_cluster",
        service_error_code="ClientException",
        service_message="Cluster creation failed due to client-side error",
        expected_params={
            "clusterName": "bad-cluster",
            "settings": [{"name": "containerInsights", "value": "enabled"}],
        },
    )
    stubber.activate()

    with pytest.raises(ClientError) as exc_info:
        wrapper.create_cluster("bad-cluster")
    assert exc_info.value.response["Error"]["Code"] == "ClientException"

    stubber.deactivate()


def test_create_service_cluster_not_found():
    """Test service creation when cluster doesn't exist."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)
    stubber.add_client_error(
        "create_service",
        service_error_code="ClusterNotFoundException",
        service_message="Cluster not found",
        expected_params={
            "cluster": "nonexistent-cluster",
            "serviceName": "test-service",
            "taskDefinition": "test-task-def:1",
            "desiredCount": 1,
            "launchType": "FARGATE",
            "networkConfiguration": {
                "awsvpcConfiguration": {
                    "subnets": ["subnet-111"],
                    "securityGroups": ["sg-123"],
                    "assignPublicIp": "ENABLED",
                }
            },
            "deploymentConfiguration": {
                "deploymentCircuitBreaker": {"enable": True, "rollback": True}
            },
        },
    )
    stubber.activate()

    with pytest.raises(ClientError) as exc_info:
        wrapper.create_service(
            cluster_name="nonexistent-cluster",
            service_name="test-service",
            task_definition="test-task-def:1",
            desired_count=1,
            subnets=["subnet-111"],
            security_groups=["sg-123"],
        )
    assert exc_info.value.response["Error"]["Code"] == "ClusterNotFoundException"

    stubber.deactivate()


def test_describe_services_cluster_not_found():
    """Test describe services when cluster doesn't exist."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)
    stubber.add_client_error(
        "describe_services",
        service_error_code="ClusterNotFoundException",
        service_message="Cluster not found",
        expected_params={
            "cluster": "nonexistent-cluster",
            "services": ["test-service"],
        },
    )
    stubber.activate()

    with pytest.raises(ClientError) as exc_info:
        wrapper.describe_services("nonexistent-cluster", ["test-service"])
    assert exc_info.value.response["Error"]["Code"] == "ClusterNotFoundException"

    stubber.deactivate()


def test_update_service_service_not_found():
    """Test update service when service doesn't exist."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)
    stubber.add_client_error(
        "update_service",
        service_error_code="ServiceNotFoundException",
        service_message="Service not found",
        expected_params={
            "cluster": "test-cluster",
            "service": "nonexistent-service",
            "desiredCount": 2,
        },
    )
    stubber.activate()

    with pytest.raises(ClientError) as exc_info:
        wrapper.update_service("test-cluster", "nonexistent-service", desired_count=2)
    assert exc_info.value.response["Error"]["Code"] == "ServiceNotFoundException"

    stubber.deactivate()


def test_delete_cluster_has_active_services():
    """Test delete cluster when it has active services."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)
    stubber.add_client_error(
        "delete_cluster",
        service_error_code="ClusterContainsServicesException",
        service_message="Cluster has active services",
        expected_params={
            "cluster": "test-cluster",
        },
    )
    stubber.activate()

    with pytest.raises(ClientError) as exc_info:
        wrapper.delete_cluster("test-cluster")
    assert exc_info.value.response["Error"]["Code"] == "ClusterContainsServicesException"

    stubber.deactivate()


def test_list_tasks_service_not_found():
    """Test list tasks when service doesn't exist."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)
    stubber.add_client_error(
        "list_tasks",
        service_error_code="ServiceNotFoundException",
        service_message="Service not found",
        expected_params={
            "cluster": "test-cluster",
            "serviceName": "nonexistent-service",
            "desiredStatus": "RUNNING",
        },
    )
    stubber.activate()

    with pytest.raises(ClientError) as exc_info:
        wrapper.list_tasks("test-cluster", "nonexistent-service")
    assert exc_info.value.response["Error"]["Code"] == "ServiceNotFoundException"

    stubber.deactivate()


def test_describe_tasks_cluster_not_found():
    """Test describe tasks when cluster doesn't exist."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)
    stubber.add_client_error(
        "describe_tasks",
        service_error_code="ClusterNotFoundException",
        service_message="Cluster not found",
        expected_params={
            "cluster": "nonexistent-cluster",
            "tasks": ["arn:aws:ecs:us-east-1:123456789012:task/task-id-1"],
        },
    )
    stubber.activate()

    with pytest.raises(ClientError) as exc_info:
        wrapper.describe_tasks(
            "nonexistent-cluster",
            ["arn:aws:ecs:us-east-1:123456789012:task/task-id-1"],
        )
    assert exc_info.value.response["Error"]["Code"] == "ClusterNotFoundException"

    stubber.deactivate()


def test_delete_service_not_found():
    """Test delete service when service doesn't exist."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)
    stubber.add_client_error(
        "delete_service",
        service_error_code="ServiceNotFoundException",
        service_message="Service not found",
        expected_params={
            "cluster": "test-cluster",
            "service": "nonexistent-service",
            "force": True,
        },
    )
    stubber.activate()

    with pytest.raises(ClientError) as exc_info:
        wrapper.delete_service("test-cluster", "nonexistent-service", force=True)
    assert exc_info.value.response["Error"]["Code"] == "ServiceNotFoundException"

    stubber.deactivate()


def test_register_task_definition_invalid_param():
    """Test register task definition with InvalidParameterException."""
    client = boto3.client("ecs", region_name="us-east-1")
    wrapper = EcsWrapper(client)
    stubber = Stubber(client)
    stubber.add_client_error(
        "register_task_definition",
        service_error_code="InvalidParameterException",
        service_message="Invalid parameter: cpu must be a valid value",
        expected_params={
            "family": "bad-task-def",
            "networkMode": "awsvpc",
            "requiresCompatibilities": ["FARGATE"],
            "cpu": "999",
            "memory": "512",
            "executionRoleArn": "arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
            "containerDefinitions": [
                {
                    "name": "web-container",
                    "image": "public.ecr.aws/docker/library/httpd:latest",
                    "essential": True,
                    "portMappings": [{"containerPort": 80, "protocol": "tcp"}],
                }
            ],
        },
    )
    stubber.activate()

    with pytest.raises(ClientError) as exc_info:
        wrapper.register_task_definition(
            family="bad-task-def",
            execution_role_arn="arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
            container_name="web-container",
            container_image="public.ecr.aws/docker/library/httpd:latest",
            cpu="999",
            memory="512",
            container_port=80,
        )
    assert exc_info.value.response["Error"]["Code"] == "InvalidParameterException"

    stubber.deactivate()


# --- CloudFormation Stub Tests ---


def test_create_stack():
    """Test CloudFormation stack creation stub."""
    cfn_client = boto3.client("cloudformation", region_name="us-east-1")
    stubber = Stubber(cfn_client)

    expected_params = {
        "StackName": "ecs-basics-stack-123",
        "TemplateBody": "template-body",
        "Capabilities": ["CAPABILITY_IAM"],
    }
    response = {
        "StackId": "arn:aws:cloudformation:us-east-1:123456789012:stack/ecs-basics-stack-123/guid"
    }
    stubber.add_response("create_stack", response, expected_params)
    stubber.activate()

    result = cfn_client.create_stack(
        StackName="ecs-basics-stack-123",
        TemplateBody="template-body",
        Capabilities=["CAPABILITY_IAM"],
    )
    assert "StackId" in result

    stubber.deactivate()


def test_delete_stack():
    """Test CloudFormation stack deletion stub."""
    cfn_client = boto3.client("cloudformation", region_name="us-east-1")
    stubber = Stubber(cfn_client)

    expected_params = {"StackName": "ecs-basics-stack-123"}
    response = dict()
    stubber.add_response("delete_stack", response, expected_params)
    stubber.activate()

    result = cfn_client.delete_stack(StackName="ecs-basics-stack-123")
    assert result is not None

    stubber.deactivate()

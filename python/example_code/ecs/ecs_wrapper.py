# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon ECS wrapper class that encapsulates Amazon Elastic Container Service operations.
"""

import logging
from typing import Any, Dict, List, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ecs.EcsWrapper.decl]
class EcsWrapper:
    """Encapsulates Amazon ECS operations."""

    def __init__(self, ecs_client: boto3.client):
        """
        Initializes the EcsWrapper with an ECS client.

        :param ecs_client: A Boto3 ECS client.
        """
        self.ecs_client = ecs_client

    @classmethod
    def from_client(cls):
        """
        Creates an EcsWrapper instance from a default Boto3 ECS client.
        """
        ecs_client = boto3.client("ecs")
        return cls(ecs_client)

    # snippet-end:[python.example_code.ecs.EcsWrapper.decl]

    # snippet-start:[python.example_code.ecs.CreateCluster]
    def create_cluster(self, cluster_name: str) -> Dict[str, Any]:
        """
        Creates an ECS cluster with Container Insights enabled.

        :param cluster_name: The name of the cluster to create.
        :return: A dictionary containing cluster information.
        :raises ClientError: If a client-side error occurs during cluster creation.
        """
        try:
            response = self.ecs_client.create_cluster(
                clusterName=cluster_name,
                settings=[
                    {"name": "containerInsights", "value": "enabled"}
                ],
            )
            cluster = response["cluster"]
            logger.info("Created cluster '%s' with ARN: %s", cluster_name, cluster["clusterArn"])
            return cluster
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClientException":
                logger.error(
                    "Client error creating cluster '%s': %s",
                    cluster_name,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.ecs.CreateCluster]

    # snippet-start:[python.example_code.ecs.RegisterTaskDefinition]
    def register_task_definition(
        self,
        family: str,
        execution_role_arn: str,
        container_name: str,
        container_image: str,
        cpu: str = "256",
        memory: str = "512",
        container_port: int = 80,
    ) -> Dict[str, Any]:
        """
        Registers a Fargate-compatible task definition.

        :param family: The family name for the task definition.
        :param execution_role_arn: The ARN of the task execution role.
        :param container_name: The name of the container.
        :param container_image: The container image to use.
        :param cpu: The CPU units for the task (default '256').
        :param memory: The memory (MB) for the task (default '512').
        :param container_port: The port to expose (default 80).
        :return: A dictionary containing the task definition information.
        :raises ClientError: If parameters are invalid.
        """
        try:
            response = self.ecs_client.register_task_definition(
                family=family,
                networkMode="awsvpc",
                requiresCompatibilities=["FARGATE"],
                cpu=cpu,
                memory=memory,
                executionRoleArn=execution_role_arn,
                containerDefinitions=[
                    {
                        "name": container_name,
                        "image": container_image,
                        "essential": True,
                        "portMappings": [
                            {
                                "containerPort": container_port,
                                "protocol": "tcp",
                            }
                        ],
                    }
                ],
            )
            task_def = response["taskDefinition"]
            logger.info(
                "Registered task definition '%s' revision %s",
                task_def["family"],
                task_def["revision"],
            )
            return task_def
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidParameterException":
                logger.error(
                    "Invalid parameter for task definition '%s': %s",
                    family,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.ecs.RegisterTaskDefinition]

    # snippet-start:[python.example_code.ecs.DescribeTaskDefinition]
    def describe_task_definition(self, task_definition: str) -> Dict[str, Any]:
        """
        Describes a task definition.

        :param task_definition: The task definition family:revision or full ARN.
        :return: A dictionary containing the task definition details.
        :raises ClientError: If the task definition is invalid.
        """
        try:
            response = self.ecs_client.describe_task_definition(
                taskDefinition=task_definition
            )
            task_def = response["taskDefinition"]
            logger.info(
                "Described task definition '%s' revision %s, status: %s",
                task_def["family"],
                task_def["revision"],
                task_def["status"],
            )
            return task_def
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidParameterException":
                logger.error(
                    "Invalid parameter describing task definition '%s': %s",
                    task_definition,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.ecs.DescribeTaskDefinition]

    # snippet-start:[python.example_code.ecs.CreateService]
    def create_service(
        self,
        cluster_name: str,
        service_name: str,
        task_definition: str,
        desired_count: int,
        subnets: List[str],
        security_groups: List[str],
        assign_public_ip: bool = True,
    ) -> Dict[str, Any]:
        """
        Creates an ECS service using Fargate launch type.

        :param cluster_name: The name of the cluster.
        :param service_name: The name of the service.
        :param task_definition: The task definition (family:revision).
        :param desired_count: The number of tasks to run.
        :param subnets: List of subnet IDs for the service.
        :param security_groups: List of security group IDs.
        :param assign_public_ip: Whether to assign a public IP (default True).
        :return: A dictionary containing the service information.
        :raises ClientError: If the cluster is not found.
        """
        try:
            response = self.ecs_client.create_service(
                cluster=cluster_name,
                serviceName=service_name,
                taskDefinition=task_definition,
                desiredCount=desired_count,
                launchType="FARGATE",
                networkConfiguration={
                    "awsvpcConfiguration": {
                        "subnets": subnets,
                        "securityGroups": security_groups,
                        "assignPublicIp": "ENABLED" if assign_public_ip else "DISABLED",
                    }
                },
                deploymentConfiguration={
                    "deploymentCircuitBreaker": {
                        "enable": True,
                        "rollback": True,
                    }
                },
            )
            service = response["service"]
            logger.info(
                "Created service '%s' with ARN: %s",
                service_name,
                service["serviceArn"],
            )
            return service
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClusterNotFoundException":
                logger.error(
                    "Cluster '%s' not found. Verify the cluster was created successfully.",
                    cluster_name,
                )
            raise
    # snippet-end:[python.example_code.ecs.CreateService]

    # snippet-start:[python.example_code.ecs.DescribeServices]
    def describe_services(
        self, cluster_name: str, service_names: List[str]
    ) -> List[Dict[str, Any]]:
        """
        Describes one or more ECS services.

        :param cluster_name: The cluster name.
        :param service_names: List of service names to describe.
        :return: A list of service dictionaries.
        :raises ClientError: If the cluster is not found.
        """
        try:
            response = self.ecs_client.describe_services(
                cluster=cluster_name,
                services=service_names,
            )
            services = response.get("services", list())
            for svc in services:
                logger.info(
                    "Service '%s': status=%s, running=%d, desired=%d",
                    svc["serviceName"],
                    svc["status"],
                    svc["runningCount"],
                    svc["desiredCount"],
                )
            return services
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClusterNotFoundException":
                logger.error(
                    "Cluster '%s' not found. It may have been deleted.",
                    cluster_name,
                )
            raise
    # snippet-end:[python.example_code.ecs.DescribeServices]

    # snippet-start:[python.example_code.ecs.ListTasks]
    def list_tasks(
        self, cluster_name: str, service_name: str, desired_status: str = "RUNNING"
    ) -> List[str]:
        """
        Lists tasks for a service using pagination.

        :param cluster_name: The cluster name.
        :param service_name: The service name.
        :param desired_status: The task status filter (default 'RUNNING').
        :return: A list of task ARNs.
        :raises ClientError: If the service is not found.
        """
        try:
            task_arns = list()
            paginator = self.ecs_client.get_paginator("list_tasks")
            for page in paginator.paginate(
                cluster=cluster_name,
                serviceName=service_name,
                desiredStatus=desired_status,
            ):
                task_arns.extend(page.get("taskArns", list()))
            logger.info(
                "Found %d tasks for service '%s' in cluster '%s'",
                len(task_arns),
                service_name,
                cluster_name,
            )
            return task_arns
        except ClientError as err:
            if err.response["Error"]["Code"] == "ServiceNotFoundException":
                logger.error(
                    "Service '%s' not found in cluster '%s'.",
                    service_name,
                    cluster_name,
                )
            raise
    # snippet-end:[python.example_code.ecs.ListTasks]

    # snippet-start:[python.example_code.ecs.DescribeTasks]
    def describe_tasks(
        self, cluster_name: str, task_arns: List[str]
    ) -> List[Dict[str, Any]]:
        """
        Describes specified tasks in a cluster.

        :param cluster_name: The cluster name.
        :param task_arns: List of task ARNs to describe.
        :return: A list of task detail dictionaries.
        :raises ClientError: If the cluster is not found.
        """
        try:
            response = self.ecs_client.describe_tasks(
                cluster=cluster_name,
                tasks=task_arns,
            )
            tasks = response.get("tasks", list())
            for task in tasks:
                logger.info(
                    "Task '%s': lastStatus=%s, cpu=%s, memory=%s",
                    task["taskArn"],
                    task["lastStatus"],
                    task.get("cpu", "N/A"),
                    task.get("memory", "N/A"),
                )
            return tasks
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClusterNotFoundException":
                logger.error(
                    "Cluster '%s' not found. Verify the cluster name/ARN is correct.",
                    cluster_name,
                )
            raise
    # snippet-end:[python.example_code.ecs.DescribeTasks]

    # snippet-start:[python.example_code.ecs.UpdateService]
    def update_service(
        self, cluster_name: str, service_name: str, desired_count: int
    ) -> Dict[str, Any]:
        """
        Updates an ECS service (e.g., to scale up or down).

        :param cluster_name: The cluster name.
        :param service_name: The service name.
        :param desired_count: The new desired task count.
        :return: A dictionary containing the updated service information.
        :raises ClientError: If the service is not found.
        """
        try:
            response = self.ecs_client.update_service(
                cluster=cluster_name,
                service=service_name,
                desiredCount=desired_count,
            )
            service = response["service"]
            logger.info(
                "Updated service '%s' desired count to %d",
                service_name,
                desired_count,
            )
            return service
        except ClientError as err:
            if err.response["Error"]["Code"] == "ServiceNotFoundException":
                logger.error(
                    "Service '%s' not found. It may have been deleted or is inactive.",
                    service_name,
                )
            raise
    # snippet-end:[python.example_code.ecs.UpdateService]

    # snippet-start:[python.example_code.ecs.DeleteService]
    def delete_service(
        self, cluster_name: str, service_name: str, force: bool = True
    ) -> Dict[str, Any]:
        """
        Deletes an ECS service.

        :param cluster_name: The cluster name.
        :param service_name: The service name to delete.
        :param force: Whether to force deletion (default True).
        :return: A dictionary containing the deleted service information.
        :raises ClientError: If the service is not found.
        """
        try:
            response = self.ecs_client.delete_service(
                cluster=cluster_name,
                service=service_name,
                force=force,
            )
            service = response["service"]
            logger.info("Deleted service '%s' from cluster '%s'", service_name, cluster_name)
            return service
        except ClientError as err:
            if err.response["Error"]["Code"] == "ServiceNotFoundException":
                logger.error(
                    "Service '%s' is already deleted. Proceeding with cleanup.",
                    service_name,
                )
            raise
    # snippet-end:[python.example_code.ecs.DeleteService]

    # snippet-start:[python.example_code.ecs.DeleteCluster]
    def delete_cluster(self, cluster_name: str) -> Dict[str, Any]:
        """
        Deletes an ECS cluster.

        :param cluster_name: The name of the cluster to delete.
        :return: A dictionary containing the deleted cluster information.
        :raises ClientError: If the cluster contains active services.
        """
        try:
            response = self.ecs_client.delete_cluster(cluster=cluster_name)
            cluster = response["cluster"]
            logger.info("Deleted cluster '%s'", cluster_name)
            return cluster
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClusterContainsServicesException":
                logger.error(
                    "Cluster '%s' still has active services. "
                    "Force-delete services before retrying cluster deletion.",
                    cluster_name,
                )
            raise
    # snippet-end:[python.example_code.ecs.DeleteCluster]

    # snippet-start:[python.example_code.ecs.ListClusters]
    def list_clusters(self) -> List[str]:
        """
        Lists all ECS clusters using pagination.

        :return: A list of cluster ARNs.
        """
        try:
            cluster_arns = list()
            paginator = self.ecs_client.get_paginator("list_clusters")
            for page in paginator.paginate():
                cluster_arns.extend(page.get("clusterArns", list()))
            logger.info("Found %d clusters", len(cluster_arns))
            return cluster_arns
        except ClientError as err:
            logger.error("Error listing clusters: %s", err.response["Error"]["Message"])
            raise
    # snippet-end:[python.example_code.ecs.ListClusters]

    def wait_for_service_stable(self, cluster_name: str, service_name: str) -> None:
        """
        Waits for a service to reach a stable state.

        :param cluster_name: The cluster name.
        :param service_name: The service name.
        """
        logger.info("Waiting for service '%s' to stabilize...", service_name)
        waiter = self.ecs_client.get_waiter("services_stable")
        waiter.wait(
            cluster=cluster_name,
            services=[service_name],
            WaiterConfig={"Delay": 15, "MaxAttempts": 40},
        )
        logger.info("Service '%s' is stable.", service_name)

    def wait_for_service_inactive(self, cluster_name: str, service_name: str) -> None:
        """
        Waits for a service to become inactive.

        :param cluster_name: The cluster name.
        :param service_name: The service name.
        """
        logger.info("Waiting for service '%s' to become inactive...", service_name)
        waiter = self.ecs_client.get_waiter("services_inactive")
        waiter.wait(
            cluster=cluster_name,
            services=[service_name],
            WaiterConfig={"Delay": 15, "MaxAttempts": 40},
        )
        logger.info("Service '%s' is now inactive.", service_name)

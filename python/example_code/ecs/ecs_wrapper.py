# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon ECS wrapper class that encapsulates Amazon Elastic Container Service operations.
"""

import logging
from typing import Any, Dict, List, Optional

import boto3
from botocore.client import BaseClient
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ecs.EcsWrapper.complete]
# snippet-start:[python.example_code.ecs.EcsWrapper.decl]
class EcsWrapper:
    """Encapsulates Amazon ECS operations."""

    def __init__(self, ecs_client: BaseClient):
        """
        Initializes the EcsWrapper with an ECS client.

        :param ecs_client: A Boto3 Amazon ECS client. Boto3 clients are created
            by the ``boto3.client`` factory function and are instances of
            ``botocore.client.BaseClient``, which is the correct type to
            annotate here (``boto3.client`` itself is a function, not a type).
        """
        self.ecs_client = ecs_client

    @classmethod
    def from_client(cls) -> "EcsWrapper":
        """Creates an EcsWrapper using a default Boto3 ECS client."""
        ecs_client = boto3.client("ecs")
        return cls(ecs_client)

    # snippet-end:[python.example_code.ecs.EcsWrapper.decl]

    # snippet-start:[python.example_code.ecs.CreateCluster]
    def create_cluster(self, cluster_name: str) -> Dict[str, Any]:
        """
        Creates a new Amazon ECS cluster.

        :param cluster_name: The name of the cluster to create.
        :return: The cluster details from the response.
        :raises ClientError: If the request fails (e.g., InvalidParameterException).
        """
        try:
            response = self.ecs_client.create_cluster(
                clusterName=cluster_name,
                settings=[
                    {"name": "containerInsights", "value": "enabled"},
                ],
            )
            cluster = response["cluster"]
            logger.info(
                "Created cluster '%s' with ARN: %s",
                cluster["clusterName"],
                cluster["clusterArn"],
            )
            return cluster
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidParameterException":
                logger.error(
                    "Invalid parameter when creating cluster '%s': %s",
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
        container_definitions: Optional[List[Dict[str, Any]]] = None,
        cpu: str = "256",
        memory: str = "512",
    ) -> Dict[str, Any]:
        """
        Registers a new task definition for Fargate.

        :param family: The family name for the task definition.
        :param execution_role_arn: The ARN of the task execution IAM role.
        :param container_definitions: List of container definitions. If None, a
            default httpd container is used.
        :param cpu: The task-level CPU units (e.g., '256').
        :param memory: The task-level memory in MiB (e.g., '512').
        :return: The task definition details from the response.
        :raises ClientError: If the request fails (e.g., InvalidParameterException).
        """
        if container_definitions is None:
            container_definitions = [
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
            ]
        try:
            response = self.ecs_client.register_task_definition(
                family=family,
                networkMode="awsvpc",
                requiresCompatibilities=["FARGATE"],
                cpu=cpu,
                memory=memory,
                executionRoleArn=execution_role_arn,
                containerDefinitions=container_definitions,
            )
            task_def = response["taskDefinition"]
            logger.info(
                "Registered task definition '%s' revision %s, ARN: %s",
                task_def["family"],
                task_def["revision"],
                task_def["taskDefinitionArn"],
            )
            return task_def
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidParameterException":
                logger.error(
                    "Invalid parameter when registering task definition '%s': %s",
                    family,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.ecs.RegisterTaskDefinition]

    # snippet-start:[python.example_code.ecs.DescribeClusters]
    def describe_clusters(self, cluster_names: List[str]) -> List[Dict[str, Any]]:
        """
        Describes one or more ECS clusters.

        :param cluster_names: A list of cluster names or ARNs.
        :return: A list of cluster details.
        :raises ClientError: If the request fails (e.g., ClusterNotFoundException).
        """
        try:
            response = self.ecs_client.describe_clusters(
                clusters=cluster_names,
                include=["STATISTICS"],
            )
            clusters = response.get("clusters", list())
            for cluster in clusters:
                logger.info(
                    "Cluster '%s': status=%s, active_services=%d, running_tasks=%d",
                    cluster["clusterName"],
                    cluster["status"],
                    cluster["activeServicesCount"],
                    cluster["runningTasksCount"],
                )
            return clusters
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClusterNotFoundException":
                logger.error(
                    "Cluster(s) not found: %s. %s",
                    cluster_names,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.ecs.DescribeClusters]

    # snippet-start:[python.example_code.ecs.RunTask]
    def run_task(
        self,
        cluster: str,
        task_definition: str,
        subnets: List[str],
        security_groups: List[str],
        count: int = 1,
    ) -> List[Dict[str, Any]]:
        """
        Runs a standalone Fargate task.

        :param cluster: The cluster name or ARN.
        :param task_definition: The task definition family or family:revision.
        :param subnets: List of subnet IDs.
        :param security_groups: List of security group IDs.
        :param count: Number of tasks to run.
        :return: A list of task details.
        :raises ClientError: If the request fails (e.g., ClusterNotFoundException).
        """
        try:
            response = self.ecs_client.run_task(
                cluster=cluster,
                taskDefinition=task_definition,
                launchType="FARGATE",
                count=count,
                networkConfiguration={
                    "awsvpcConfiguration": {
                        "subnets": subnets,
                        "securityGroups": security_groups,
                        "assignPublicIp": "ENABLED",
                    }
                },
            )
            tasks = response.get("tasks", list())
            failures = response.get("failures", list())
            if failures:
                logger.warning("Task run failures: %s", failures)
            for task in tasks:
                logger.info(
                    "Started task '%s' with status '%s'",
                    task["taskArn"],
                    task["lastStatus"],
                )
            return tasks
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClusterNotFoundException":
                logger.error(
                    "Cluster '%s' not found: %s",
                    cluster,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.ecs.RunTask]

    # snippet-start:[python.example_code.ecs.DescribeTasks]
    def describe_tasks(
        self, cluster: str, task_arns: List[str]
    ) -> List[Dict[str, Any]]:
        """
        Describes the specified tasks.

        :param cluster: The cluster name or ARN.
        :param task_arns: A list of task ARNs.
        :return: A list of task details.
        :raises ClientError: If the request fails (e.g., ClusterNotFoundException).
        """
        try:
            response = self.ecs_client.describe_tasks(
                cluster=cluster,
                tasks=task_arns,
            )
            tasks = response.get("tasks", list())
            for task in tasks:
                logger.info(
                    "Task '%s': status=%s, desired_status=%s",
                    task["taskArn"],
                    task["lastStatus"],
                    task["desiredStatus"],
                )
            return tasks
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClusterNotFoundException":
                logger.error(
                    "Cluster '%s' not found when describing tasks: %s",
                    cluster,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.ecs.DescribeTasks]

    # snippet-start:[python.example_code.ecs.CreateService]
    def create_service(
        self,
        cluster: str,
        service_name: str,
        task_definition: str,
        desired_count: int,
        subnets: List[str],
        security_groups: List[str],
    ) -> Dict[str, Any]:
        """
        Creates a service that maintains a desired count of running tasks.

        :param cluster: The cluster name or ARN.
        :param service_name: The name for the service.
        :param task_definition: The task definition family or family:revision.
        :param desired_count: The number of task instances to maintain.
        :param subnets: List of subnet IDs.
        :param security_groups: List of security group IDs.
        :return: The service details from the response.
        :raises ClientError: If the request fails (e.g., InvalidParameterException).
        """
        try:
            response = self.ecs_client.create_service(
                cluster=cluster,
                serviceName=service_name,
                taskDefinition=task_definition,
                desiredCount=desired_count,
                launchType="FARGATE",
                networkConfiguration={
                    "awsvpcConfiguration": {
                        "subnets": subnets,
                        "securityGroups": security_groups,
                        "assignPublicIp": "ENABLED",
                    }
                },
            )
            service = response["service"]
            logger.info(
                "Created service '%s' (ARN: %s) with desired count %d",
                service["serviceName"],
                service["serviceArn"],
                service["desiredCount"],
            )
            return service
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidParameterException":
                logger.error(
                    "Invalid parameter when creating service '%s': %s",
                    service_name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.ecs.CreateService]

    # snippet-start:[python.example_code.ecs.ListTasks]
    def list_tasks(
        self,
        cluster: str,
        service_name: Optional[str] = None,
    ) -> List[str]:
        """
        Lists task ARNs for a cluster, optionally filtered by service.
        Uses paginator to handle large result sets.

        :param cluster: The cluster name or ARN.
        :param service_name: The service name to filter by (optional).
        :return: A list of task ARN strings.
        :raises ClientError: If the request fails (e.g., InvalidParameterException).
        """
        try:
            task_arns = list()
            paginator = self.ecs_client.get_paginator("list_tasks")
            params = dict()
            params["cluster"] = cluster
            if service_name is not None:
                params["serviceName"] = service_name
            for page in paginator.paginate(**params):
                task_arns.extend(page.get("taskArns", list()))
            logger.info(
                "Listed %d tasks for cluster '%s'%s",
                len(task_arns),
                cluster,
                f" (service='{service_name}')" if service_name else "",
            )
            return task_arns
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidParameterException":
                logger.error(
                    "Invalid parameter when listing tasks: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.ecs.ListTasks]

    # snippet-start:[python.example_code.ecs.DescribeServices]
    def describe_services(
        self, cluster: str, service_names: List[str]
    ) -> List[Dict[str, Any]]:
        """
        Describes the specified services running in a cluster.

        :param cluster: The cluster name or ARN.
        :param service_names: A list of service names or ARNs.
        :return: A list of service details.
        :raises ClientError: If the request fails (e.g., ClusterNotFoundException).
        """
        try:
            response = self.ecs_client.describe_services(
                cluster=cluster,
                services=service_names,
            )
            services = response.get("services", list())
            for svc in services:
                logger.info(
                    "Service '%s': status=%s, desired=%d, running=%d",
                    svc["serviceName"],
                    svc["status"],
                    svc["desiredCount"],
                    svc["runningCount"],
                )
            return services
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClusterNotFoundException":
                logger.error(
                    "Cluster '%s' not found when describing services: %s",
                    cluster,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.ecs.DescribeServices]

    # snippet-start:[python.example_code.ecs.UpdateService]
    def update_service(
        self,
        cluster: str,
        service: str,
        desired_count: int,
    ) -> Dict[str, Any]:
        """
        Updates the desired count for a service.

        :param cluster: The cluster name or ARN.
        :param service: The service name or ARN.
        :param desired_count: The new desired task count.
        :return: The updated service details.
        :raises ClientError: If the request fails (e.g., ServiceNotFoundException).
        """
        try:
            response = self.ecs_client.update_service(
                cluster=cluster,
                service=service,
                desiredCount=desired_count,
            )
            svc = response["service"]
            logger.info(
                "Updated service '%s' desired count to %d",
                svc["serviceName"],
                svc["desiredCount"],
            )
            return svc
        except ClientError as err:
            if err.response["Error"]["Code"] == "ServiceNotFoundException":
                logger.error(
                    "Service '%s' not found in cluster '%s': %s",
                    service,
                    cluster,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.ecs.UpdateService]

    # snippet-start:[python.example_code.ecs.DeleteService]
    def delete_service(
        self,
        cluster: str,
        service: str,
        force: bool = True,
    ) -> Dict[str, Any]:
        """
        Deletes a service from a cluster.

        :param cluster: The cluster name or ARN.
        :param service: The service name or ARN.
        :param force: If True, deletes the service even if it has active tasks.
        :return: The deleted service details.
        :raises ClientError: If the request fails (e.g., ServiceNotFoundException).
        """
        try:
            response = self.ecs_client.delete_service(
                cluster=cluster,
                service=service,
                force=force,
            )
            svc = response["service"]
            logger.info(
                "Deleted service '%s' from cluster '%s'",
                svc["serviceName"],
                cluster,
            )
            return svc
        except ClientError as err:
            if err.response["Error"]["Code"] == "ServiceNotFoundException":
                logger.error(
                    "Service '%s' not found (may already be deleted): %s",
                    service,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.ecs.DeleteService]

    # snippet-start:[python.example_code.ecs.DeregisterTaskDefinition]
    def deregister_task_definition(self, task_definition: str) -> Dict[str, Any]:
        """
        Deregisters a task definition.

        :param task_definition: The family:revision of the task definition.
        :return: The deregistered task definition details.
        :raises ClientError: If the request fails (e.g., InvalidParameterException).
        """
        try:
            response = self.ecs_client.deregister_task_definition(
                taskDefinition=task_definition,
            )
            task_def = response["taskDefinition"]
            logger.info(
                "Deregistered task definition '%s' (status: %s)",
                task_def["taskDefinitionArn"],
                task_def["status"],
            )
            return task_def
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidParameterException":
                logger.error(
                    "Invalid parameter when deregistering task definition '%s': %s",
                    task_definition,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.ecs.DeregisterTaskDefinition]

    # snippet-start:[python.example_code.ecs.DeleteCluster]
    def delete_cluster(self, cluster: str) -> Dict[str, Any]:
        """
        Deletes an ECS cluster.

        :param cluster: The cluster name or ARN.
        :return: The deleted cluster details.
        :raises ClientError: If the request fails (e.g., ClusterContainsServicesException).
        """
        try:
            response = self.ecs_client.delete_cluster(
                cluster=cluster,
            )
            cluster_info = response["cluster"]
            logger.info(
                "Deleted cluster '%s' (status: %s)",
                cluster_info["clusterName"],
                cluster_info["status"],
            )
            return cluster_info
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClusterContainsServicesException":
                logger.error(
                    "Cluster '%s' still contains services. Delete services first: %s",
                    cluster,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.ecs.DeleteCluster]


# snippet-end:[python.example_code.ecs.EcsWrapper.complete]

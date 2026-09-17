# Amazon ECS Specification

This is a draft specification for an Amazon Elastic Container Service (Amazon ECS) Basics scenario. This scenario demonstrates how to create and manage containerized workloads using AWS Fargate, including creating a cluster, registering task definitions, running tasks, creating services, and monitoring/cleaning up resources. The scenario runs entirely serverless on Fargate so no EC2 instances are required.

**IMPORTANT**: This scenario requires prerequisite AWS resources from other services (IAM role, VPC with subnets and security group). These are created via a CloudFormation stack in the setup phase and deleted during cleanup. The scenario is fully self-contained — no manual pre-configuration, no hardcoded ARNs, and no environment variables are required.

### Resources

The scenario deploys a CloudFormation stack containing:
- An IAM **task execution role** with the `AmazonECSTaskExecutionRolePolicy` managed policy attached. This grants Fargate the permissions to pull container images and send logs to CloudWatch.
- A **VPC** with two public subnets in different Availability Zones, an Internet Gateway, a route table with a default route, and a **security group** that allows outbound traffic. This networking infrastructure is required for Fargate tasks using `awsvpc` network mode.

Stack outputs used by the scenario:
- `TaskExecutionRoleArn` — The ARN of the task execution IAM role.
- `SubnetIdOne` — The first public subnet ID.
- `SubnetIdTwo` — The second public subnet ID.
- `SecurityGroupId` — The security group ID.

### Relevant documentation
- [What is Amazon ECS?](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/Welcome.html)
- [Amazon ECS API Reference](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/Welcome.html)
- [Amazon ECS Task Definitions](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_definitions.html)
- [Amazon ECS task execution IAM role](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_execution_IAM_role.html)
- [Task Networking in Amazon ECS](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task-networking.html)
- [Amazon ECS CloudFormation templates](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/working-with-templates.html)
- [Amazon ECS service event messages](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/service-event-messages.html)

### API Actions Used
- [CreateCluster](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_CreateCluster.html) — Creates a new ECS cluster.
- [RegisterTaskDefinition](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_RegisterTaskDefinition.html) — Registers a new task definition from a family and container definitions.
- [DescribeClusters](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_DescribeClusters.html) — Describes one or more clusters.
- [RunTask](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_RunTask.html) — Starts a new standalone task from a task definition.
- [DescribeTasks](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_DescribeTasks.html) — Describes a set of tasks.
- [CreateService](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_CreateService.html) — Runs and maintains a desired count of tasks from a task definition.
- [DescribeServices](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_DescribeServices.html) — Describes the specified services in a cluster.
- [ListTasks](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_ListTasks.html) — Returns a list of tasks for a cluster.
- [UpdateService](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_UpdateService.html) — Modifies the parameters of a service.
- [DeleteService](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_DeleteService.html) — Deletes a specified service within a cluster.
- [DeregisterTaskDefinition](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_DeregisterTaskDefinition.html) — Deregisters the specified task definition by family and revision.
- [DeleteCluster](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_DeleteCluster.html) — Deletes the specified cluster.

## Hello ECS

The Hello ECS example is a minimal, standalone runnable program that verifies connectivity to the Amazon ECS service.

- Create an ECS client.
- Call `ListClusters` to retrieve and display the ARNs of existing ECS clusters in the current region.
- If no clusters exist, display a message indicating no clusters were found.
- Handle errors gracefully (e.g., authentication failures).

**ListClusters Parameters:**
- `maxResults` (optional, integer): Maximum number of cluster results. Default is up to 100.
- `nextToken` (optional, string): Pagination token from a previous response.

**Response:**
- `clusterArns` — A list of cluster ARN strings.
- `nextToken` — Token for retrieving the next page of results.

## Scenario

This scenario walks through the complete lifecycle of running a containerized workload on Amazon ECS using AWS Fargate.

## Errors

| Action | Exception | Handling |
|-|-|-|
| `CreateCluster` | `InvalidParameterException` | Notify the user that the cluster name or configuration is invalid. |
| `RegisterTaskDefinition` | `InvalidParameterException` | Notify the user that the task definition parameters are invalid. |
| `DescribeClusters` | `ClusterNotFoundException` | Inform the user that the specified cluster was not found. |
| `RunTask` | `ClusterNotFoundException` | Inform the user that the cluster was not found. |
| `DescribeTasks` | `ClusterNotFoundException` | Inform the user that the cluster was not found. |
| `CreateService` | `InvalidParameterException` | Notify the user that service parameters are invalid. |
| `DescribeServices` | `ClusterNotFoundException` | Inform the user that the cluster was not found. |
| `ListTasks` | `InvalidParameterException` | Notify the user that the filter parameters are invalid. |
| `UpdateService` | `ServiceNotFoundException` | Inform the user that the service was not found in the cluster. |
| `DeleteService` | `ServiceNotFoundException` | Inform the user that the service does not exist or was already deleted. |
| `DeregisterTaskDefinition` | `InvalidParameterException` | Notify the user that the task definition identifier is invalid. |
| `DeleteCluster` | `ClusterContainsServicesException` | Notify the user that the cluster still has active services. |

## Metadata

| action / scenario | metadata file | metadata key |
|-|-|-|
| `ListClusters` | ecs_metadata.yaml | ecs_Hello |
| `CreateCluster` | ecs_metadata.yaml | ecs_CreateCluster |
| `RegisterTaskDefinition` | ecs_metadata.yaml | ecs_RegisterTaskDefinition |
| `DescribeClusters` | ecs_metadata.yaml | ecs_DescribeClusters |
| `RunTask` | ecs_metadata.yaml | ecs_RunTask |
| `DescribeTasks` | ecs_metadata.yaml | ecs_DescribeTasks |
| `CreateService` | ecs_metadata.yaml | ecs_CreateService |
| `DescribeServices` | ecs_metadata.yaml | ecs_DescribeServices |
| `ListTasks` | ecs_metadata.yaml | ecs_ListTasks |
| `UpdateService` | ecs_metadata.yaml | ecs_UpdateService |
| `DeleteService` | ecs_metadata.yaml | ecs_DeleteService |
| `DeregisterTaskDefinition` | ecs_metadata.yaml | ecs_DeregisterTaskDefinition |
| `DeleteCluster` | ecs_metadata.yaml | ecs_DeleteCluster |
| `Amazon ECS Basics Scenario` | ecs_metadata.yaml | ecs_Scenario |

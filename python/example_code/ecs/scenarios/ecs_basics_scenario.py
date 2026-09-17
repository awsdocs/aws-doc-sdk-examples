# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon ECS Basics Scenario

This scenario demonstrates the full lifecycle of running a containerized workload
on Amazon ECS using AWS Fargate:
  1. Deploy CloudFormation stack for VPC/IAM prerequisites.
  2. Create an ECS cluster.
  3. Register a Fargate task definition.
  4. Describe the cluster.
  5. Run a standalone task.
  6. Wait for the task to run, then describe it.
  7. Create a service.
  8. List tasks in the service.
  9. Describe the service.
  10. Update the service (scale up).
  11. Clean up all resources.

Usage:
    python scenario_ecs_basics.py
"""

# snippet-start:[python.example_code.ecs.EcsScenario]
import json
import logging
import time

import boto3
from botocore.exceptions import ClientError, WaiterError

from ecs_wrapper import EcsWrapper

logger = logging.getLogger(__name__)

CLUSTER_NAME = "ecs-basics-cluster"
TASK_FAMILY = "ecs-basics-task-def"
SERVICE_NAME = "ecs-basics-service"
STACK_NAME = "ecs-basics-prerequisites"


def get_cfn_template():
    """Returns the CloudFormation template as a JSON string."""
    template = {
        "AWSTemplateFormatVersion": "2010-09-09",
        "Description": "ECS Basics - VPC, subnets, security group, and task execution role.",
        "Resources": {
            "EcsTaskExecutionRole": {
                "Type": "AWS::IAM::Role",
                "Properties": {
                    "AssumeRolePolicyDocument": {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": {"Service": "ecs-tasks.amazonaws.com"},
                                "Action": "sts:AssumeRole",
                            }
                        ],
                    },
                    "ManagedPolicyArns": [
                        "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
                    ],
                },
            },
            "Vpc": {
                "Type": "AWS::EC2::VPC",
                "Properties": {
                    "CidrBlock": "10.0.0.0/16",
                    "EnableDnsSupport": True,
                    "EnableDnsHostnames": True,
                },
            },
            "InternetGateway": {"Type": "AWS::EC2::InternetGateway"},
            "AttachGateway": {
                "Type": "AWS::EC2::VPCGatewayAttachment",
                "Properties": {
                    "VpcId": {"Ref": "Vpc"},
                    "InternetGatewayId": {"Ref": "InternetGateway"},
                },
            },
            "PublicSubnetOne": {
                "Type": "AWS::EC2::Subnet",
                "Properties": {
                    "VpcId": {"Ref": "Vpc"},
                    "CidrBlock": "10.0.1.0/24",
                    "AvailabilityZone": {"Fn::Select": ["0", {"Fn::GetAZs": ""}]},
                    "MapPublicIpOnLaunch": True,
                },
            },
            "PublicSubnetTwo": {
                "Type": "AWS::EC2::Subnet",
                "Properties": {
                    "VpcId": {"Ref": "Vpc"},
                    "CidrBlock": "10.0.2.0/24",
                    "AvailabilityZone": {"Fn::Select": ["1", {"Fn::GetAZs": ""}]},
                    "MapPublicIpOnLaunch": True,
                },
            },
            "RouteTable": {
                "Type": "AWS::EC2::RouteTable",
                "Properties": {"VpcId": {"Ref": "Vpc"}},
            },
            "DefaultRoute": {
                "Type": "AWS::EC2::Route",
                "DependsOn": "AttachGateway",
                "Properties": {
                    "RouteTableId": {"Ref": "RouteTable"},
                    "DestinationCidrBlock": "0.0.0.0/0",
                    "GatewayId": {"Ref": "InternetGateway"},
                },
            },
            "SubnetOneRouteTableAssoc": {
                "Type": "AWS::EC2::SubnetRouteTableAssociation",
                "Properties": {
                    "SubnetId": {"Ref": "PublicSubnetOne"},
                    "RouteTableId": {"Ref": "RouteTable"},
                },
            },
            "SubnetTwoRouteTableAssoc": {
                "Type": "AWS::EC2::SubnetRouteTableAssociation",
                "Properties": {
                    "SubnetId": {"Ref": "PublicSubnetTwo"},
                    "RouteTableId": {"Ref": "RouteTable"},
                },
            },
            "SecurityGroup": {
                "Type": "AWS::EC2::SecurityGroup",
                "Properties": {
                    "GroupDescription": "ECS Basics security group",
                    "VpcId": {"Ref": "Vpc"},
                    "SecurityGroupEgress": [
                        {
                            "IpProtocol": "-1",
                            "CidrIp": "0.0.0.0/0",
                        }
                    ],
                },
            },
        },
        "Outputs": {
            "TaskExecutionRoleArn": {
                "Value": {"Fn::GetAtt": ["EcsTaskExecutionRole", "Arn"]},
            },
            "SubnetIdOne": {"Value": {"Ref": "PublicSubnetOne"}},
            "SubnetIdTwo": {"Value": {"Ref": "PublicSubnetTwo"}},
            "SecurityGroupId": {"Value": {"Ref": "SecurityGroup"}},
        },
    }
    return json.dumps(template)


def deploy_stack(cfn_client):
    """Deploys the CloudFormation stack and returns outputs as a dict."""
    print(f"\nDeploying CloudFormation stack '{STACK_NAME}'...")
    cfn_client.create_stack(
        StackName=STACK_NAME,
        TemplateBody=get_cfn_template(),
        Capabilities=["CAPABILITY_IAM"],
    )
    waiter = cfn_client.get_waiter("stack_create_complete")
    print("Waiting for stack creation to complete...")
    waiter.wait(StackName=STACK_NAME, WaiterConfig={"Delay": 15, "MaxAttempts": 60})

    response = cfn_client.describe_stacks(StackName=STACK_NAME)
    outputs = response["Stacks"][0]["Outputs"]
    result = dict()
    for output in outputs:
        result[output["OutputKey"]] = output["OutputValue"]
    print(f"Stack outputs: {result}")
    return result


def delete_stack(cfn_client):
    """Deletes the CloudFormation stack."""
    print(f"\nDeleting CloudFormation stack '{STACK_NAME}'...")
    try:
        cfn_client.delete_stack(StackName=STACK_NAME)
        waiter = cfn_client.get_waiter("stack_delete_complete")
        waiter.wait(StackName=STACK_NAME, WaiterConfig={"Delay": 15, "MaxAttempts": 60})
        print("Stack deleted.")
    except ClientError as err:
        logger.error("Failed to delete stack: %s", err)


def run_scenario():
    """Runs the ECS Basics scenario."""
    ecs_client = boto3.client("ecs")
    cfn_client = boto3.client("cloudformation")
    wrapper = EcsWrapper(ecs_client)

    cluster_name = None
    task_def_arn = None
    standalone_task_arn = None
    service_name = None

    try:
        # --- Setup: Deploy CloudFormation stack ---
        stack_outputs = deploy_stack(cfn_client)
        execution_role_arn = stack_outputs["TaskExecutionRoleArn"]
        subnets = [stack_outputs["SubnetIdOne"], stack_outputs["SubnetIdTwo"]]
        security_groups = [stack_outputs["SecurityGroupId"]]

        # --- Setup: Create ECS cluster ---
        print(f"\nCreating ECS cluster '{CLUSTER_NAME}'...")
        cluster = wrapper.create_cluster(CLUSTER_NAME)
        cluster_name = cluster["clusterName"]
        print(f"  Cluster: {cluster['clusterName']} (ARN: {cluster['clusterArn']})")
        print(f"  Status: {cluster['status']}")

        # --- Step 1: Register task definition ---
        print(f"\nRegistering task definition '{TASK_FAMILY}'...")
        task_def = wrapper.register_task_definition(
            family=TASK_FAMILY,
            execution_role_arn=execution_role_arn,
        )
        task_def_arn = f"{task_def['family']}:{task_def['revision']}"
        print(f"  Family: {task_def['family']}, Revision: {task_def['revision']}")
        print(f"  ARN: {task_def['taskDefinitionArn']}")

        # --- Step 2: Describe the cluster ---
        print(f"\nDescribing cluster '{cluster_name}'...")
        clusters = wrapper.describe_clusters([cluster_name])
        if clusters:
            c = clusters[0]
            print(f"  Name: {c['clusterName']}, Status: {c['status']}")
            print(f"  Active services: {c['activeServicesCount']}")
            print(f"  Running tasks: {c['runningTasksCount']}")

        # --- Step 3: Run a standalone task ---
        print(f"\nRunning standalone task with '{task_def_arn}'...")
        tasks = wrapper.run_task(
            cluster=cluster_name,
            task_definition=task_def_arn,
            subnets=subnets,
            security_groups=security_groups,
        )
        if tasks:
            standalone_task_arn = tasks[0]["taskArn"]
            print(f"  Task ARN: {standalone_task_arn}")
            print(f"  Status: {tasks[0]['lastStatus']}")

        # --- Step 4: Wait for task to run, then describe it ---
        if standalone_task_arn:
            print("\nWaiting for task to reach RUNNING state...")
            try:
                waiter = ecs_client.get_waiter("tasks_running")
                waiter.wait(
                    cluster=cluster_name,
                    tasks=[standalone_task_arn],
                    WaiterConfig={"Delay": 6, "MaxAttempts": 100},
                )
            except WaiterError:
                print("  Task did not reach RUNNING state within timeout.")

            task_details = wrapper.describe_tasks(cluster_name, [standalone_task_arn])
            if task_details:
                t = task_details[0]
                print(f"  Task status: {t['lastStatus']}")
                for container in t.get("containers", list()):
                    print(f"  Container '{container['name']}': {container['lastStatus']}")

        # --- Step 5: Create a service ---
        print(f"\nCreating service '{SERVICE_NAME}'...")
        service = wrapper.create_service(
            cluster=cluster_name,
            service_name=SERVICE_NAME,
            task_definition=task_def_arn,
            desired_count=1,
            subnets=subnets,
            security_groups=security_groups,
        )
        service_name = service["serviceName"]
        print(f"  Service: {service['serviceName']} (ARN: {service['serviceArn']})")
        print(f"  Desired count: {service['desiredCount']}")

        # --- Step 6: List tasks in the service ---
        print("\nWaiting for service tasks to start...")
        time.sleep(20)
        task_arns = wrapper.list_tasks(
            cluster=cluster_name, service_name=service_name
        )
        print(f"  Service tasks: {task_arns}")

        # --- Step 7: Describe the service ---
        print(f"\nDescribing service '{service_name}'...")
        services = wrapper.describe_services(cluster_name, [service_name])
        if services:
            s = services[0]
            print(f"  Name: {s['serviceName']}, Status: {s['status']}")
            print(f"  Desired: {s['desiredCount']}, Running: {s['runningCount']}")
            events = s.get("events", list())
            if events:
                print(f"  Latest event: {events[0].get('message', 'N/A')}")

        # --- Step 8: Update the service (scale up) ---
        print(f"\nScaling service '{service_name}' to 2 tasks...")
        updated = wrapper.update_service(
            cluster=cluster_name,
            service=service_name,
            desired_count=2,
        )
        print(f"  Updated desired count: {updated['desiredCount']}")

    except Exception:
        logger.exception("Scenario encountered an error.")
        raise

    finally:
        # --- Cleanup ---
        print("\n--- Cleanup ---")

        # Stop standalone task
        if standalone_task_arn and cluster_name:
            try:
                print(f"Stopping standalone task '{standalone_task_arn}'...")
                ecs_client.stop_task(
                    cluster=cluster_name,
                    task=standalone_task_arn,
                    reason="Scenario cleanup",
                )
                stopped_waiter = ecs_client.get_waiter("tasks_stopped")
                stopped_waiter.wait(
                    cluster=cluster_name,
                    tasks=[standalone_task_arn],
                    WaiterConfig={"Delay": 6, "MaxAttempts": 100},
                )
                print("  Standalone task stopped.")
            except (ClientError, WaiterError) as err:
                logger.warning("Could not stop standalone task: %s", err)

        # Scale down and delete service
        if service_name and cluster_name:
            try:
                print(f"Scaling down service '{service_name}' to 0...")
                wrapper.update_service(
                    cluster=cluster_name,
                    service=service_name,
                    desired_count=0,
                )
                time.sleep(10)
                print(f"Deleting service '{service_name}'...")
                wrapper.delete_service(cluster=cluster_name, service=service_name)
                print("  Service deleted.")
            except ClientError as err:
                logger.warning("Could not delete service: %s", err)

        # Deregister task definition
        if task_def_arn:
            try:
                print(f"Deregistering task definition '{task_def_arn}'...")
                wrapper.deregister_task_definition(task_def_arn)
                print("  Task definition deregistered.")
            except ClientError as err:
                logger.warning("Could not deregister task definition: %s", err)

        # Delete cluster
        if cluster_name:
            try:
                print(f"Deleting cluster '{cluster_name}'...")
                wrapper.delete_cluster(cluster_name)
                print("  Cluster deleted.")
            except ClientError as err:
                logger.warning("Could not delete cluster: %s", err)

        # Delete CloudFormation stack
        delete_stack(cfn_client)

    print("\nECS Basics scenario complete!")


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    run_scenario()
# snippet-end:[python.example_code.ecs.EcsScenario]

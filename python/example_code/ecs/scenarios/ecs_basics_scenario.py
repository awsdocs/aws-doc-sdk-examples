# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon ECS Basics Scenario

This scenario demonstrates how to deploy and manage a containerized application
on Amazon ECS using AWS Fargate. The workflow covers:
1. Creating prerequisite infrastructure via CloudFormation (VPC, subnets, IAM role)
2. Creating an ECS cluster
3. Registering a Fargate task definition
4. Describing the task definition
5. Creating an ECS service
6. Waiting for service stability and describing the service
7. Listing running tasks
8. Describing task details (including public IP)
9. Scaling the service (updating desired count)
10. Cleaning up all resources
"""

# snippet-start:[python.example_code.ecs.EcsScenario]
import json
import logging
import time

import boto3
from botocore.exceptions import ClientError, WaiterError

from ecs_wrapper import EcsWrapper

logger = logging.getLogger(__name__)


def get_cfn_template() -> str:
    """Returns the CloudFormation template for prerequisite resources."""
    template = {
        "AWSTemplateFormatVersion": "2010-09-09",
        "Description": "ECS Basics Scenario - Prerequisite resources (VPC, Subnets, IGW, SG, IAM Role)",
        "Resources": {
            "VPC": {
                "Type": "AWS::EC2::VPC",
                "Properties": {
                    "CidrBlock": "10.0.0.0/16",
                    "EnableDnsSupport": True,
                    "EnableDnsHostnames": True,
                    "Tags": [{"Key": "Name", "Value": "ecs-basics-vpc"}],
                },
            },
            "InternetGateway": {
                "Type": "AWS::EC2::InternetGateway",
                "Properties": {
                    "Tags": [{"Key": "Name", "Value": "ecs-basics-igw"}]
                },
            },
            "AttachGateway": {
                "Type": "AWS::EC2::VPCGatewayAttachment",
                "Properties": {
                    "VpcId": {"Ref": "VPC"},
                    "InternetGatewayId": {"Ref": "InternetGateway"},
                },
            },
            "PublicSubnet1": {
                "Type": "AWS::EC2::Subnet",
                "Properties": {
                    "VpcId": {"Ref": "VPC"},
                    "CidrBlock": "10.0.1.0/24",
                    "AvailabilityZone": {"Fn::Select": ["0", {"Fn::GetAZs": ""}]},
                    "MapPublicIpOnLaunch": True,
                    "Tags": [{"Key": "Name", "Value": "ecs-basics-subnet-1"}],
                },
            },
            "PublicSubnet2": {
                "Type": "AWS::EC2::Subnet",
                "Properties": {
                    "VpcId": {"Ref": "VPC"},
                    "CidrBlock": "10.0.2.0/24",
                    "AvailabilityZone": {"Fn::Select": ["1", {"Fn::GetAZs": ""}]},
                    "MapPublicIpOnLaunch": True,
                    "Tags": [{"Key": "Name", "Value": "ecs-basics-subnet-2"}],
                },
            },
            "PublicRouteTable": {
                "Type": "AWS::EC2::RouteTable",
                "Properties": {
                    "VpcId": {"Ref": "VPC"},
                    "Tags": [{"Key": "Name", "Value": "ecs-basics-rt"}],
                },
            },
            "PublicRoute": {
                "Type": "AWS::EC2::Route",
                "DependsOn": "AttachGateway",
                "Properties": {
                    "RouteTableId": {"Ref": "PublicRouteTable"},
                    "DestinationCidrBlock": "0.0.0.0/0",
                    "GatewayId": {"Ref": "InternetGateway"},
                },
            },
            "SubnetRouteTableAssociation1": {
                "Type": "AWS::EC2::SubnetRouteTableAssociation",
                "Properties": {
                    "SubnetId": {"Ref": "PublicSubnet1"},
                    "RouteTableId": {"Ref": "PublicRouteTable"},
                },
            },
            "SubnetRouteTableAssociation2": {
                "Type": "AWS::EC2::SubnetRouteTableAssociation",
                "Properties": {
                    "SubnetId": {"Ref": "PublicSubnet2"},
                    "RouteTableId": {"Ref": "PublicRouteTable"},
                },
            },
            "SecurityGroup": {
                "Type": "AWS::EC2::SecurityGroup",
                "Properties": {
                    "GroupDescription": "ECS Basics - Allow HTTP inbound",
                    "VpcId": {"Ref": "VPC"},
                    "SecurityGroupIngress": [
                        {
                            "IpProtocol": "tcp",
                            "FromPort": 80,
                            "ToPort": 80,
                            "CidrIp": "0.0.0.0/0",
                        }
                    ],
                    "SecurityGroupEgress": [
                        {
                            "IpProtocol": "-1",
                            "CidrIp": "0.0.0.0/0",
                        }
                    ],
                    "Tags": [{"Key": "Name", "Value": "ecs-basics-sg"}],
                },
            },
            "TaskExecutionRole": {
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
        },
        "Outputs": {
            "VpcId": {"Value": {"Ref": "VPC"}},
            "SubnetId1": {"Value": {"Ref": "PublicSubnet1"}},
            "SubnetId2": {"Value": {"Ref": "PublicSubnet2"}},
            "SecurityGroupId": {"Value": {"Ref": "SecurityGroup"}},
            "TaskExecutionRoleArn": {"Value": {"Fn::GetAtt": ["TaskExecutionRole", "Arn"]}},
        },
    }
    return json.dumps(template)


def deploy_prerequisites(cfn_client, stack_name: str) -> dict:
    """
    Deploys prerequisite resources using CloudFormation.

    :param cfn_client: A Boto3 CloudFormation client.
    :param stack_name: The name for the CloudFormation stack.
    :return: A dictionary of stack outputs.
    """
    print(f"\n--- Deploying CloudFormation stack: {stack_name} ---")
    template_body = get_cfn_template()

    cfn_client.create_stack(
        StackName=stack_name,
        TemplateBody=template_body,
        Capabilities=["CAPABILITY_IAM"],
    )

    print("Waiting for stack creation to complete...")
    waiter = cfn_client.get_waiter("stack_create_complete")
    waiter.wait(StackName=stack_name, WaiterConfig={"Delay": 15, "MaxAttempts": 60})

    response = cfn_client.describe_stacks(StackName=stack_name)
    outputs = response["Stacks"][0]["Outputs"]
    output_map = dict()
    for output in outputs:
        output_map[output["OutputKey"]] = output["OutputValue"]

    print("Stack created successfully. Resources:")
    for key, value in output_map.items():
        print(f"  {key}: {value}")

    return output_map


def cleanup_stack(cfn_client, stack_name: str) -> None:
    """
    Deletes the CloudFormation stack.

    :param cfn_client: A Boto3 CloudFormation client.
    :param stack_name: The stack name to delete.
    """
    print(f"\n--- Deleting CloudFormation stack: {stack_name} ---")
    cfn_client.delete_stack(StackName=stack_name)
    waiter = cfn_client.get_waiter("stack_delete_complete")
    waiter.wait(StackName=stack_name, WaiterConfig={"Delay": 15, "MaxAttempts": 60})
    print("Stack deleted successfully.")


def run_scenario():
    """Runs the Amazon ECS Basics scenario."""
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    print("=" * 70)
    print("Welcome to the Amazon ECS Basics Scenario!")
    print("=" * 70)
    print(
        "\nThis scenario demonstrates deploying and managing a containerized\n"
        "application on Amazon ECS using AWS Fargate.\n"
    )

    # Configuration
    timestamp = str(int(time.time()))
    cluster_name = f"ecs-basics-cluster-{timestamp}"
    service_name = "ecs-basics-service"
    task_family = "ecs-basics-task-def"
    stack_name = f"ecs-basics-stack-{timestamp}"
    container_name = "web-container"
    container_image = "public.ecr.aws/docker/library/httpd:latest"

    # Initialize clients
    ecs_wrapper = EcsWrapper.from_client()
    cfn_client = boto3.client("cloudformation")

    stack_outputs = None

    try:
        # Setup: Deploy CloudFormation stack
        stack_outputs = deploy_prerequisites(cfn_client, stack_name)
        subnet1 = stack_outputs["SubnetId1"]
        subnet2 = stack_outputs["SubnetId2"]
        security_group_id = stack_outputs["SecurityGroupId"]
        execution_role_arn = stack_outputs["TaskExecutionRoleArn"]

        # Step 1: Create an ECS Cluster
        print(f"\n--- Step 1: Create ECS Cluster '{cluster_name}' ---")
        cluster = ecs_wrapper.create_cluster(cluster_name)
        print(f"  Cluster ARN: {cluster['clusterArn']}")
        print(f"  Status: {cluster['status']}")

        # Step 2: Register a Task Definition
        print(f"\n--- Step 2: Register Task Definition '{task_family}' ---")
        task_def = ecs_wrapper.register_task_definition(
            family=task_family,
            execution_role_arn=execution_role_arn,
            container_name=container_name,
            container_image=container_image,
            cpu="256",
            memory="512",
            container_port=80,
        )
        task_def_arn = task_def["taskDefinitionArn"]
        task_def_revision = f"{task_def['family']}:{task_def['revision']}"
        print(f"  Task Definition ARN: {task_def_arn}")
        print(f"  Revision: {task_def['revision']}")

        # Step 3: Describe the Task Definition
        print(f"\n--- Step 3: Describe Task Definition '{task_def_revision}' ---")
        described_td = ecs_wrapper.describe_task_definition(task_def_revision)
        print(f"  Family: {described_td['family']}")
        print(f"  Revision: {described_td['revision']}")
        print(f"  Status: {described_td['status']}")
        print(f"  CPU: {described_td['cpu']}, Memory: {described_td['memory']}")
        print(f"  Network Mode: {described_td['networkMode']}")
        if described_td.get("containerDefinitions"):
            print(f"  Container Image: {described_td['containerDefinitions'][0]['image']}")

        # Step 4: Create an ECS Service
        print(f"\n--- Step 4: Create ECS Service '{service_name}' ---")
        service = ecs_wrapper.create_service(
            cluster_name=cluster_name,
            service_name=service_name,
            task_definition=task_def_revision,
            desired_count=1,
            subnets=[subnet1, subnet2],
            security_groups=[security_group_id],
            assign_public_ip=True,
        )
        print(f"  Service ARN: {service['serviceArn']}")
        print(f"  Status: {service['status']}")

        # Step 5: Wait for Service Stability and Describe Service
        print(f"\n--- Step 5: Wait for Service Stability ---")
        try:
            ecs_wrapper.wait_for_service_stable(cluster_name, service_name)
        except WaiterError as e:
            logger.warning("Waiter timed out: %s. Continuing...", str(e))

        services = ecs_wrapper.describe_services(cluster_name, [service_name])
        if services:
            svc = services[0]
            print(f"  Service Status: {svc['status']}")
            print(f"  Running Count: {svc['runningCount']}")
            print(f"  Desired Count: {svc['desiredCount']}")

        # Step 6: List Running Tasks
        print(f"\n--- Step 6: List Running Tasks ---")
        task_arns = ecs_wrapper.list_tasks(cluster_name, service_name)
        for arn in task_arns:
            print(f"  Task ARN: {arn}")

        # Step 7: Describe Tasks
        if task_arns:
            print(f"\n--- Step 7: Describe Tasks ---")
            tasks = ecs_wrapper.describe_tasks(cluster_name, task_arns)
            for task in tasks:
                print(f"  Task ARN: {task['taskArn']}")
                print(f"    Last Status: {task['lastStatus']}")
                print(f"    Health Status: {task.get('healthStatus', 'N/A')}")
                print(f"    CPU: {task.get('cpu', 'N/A')}, Memory: {task.get('memory', 'N/A')}")
                for container in task.get("containers", list()):
                    print(f"    Container: {container['name']} - {container.get('lastStatus', 'N/A')}")
                    for ni in container.get("networkInterfaces", list()):
                        print(f"      Private IP: {ni.get('privateIpv4Address', 'N/A')}")
                for attachment in task.get("attachments", list()):
                    for detail in attachment.get("details", list()):
                        if detail.get("name") == "networkInterfaceId":
                            print(f"    ENI: {detail.get('value', 'N/A')}")
        else:
            print("\n--- Step 7: No tasks to describe ---")

        # Step 8: Update the Service (Scale Up)
        print(f"\n--- Step 8: Scale Service to 2 Tasks ---")
        ecs_wrapper.update_service(cluster_name, service_name, desired_count=2)
        print("  Waiting for service to stabilize after scaling...")
        try:
            ecs_wrapper.wait_for_service_stable(cluster_name, service_name)
        except WaiterError as e:
            logger.warning("Waiter timed out during scale up: %s. Continuing...", str(e))

        services = ecs_wrapper.describe_services(cluster_name, [service_name])
        if services:
            svc = services[0]
            print(f"  Running Count: {svc['runningCount']}")
            print(f"  Desired Count: {svc['desiredCount']}")

    finally:
        # Cleanup
        print("\n" + "=" * 70)
        print("CLEANUP")
        print("=" * 70)

        # Step 9: Delete the ECS Service
        try:
            print(f"\n--- Cleanup: Scale down and delete service '{service_name}' ---")
            try:
                ecs_wrapper.update_service(cluster_name, service_name, desired_count=0)
                time.sleep(5)
            except ClientError:
                pass

            try:
                ecs_wrapper.delete_service(cluster_name, service_name, force=True)
                print("  Service deletion initiated.")
                try:
                    ecs_wrapper.wait_for_service_inactive(cluster_name, service_name)
                    print("  Service is now inactive.")
                except WaiterError:
                    logger.warning("Service inactive waiter timed out.")
            except ClientError as err:
                if err.response["Error"]["Code"] == "ServiceNotFoundException":
                    print("  Service already deleted.")
                else:
                    logger.error("Error deleting service: %s", err)
        except Exception as e:
            logger.error("Unexpected error during service cleanup: %s", e)

        # Step 10: Delete the ECS Cluster
        try:
            print(f"\n--- Cleanup: Delete cluster '{cluster_name}' ---")
            ecs_wrapper.delete_cluster(cluster_name)
            print("  Cluster deleted.")
        except ClientError as err:
            logger.error("Error deleting cluster: %s", err)

        # Step 11: Delete the CloudFormation Stack
        if stack_outputs is not None:
            try:
                cleanup_stack(cfn_client, stack_name)
            except Exception as e:
                logger.error("Error deleting CloudFormation stack: %s", e)

    print("\n" + "=" * 70)
    print("Amazon ECS Basics Scenario complete!")
    print("=" * 70)


if __name__ == "__main__":
    run_scenario()
# snippet-end:[python.example_code.ecs.EcsScenario]

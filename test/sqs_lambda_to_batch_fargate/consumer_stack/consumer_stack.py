# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0

from aws_cdk import (
    aws_iam as iam,
    aws_events as events,
    aws_events_targets as targets,
    aws_lambda as _lambda,
    aws_lambda_event_sources as _event_sources,
    aws_sqs as sqs,
    aws_sns as sns,
    aws_sns_subscriptions as subs,
    aws_logs as logs,
    aws_ec2 as ec2,
    aws_ecs as ecs,
    aws_ecr as ecr,
    aws_batch as batch,
    aws_batch_alpha as batch_alpha,
    aws_kinesis as kinesis,
    Aws,
    Stack
)
from constructs import Construct
import json
import os

# Raises KeyError if environment variable doesn't exist.
language_name = os.environ["LANGUAGE_NAME"]
producer_account_id = os.environ["PRODUCER_ACCOUNT_ID"]
fanout_topic_name = os.environ["FANOUT_TOPIC_NAME"]

class ConsumerStack(Stack):
    def __init__(self, scope: Construct, id: str, **kwargs) -> None:
        super().__init__(scope, id, **kwargs)

        #############################################
        ##                                         ##
        ##                 RESOURCES               ##
        ##                                         ##
        #############################################

        # Locate Amazon Simple Notification Service (Amazon SNS) topic in the producer account.
        fanout_topic_arn = f'arn:aws:sns:us-east-1:{producer_account_id}:{fanout_topic_name}'
        sns_topic = sns.Topic.from_topic_arn(self, fanout_topic_name, topic_arn=fanout_topic_arn)

        container_image = ecs.EcrImage.from_registry(f"public.ecr.aws/b4v4v1s0/{language_name}:latest")

        # Define the Amazon Simple Queue Service (Amazon SQS) queue in this account.
        sqs_queue = sqs.Queue(self, f'BatchJobQueue-{language_name}')

        # Create an AWS Identity and Access Management (IAM) role for the SNS topic to send messages to the SQS queue.
        sns_topic_role = iam.Role(
            self, f"SNSTopicRole-{language_name}",
            assumed_by=iam.ServicePrincipal('sns.amazonaws.com'),
            description='Allows the SNS topic to send messages to the SQS queue in this account',
            role_name=f'SNSTopicRole-{language_name}'
        )

        # Policy to allow existing SNS topic to publish to new SQS queue.
        sns_topic_policy = iam.PolicyStatement(
            effect=iam.Effect.ALLOW,
            actions=["sqs:SendMessage"],
            resources=[sqs_queue.queue_arn],
            conditions={
                "ArnEquals": {
                    "aws:SourceArn": fanout_topic_arn
                }
            }
        )

        # Execution role for AWS Lambda function to use.
        execution_role = iam.Role(
            self, f"LambdaExecutionRole-{language_name}",
            assumed_by=iam.ServicePrincipal('lambda.amazonaws.com'),
            description='Allows Lambda function to submit jobs to Batch',
            role_name=f'LambdaExecutionRole-{language_name}'
        )

        batch_execution_role = iam.Role(
            self, f"BatchExecutionRole-{language_name}",
            assumed_by=iam.ServicePrincipal("ecs-tasks.amazonaws.com"),
            inline_policies={
                "MyCustomPolicy": iam.PolicyDocument(
                    statements=[
                        iam.PolicyStatement(
                            effect=iam.Effect.ALLOW,
                            actions=[
                                "logs:CreateLogGroup",
                                "logs:CreateLogStream",
                                "logs:PutLogEvents",
                                "logs:DescribeLogStreams"
                            ],
                            resources=["arn:aws:logs:*:*:*"]
                        )
                    ]
                )
            },
            managed_policies=[
                iam.ManagedPolicy.from_aws_managed_policy_name(
                    "job-function/SystemAdministrator"
                ),
                iam.ManagedPolicy.from_aws_managed_policy_name(
                    "service-role/AmazonECSTaskExecutionRolePolicy"
                )
            ]
        )

        # Batch resources commented out due to bug: https://github.com/aws/aws-cdk/issues/24783.
        # Using Alpha as workaround.

        fargate_environment = batch_alpha.ComputeEnvironment(self, f"FargateEnv-{language_name}",
            compute_resources=batch_alpha.ComputeResources(
                type=batch_alpha.ComputeResourceType.FARGATE,
                vpc=ec2.Vpc.from_lookup(self, "Vpc", is_default=True)
            )
        )

        # Configure AWS Batch to use the log group in the producer account.
        log_config = batch_alpha.LogConfiguration(
            log_driver=batch_alpha.LogDriver.AWSLOGS
        )

        job_definition = batch_alpha.JobDefinition(self, f"JobDefinition-{language_name}",
            container=batch_alpha.JobDefinitionContainer(
                image=container_image,
                execution_role=batch_execution_role,
                log_configuration=log_config,
                assign_public_ip=True,
            ),
            platform_capabilities=[ batch_alpha.PlatformCapabilities.FARGATE ]
        )

        job_queue = batch_alpha.JobQueue(self, f"JobQueue-{language_name}",
            compute_environments=[batch_alpha.JobQueueComputeEnvironment(
               compute_environment=fargate_environment,
               order=1
            )]
        )

        # Define the Lambda function.
        function = _lambda.Function(self, f'SubmitBatchJob-{language_name}',
                                    runtime=_lambda.Runtime.PYTHON_3_8,
                                    handler='lambda_handler.lambda_handler',
                                    role=execution_role,
                                    code=_lambda.Code.from_asset('lambda'),
                                    environment={
                                        'LANGUAGE_NAME': language_name,
                                        'JOB_QUEUE': job_queue.job_queue_arn,
                                        'JOB_DEFINITION': job_definition.job_definition_arn,
                                        'JOB_NAME': f'job-{language_name}'
                                    }
                                    )

        #################################################
        ##                                             ##
        ##                 CONFIGURATION               ##
        ##                                             ##
        #################################################

        # Add the SQS queue as an event source for the Lambda function.
        function.add_event_source(_event_sources.SqsEventSource(sqs_queue))

        # Create an SNS subscription for the SQS queue.
        subs.SqsSubscription(sqs_queue, raw_message_delivery=True).bind(sns_topic)
        sns_topic.add_subscription(subs.SqsSubscription(sqs_queue))

        ##########################################
        ##                                      ##
        ##                 ACCESS               ##
        ##                                      ##
        ##########################################

        # Add the Amazon SNS and Amazon SQS policy to the IAM role.
        sns_topic_role.add_to_policy(sns_topic_policy)

        # Grant permissions to allow the function to receive messages from the queue.
        sqs_queue.grant_consume_messages(function)

        # Grant permissions to allow the function to read messages from the queue and to write logs to Amazon CloudWatch.
        function.add_to_role_policy(
            statement=iam.PolicyStatement(
                actions=['sqs:ReceiveMessage'],
                resources=[sqs_queue.queue_arn]
            )
        )
        function.add_to_role_policy(
            statement=iam.PolicyStatement(
                actions=['logs:CreateLogGroup', 'logs:CreateLogStream', 'logs:PutLogEvents'],
                resources=["*"]
            )
        )

        execution_role.add_to_policy(
            statement=iam.PolicyStatement(
                actions=['batch:*'],
                resources=["*"]
            )
        )

        # Define policy that allows cross-account Amazon SNS and Amazon SQS access.
        statement = iam.PolicyStatement()
        statement.add_resources(sqs_queue.queue_arn)
        statement.add_actions("sqs:*")
        statement.add_arn_principal(f'arn:aws:iam::{producer_account_id}:root')
        statement.add_arn_principal(f'arn:aws:iam::{Aws.ACCOUNT_ID}:root')
        statement.add_condition("ArnLike", {"aws:SourceArn": fanout_topic_arn})
        sqs_queue.add_to_resource_policy(statement)

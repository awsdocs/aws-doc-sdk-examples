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
    Stack
)
from constructs import Construct
import json

class ConsumerStack(Stack):
    def __init__(self, scope: Construct, id: str, **kwargs) -> None:
        super().__init__(scope, id, **kwargs)

        #############################################
        ##                                         ##
        ##                 RESOURCES               ##
        ##                                         ##
        #############################################

        # Define existing SNS topic in producer account
        fanout_topic_name = "ProducerStack-MyTopic86869434-71bWvzg3W1sQ"
        fanout_topic_arn = f'arn:aws:sns:us-east-1:808326389482:{fanout_topic_name}'
        sns_topic = sns.Topic.from_topic_arn(self, fanout_topic_name, topic_arn=fanout_topic_arn)
        container_image = ecs.EcrImage.from_registry("public.ecr.aws/b4v4v1s0/ruby:latest")

        # Define the SQS queue in this account
        sqs_queue = sqs.Queue(self, 'BatchJobQueue')

        # Create an IAM role for the SNS topic to send messages to the SQS queue
        sns_topic_role = iam.Role(
            self, "SNSTopicRole",
            assumed_by=iam.ServicePrincipal('sns.amazonaws.com'),
            description='Allows the SNS topic to send messages to the SQS queue in this account',
            role_name='SNSTopicRole'
        )

        # Policy to allow existing SNS topic to publish to new SQS queue
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

        # Execution role for Lambda function to use
        execution_role = iam.Role(
            self, "LambdaExecutionRole",
            assumed_by=iam.ServicePrincipal('lambda.amazonaws.com'),
            description='Allows Lambda function to submit jobs to Batch',
            role_name='LambdaExecutionRole'
        )

        # Define the Lambda function
        function = _lambda.Function(self, 'SubmitBatchJob',
            runtime=_lambda.Runtime.PYTHON_3_8,
            handler='lambda_handler.lambda_handler',
            role=execution_role,
            code=_lambda.Code.from_asset('lambda'),
            environment={
                'JOB_NAME': 'rubylang'
            }
        )

        # # Batch resources commented out due to bug: https://github.com/aws/aws-cdk/issues/24783
        #
        # # Define compute environment
        # vpc = ec2.Vpc.from_lookup(self, "Vpc", is_default=True)
        # compute_environment = batch.CfnComputeEnvironment(self,'CfnComputeEnvironmentBatchMaterializeQuery',
        #     type="MANAGED",
        #     compute_environment_name="BatchMaterializeQueryCE",
        #     state="Enabled",
        #     compute_resources=batch.CfnComputeEnvironment.ComputeResourcesProperty(
        #         maxv_cpus=8,
        #         type="FARGATE",
        #         subnets=vpc.select_subnets(
        #             subnet_type=ec2.SubnetType.PUBLIC
        #         ).subnet_ids,
        #         security_group_ids=[
        #             ec2.SecurityGroup(self, "DefaultSG",
        #                 vpc=vpc
        #             ).security_group_id
        #         ]
        #     )
        # )
        #
        # # Define the job queue
        # batch_job_queue = batch.CfnJobQueue(self, "CfnJobQueueBatchMaterializeQuery",
        #     compute_environment_order=[
        #         batch.CfnJobQueue.ComputeEnvironmentOrderProperty(
        #             compute_environment=compute_environment.attr_compute_environment_arn,
        #             order=1
        #         )],
        #     priority=1,
        #     state="ENABLED"
        # )
        #
        # Define the job definition
        # cfn_job_definition = batch.CfnJobDefinition(self, "MyCfnJobDefinition",
        #     type="fargate",
        #     job_definition_name='test',
        #     container_properties=batch.CfnJobDefinition.ContainerPropertiesProperty(
        #         # environment=compute_environment,
        #         image="public.ecr.aws/b4v4v1s0/ruby:latest",
        #         # network_configuration=batch.CfnJobDefinition.NetworkConfigurationProperty(
        #         #     assign_public_ip="assignPublicIp"
        #         # ),
        #         job_role_arn="arn:aws:iam::260778392212:role/sdk-code-examples-batch",
        #         execution_role_arn="arn:aws:iam::260778392212:role/sdk-code-examples-batch",
        #         vcpus=123,
        #         memory=123,
        #         command=["rspec","/src/ruby/example_code/dynamodb/spec/scenario_getting_started_movies_spec.rb"],
        #     )
        # )

        vpc = ec2.Vpc.from_lookup(self, "Vpc", is_default=True)

        fargate_environment = batch_alpha.ComputeEnvironment(self, "MyFargateEnvironment",
            compute_resources=batch_alpha.ComputeResources(
                type=batch_alpha.ComputeResourceType.FARGATE,
                vpc=vpc
            )
        )

        repo = ecr.Repository.from_repository_arn(self, "b4v4v1s0", "arn:aws:ecr:us-east-1:260778392212:repository/b4v4v1s0/ruby")

        batch_alpha.JobDefinition(self, "batch-job-def-from-ecr",
            container=batch_alpha.JobDefinitionContainer(
                # image=ecs.EcrImage(repo, "latest"),
                image=container_image,
                execution_role=iam.Role.from_role_arn(self, 'sdk-code-examples-batch', "arn:aws:iam::260778392212:role/sdk-code-examples-batch")
            ),
            platform_capabilities=[ batch_alpha.PlatformCapabilities.FARGATE ]
        )

        job_queue = batch_alpha.JobQueue(self, "JobQueue",
            compute_environments=[batch_alpha.JobQueueComputeEnvironment(
               # Defines a collection of compute resources to handle assigned batch jobs
               compute_environment=fargate_environment,
               # Order determines the allocation order for jobs (i.e. Lower means higher preference for job assignment)
               order=1
            )]
        )

        #################################################
        ##                                             ##
        ##                 CONFIGURATION               ##
        ##                                             ##
        #################################################

        # Add the SQS queue as an event source for the Lambda function
        function.add_event_source(_event_sources.SqsEventSource(sqs_queue))

        # Create an SNS subscription for the SQS queue
        subs.SqsSubscription(sqs_queue, raw_message_delivery=True).bind(sns_topic)
        sns_topic.add_subscription(subs.SqsSubscription(sqs_queue))

        ##########################################
        ##                                      ##
        ##                 ACCESS               ##
        ##                                      ##
        ##########################################

        # Add SNS-SQS policy to the IAM role
        sns_topic_role.add_to_policy(sns_topic_policy)

        # Grant permissions to allow the function to receive messages from the queue
        sqs_queue.grant_consume_messages(function)

        # Grant permissions to allow the function to read messages from the queue and to write logs to CloudWatch
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

        # Define policy for allow cross-account SNS-SQS access
        statement = iam.PolicyStatement()
        statement.add_resources(sqs_queue.queue_arn)
        statement.add_actions("sqs:*")
        statement.add_arn_principal('arn:aws:iam::808326389482:root')
        statement.add_arn_principal('arn:aws:iam::260778392212:root')
        statement.add_condition("ArnLike", {"aws:SourceArn": fanout_topic_arn})
        sqs_queue.add_to_resource_policy(statement)




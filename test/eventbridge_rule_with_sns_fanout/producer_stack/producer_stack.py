# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0

import boto3
from aws_cdk import (
    aws_iam as iam,
    aws_events as events,
    aws_events_targets as targets,
    aws_sns as sns,
    aws_kinesis as kinesis,
    aws_sns_subscriptions as subscriptions,
    aws_logs as logs,
    Aws,
    Stack
)
from constructs import Construct

class ProducerStack(Stack):
    def __init__(self, scope: Construct, id: str, **kwargs) -> None:
        super().__init__(scope, id, **kwargs)

        client = boto3.client('ssm')

        onboarded_languages = [
            'ruby'
            # 'javav2'
            # 'javascriptv3'
            # 'gov2'
            # 'python'
            # 'dotnetv3'
            # 'kotlin'
            # 'rust_dev_preview'
            # 'swift'
            # 'cpp'
            # 'gov2'
            # 'sap-abap'
        ]

        account_ids = []
        for language_name in onboarded_languages:
            response = client.get_parameter(Name=language_name, WithDecryption=True)
            account_ids.append(response['Parameter']['Value'])

        # create a new SNS topic
        topic = sns.Topic(self, "fanout-topic")

        # create a new EventBridge rule
        rule = events.Rule(
            self,
            "trigger-rule",
            schedule=events.Schedule.cron(
                # Uncomment after testing
                # minute="0",
                # hour="22",
                # week_day="FRI",
            ),
        )

        # add a target to the EventBridge rule to publish a message to the SNS topic
        rule.add_target(targets.SnsTopic(topic))

        # Set up base SNS permissions
        sns_permissions = iam.PolicyStatement()
        sns_permissions.add_any_principal()
        sns_permissions.add_actions(
                        "SNS:Publish",
                        "SNS:RemovePermission",
                        "SNS:SetTopicAttributes",
                        "SNS:DeleteTopic",
                        "SNS:ListSubscriptionsByTopic",
                        "SNS:GetTopicAttributes",
                        "SNS:AddPermission",
                        "SNS:Subscribe"
                    )
        sns_permissions.add_resources(topic.topic_arn)
        sns_permissions.add_condition("StringEquals", {"AWS:SourceOwner": Aws.ACCOUNT_ID})
        topic.add_to_resource_policy(sns_permissions)

        # Set up cross-account Subscription permissions for every onboarded language
        subscribe_permissions = iam.PolicyStatement()
        subscribe_permissions.add_arn_principal(f'arn:aws:iam::{Aws.ACCOUNT_ID}:root')
        for id in account_ids:
            subscribe_permissions.add_arn_principal(f'arn:aws:iam::{id}:root')
        subscribe_permissions.add_actions("SNS:Subscribe")
        subscribe_permissions.add_resources(topic.topic_arn)
        topic.add_to_resource_policy(subscribe_permissions)

        # Set up cross-account Publish permissions for every onboarded language
        publish_permissions = iam.PolicyStatement()
        publish_permissions.add_arn_principal(f'arn:aws:iam::{Aws.ACCOUNT_ID}:root')
        for id in account_ids:
            subscribe_permissions.add_arn_principal(f'arn:aws:iam::{id}:root')
        publish_permissions.add_actions("SNS:Publish")
        publish_permissions.add_service_principal("events.amazonaws.com")
        publish_permissions.add_resources(topic.topic_arn)
        topic.add_to_resource_policy(publish_permissions)

        # Logging

        # Create an IAM role that allows cross-account access
        producer_log_role = iam.Role(
            self,
            "ProducerLogRole",
            assumed_by=iam.AccountPrincipal(account_id='260778392212'),
            role_name="ProducerLogRole"
        )

        # Grant permission to write to the log group
        log_group = logs.LogGroup.from_log_group_name(
            self, "LogGroup", "/aws/lambda/my-function-name"
        )
        log_group.grant_write(producer_log_role)

        # # Export the IAM role ARN
        # core.CfnOutput(
        #     self,
        #     "ProducerLogRoleArn",
        #     value=producer_log_role.role_arn,
        #     export_name="ProducerLogRoleArn"
        # )

        # # Export the ARN of the consumer role as an output
        # core.CfnOutput(
        #     self,
        #     "ConsumerRoleArn",
        #     value=consumer_role.role_arn,
        #     export_name="ConsumerRoleArn"
        # )

        # # Define the policy document that allows the role to put data into the Kinesis stream
        # policy_statement= iam.PolicyStatement(
        #             effect=iam.Effect.ALLOW,
        #             actions=[
        #                 "kinesis:PutRecord",
        #                 "kinesis:PutRecords",
        #             ],
        #             resources=[
        #                 kinesis_stream.stream_arn,
        #             ],
        #         )
        #
        # # Add the policy document to the role
        # kinesis_role.add_to_policy(policy_statement)

        # # Set up cross-account Publish permissions for every onboarded language
        # log_trust_policy = iam.PolicyStatement()
        # log_trust_policy.add_actions("sts:AssumeRole")
        # # log_trust_policy.add_service_principal("logs.amazonaws.com")
        # log_trust_policy.add_resource(kinesis_stream.stream_arn)
        # log_trust_policy.add_condition("StringLike", {"aws:SourceArn": f"arn:aws:logs:us-east-1:{Aws.ACCOUNT_ID}:*"})
        # for id in account_ids:
        #     log_trust_policy.add_condition("StringLike",{"aws:SourceArn": f"arn:aws:logs:us-east-1:{id}:*"})
        # kinesis_role.add_to_policy(log_trust_policy)

        # # Set up cross-account Publish permissions for every onboarded language
        # trust_statement = iam.PolicyStatement(
        #     effect=iam.Effect.ALLOW,
        #     actions=["sts:AssumeRole"],
        #     resources=[kinesis_role.role_arn],
        # )
        # # trust_statement.add_service_principal("logs.amazonaws.com")
        # # for id in account_ids:
        # #     trust_statement.add_condition(
        # #         "StringLike",
        # #         {"aws:SourceArn": f"arn:aws:logs:us-east-1:{id}:*"}
        # #     )
        # # trust_statement.add_condition(
        # #     "StringLike",
        # #     {"aws:SourceArn": f"arn:aws:logs:us-east-1:{Aws.ACCOUNT_ID}:*"},
        # # )
        # kinesis_role.add_to_policy(trust_statement)



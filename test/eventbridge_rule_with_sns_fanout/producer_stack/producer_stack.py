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

        # Create a new Amazon Simple Notification Service (Amazon SNS) topic.
        topic = sns.Topic(self, "fanout-topic")

        # Create a new Amazon EventBridge rule.
        rule = events.Rule(
            self,
            "trigger-rule",
            schedule=events.Schedule.cron(
                # Uncomment after testing.
                # minute="0",
                # hour="22",
                # week_day="FRI",
            ),
        )

        # Add a target to the EventBridge rule to publish a message to the SNS topic.
        rule.add_target(targets.SnsTopic(topic))

        # Set up base Amazon SNS permissions.
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

        # Set up cross-account Subscription permissions for every onboarded language.
        subscribe_permissions = iam.PolicyStatement()
        subscribe_permissions.add_arn_principal(f'arn:aws:iam::{Aws.ACCOUNT_ID}:root')
        for id in account_ids:
            subscribe_permissions.add_arn_principal(f'arn:aws:iam::{id}:root')
        subscribe_permissions.add_actions("SNS:Subscribe")
        subscribe_permissions.add_resources(topic.topic_arn)
        topic.add_to_resource_policy(subscribe_permissions)

        # Set up cross-account Publish permissions for every onboarded language.
        publish_permissions = iam.PolicyStatement()
        publish_permissions.add_arn_principal(f'arn:aws:iam::{Aws.ACCOUNT_ID}:root')
        for id in account_ids:
            subscribe_permissions.add_arn_principal(f'arn:aws:iam::{id}:root')
        publish_permissions.add_actions("SNS:Publish")
        publish_permissions.add_service_principal("events.amazonaws.com")
        publish_permissions.add_resources(topic.topic_arn)
        topic.add_to_resource_policy(publish_permissions)

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0

import boto3
from aws_cdk import (
    aws_iam as iam,
    aws_events as events,
    aws_events_targets as targets,
    aws_sns as sns,
    aws_sns_subscriptions as subscriptions,
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

        master_account = client.get_parameter(Name='weathertop_central', WithDecryption=True)

        statement1 = iam.PolicyStatement()
        statement1.add_any_principal()
        statement1.add_actions(
                        "SNS:Publish",
                        "SNS:RemovePermission",
                        "SNS:SetTopicAttributes",
                        "SNS:DeleteTopic",
                        "SNS:ListSubscriptionsByTopic",
                        "SNS:GetTopicAttributes",
                        "SNS:AddPermission",
                        "SNS:Subscribe"
                    )
        statement1.add_resources(topic.topic_arn)
        statement1.add_condition("StringEquals", {"AWS:SourceOwner": master_account})
        topic.add_to_resource_policy(statement1)

        statement2 = iam.PolicyStatement()
        statement2.add_arn_principal(f'arn:aws:iam::{master_account}:root')
        for language_name in onboarded_languages:
            response = client.get_parameter(Name=f'{language_name}', WithDecryption=True)
            account_id = response['Parameter']['Value']
            statement2.add_arn_principal(f'arn:aws:iam::{account_id}:root')
        statement2.add_actions("SNS:Subscribe")
        statement2.add_resources(topic.topic_arn)
        topic.add_to_resource_policy(statement2)

        statement3 = iam.PolicyStatement()
        statement3.add_arn_principal(f'arn:aws:iam::{master_account}:root')
        for language_name in onboarded_languages:
            response = client.get_parameter(Name=language_name, WithDecryption=True)
            account_id = response['Parameter']['Value']
            statement2.add_arn_principal(f'arn:aws:iam::{account_id}:root')
        statement3.add_actions("SNS:Publish")
        statement3.add_service_principal("events.amazonaws.com")
        statement3.add_resources(topic.topic_arn)
        topic.add_to_resource_policy(statement3)

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0

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

        # create a new SNS topic
        topic = sns.Topic(self, "MyTopic")

        # create a new EventBridge rule
        rule = events.Rule(
            self,
            "MyRule",
            schedule=events.Schedule.cron(
                # minute="0",
                # hour="22",
                # week_day="FRI",
            ),
        )

        # add a target to the EventBridge rule to publish a message to the SNS topic
        rule.add_target(targets.SnsTopic(topic))

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
        statement1.add_condition("StringEquals", {"AWS:SourceOwner": "808326389482"})
        topic.add_to_resource_policy(statement1)

        statement2 = iam.PolicyStatement()
        # statement2.add_aws_account_principal('808326389482')
        # statement2.add_aws_account_principal('260778392212')
        statement2.add_arn_principal('arn:aws:iam::808326389482:root')
        statement2.add_arn_principal('arn:aws:iam::260778392212:root')
        statement2.add_actions("SNS:Subscribe")
        statement2.add_resources(topic.topic_arn)
        topic.add_to_resource_policy(statement2)

        statement3 = iam.PolicyStatement()
        # statement3.add_aws_account_principal('808326389482')
        # statement3.add_aws_account_principal('260778392212')
        statement3.add_arn_principal('arn:aws:iam::808326389482:root')
        statement3.add_arn_principal('arn:aws:iam::260778392212:root')
        statement3.add_actions("SNS:Publish")
        statement3.add_service_principal("events.amazonaws.com")
        statement3.add_resources(topic.topic_arn)
        topic.add_to_resource_policy(statement3)




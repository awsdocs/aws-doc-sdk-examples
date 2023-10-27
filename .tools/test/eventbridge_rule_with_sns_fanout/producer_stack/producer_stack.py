# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0

import yaml
from aws_cdk import Aws, CfnOutput, Duration, Size, Stack
from aws_cdk import aws_events as events
from aws_cdk import aws_events_targets as targets
from aws_cdk import aws_iam as iam
from aws_cdk import aws_s3 as s3
from aws_cdk import aws_sns as sns
from constructs import Construct



class ProducerStack(Stack):
    def __init__(self, scope: Construct, id: str, **kwargs) -> None:
        super().__init__(scope, id, **kwargs)
        acct_config = self.get_yaml_config("../config/targets.yaml")
        resource_config = self.get_yaml_config("../config/resources.yaml")
        topic_name = resource_config["topic_name"]
        bucket_name = resource_config["bucket_name"]
        topic = self.init_get_topic(topic_name)
        self.sns_permissions(topic)
        self.init_subscribe_permissions(topic, acct_config)
        self.init_publish_permissions(topic, acct_config)
        bucket = self.init_create_bucket(bucket_name)
        self.init_cross_account_log_role(acct_config, bucket)

    def get_yaml_config(self, filepath):
        with open(filepath, "r") as file:
            data = yaml.safe_load(file)
        return data

    def init_get_topic(self, topic_name):
        topic = sns.Topic(self, "fanout-topic", topic_name=topic_name)
        return topic

    def init_rule(self, topic):
        rule = events.Rule(
            self,
            "trigger-rule",
            schedule=events.Schedule.cron(
                minute="0",
                hour="22",
                week_day="FRI",
            ),
        )
        rule.add_target(targets.SnsTopic(topic))

    def sns_permissions(self, topic):
        # Set up base Amazon SNS permissions.
        sns_permissions = iam.PolicyStatement()
        sns_permissions.add_any_principal()
        sns_permissions.add_actions(
            "SNS:AddPermission",
            "SNS:DeleteTopic",
            "SNS:GetTopicAttributes",
            "SNS:ListSubscriptionsByTopic",
            "SNS:SetTopicAttributes",
            "SNS:Subscribe",
            "SNS:RemovePermission",
            "SNS:Publish",
        )
        sns_permissions.add_resources(topic.topic_arn)
        sns_permissions.add_condition(
            "StringEquals", {"AWS:SourceOwner": Aws.ACCOUNT_ID}
        )
        topic.add_to_resource_policy(sns_permissions)

    def init_subscribe_permissions(self, topic, target_accts):
        subscribe_permissions = iam.PolicyStatement()
        subscribe_permissions.add_arn_principal(f"arn:aws:iam::{Aws.ACCOUNT_ID}:root")
        for language in target_accts.keys():
            if "enabled" in str(target_accts[language]["status"]):
                subscribe_permissions.add_arn_principal(
                    f"arn:aws:iam::{str(target_accts[language]['account_id'])}:root"
                )
        subscribe_permissions.add_actions("SNS:Subscribe")
        subscribe_permissions.add_resources(topic.topic_arn)
        topic.add_to_resource_policy(subscribe_permissions)

    def init_publish_permissions(self, topic, target_accts):
        publish_permissions = iam.PolicyStatement()
        publish_permissions.add_arn_principal(f"arn:aws:iam::{Aws.ACCOUNT_ID}:root")
        for language in target_accts.keys():
            publish_permissions.add_arn_principal(
                f"arn:aws:iam::{str(target_accts[language]['account_id'])}:root"
            )
        publish_permissions.add_actions("SNS:Publish")
        publish_permissions.add_service_principal("events.amazonaws.com")
        publish_permissions.add_resources(topic.topic_arn)
        topic.add_to_resource_policy(publish_permissions)

    def init_create_bucket(self, bucket_name):
        bucket = s3.Bucket(
            self,
            bucket_name,
            bucket_name=bucket_name,
            versioned=False,
            block_public_access=s3.BlockPublicAccess.BLOCK_ALL
        )
        return bucket

    def init_cross_account_log_role(self, target_accts, bucket):
        languages = target_accts.keys()
        if len(languages) > 0:
            # Define policy that allows cross-account Amazon SNS and Amazon SQS access.
            statement = iam.PolicyStatement()
            statement.add_actions("s3:PutObject", "s3:PutObjectAcl")
            statement.add_resources(f"{bucket.bucket_arn}/*")
            for language in languages:
                if "enabled" in str(target_accts[language]["status"]):
                    statement.add_arn_principal(
                        f"arn:aws:iam::{str(target_accts[language]['account_id'])}:role/LogsLambdaExecutionRole"
                    )
            statement.add_arn_principal(f"arn:aws:iam::{Aws.ACCOUNT_ID}:root")
            bucket.add_to_resource_policy(statement)

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0

import boto3
import json
from aws_cdk import (
    aws_iam as iam,
    aws_s3 as s3,
    aws_kinesisfirehose as firehose,
    aws_logs as logs,
    Aws,
    Stack,
)
from constructs import Construct

class DataStack(Stack):
    def __init__(self, scope: Construct, id: str, **kwargs) -> None:
        super().__init__(scope, id, **kwargs)

        client = boto3.client('ssm')

        onboarded_languages = [
            'ruby',
            'javav2',
            'javascriptv3',
            'gov2',
            'python',
            'dotnetv3',
            'kotlin',
            'rust_dev_preview',
            'swift',
            'cpp',
            'gov2',
            'sap-abap'
        ]

        account_ids = []
        for language_name in onboarded_languages:
            response = client.get_parameter(Name=f'/account-mappings/{language_name}', WithDecryption=True)
            account_ids.append(response['Parameter']['Value'])

        # Create an S3 bucket with the specified name
        bucket = s3.Bucket(
            self,
            'MyS3Bucket2',
            bucket_name='firehose22-test-bucket1534645634564',
            versioned=False,  # If you want versioning, set this to True
            block_public_access=s3.BlockPublicAccess.BLOCK_ALL,  # Block public access
            # For us-east-1, there's no need to specify LocationConstraint
        )

        # Create an IAM role
        # Inline policy results in ValidationError (A PolicyStatement used in an identity-based policy must specify at least one resource.)
        # Instructions: Step 2c https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/CreateFirehoseStream.html
        firehose_to_s3_role = iam.Role(
            self,
            "FirehosetoS3Role",
            assumed_by=iam.ServicePrincipal("firehose.amazonaws.com"),
            # inline_policies={
            #     "MyCustomPolicy": iam.PolicyDocument(
            #         statements=[
            #             iam.PolicyStatement(
            #                 effect=iam.Effect.ALLOW,
            #                 actions=[
            #                     "sts:AssumeRole"
            #                 ],
            #                 conditions={
            #                     "StringEquals": {
            #                         "sts:ExternalId": "808326389482"
            #                     }
            #                 }
            #             )
            #         ]
            #     )
            # }
        )

        policy = iam.PolicyStatement(
            effect=iam.Effect.ALLOW,
            actions=[
                "s3:PutObject",
                "s3:PutObjectAcl",
                "s3:ListBucket"
            ],
            resources=[
                f"{bucket.bucket_arn}",
                f"{bucket.bucket_arn}/*"
            ]
        )

        firehose_to_s3_role.add_to_policy(policy)

        delivery_stream = firehose.CfnDeliveryStream(
            self,
            "CloudWatchKinesisDeliveryStream",
            s3_destination_configuration={
                "bucketArn": bucket.bucket_arn,
                "roleArn": firehose_to_s3_role.role_arn,
            }
        )

        # Set up cross-account Log permissions
        logging_permissions = iam.PolicyStatement()
        logging_permissions.add_actions("firehose:*")
        logging_permissions.add_resources(f"arn:aws:firehose:us-east-1:808326389482:*")

        # Create IAM Role
        role = iam.Role(
            self, "CWLtoFirehoseRole",
            role_name="CWLtoFirehoseRole",
            assumed_by=iam.ServicePrincipal("logs.amazonaws.com"),
            inline_policies={"PermissionsPolicyForCWL": iam.PolicyDocument(statements=[logging_permissions])}
        )

        # I think the below line is redundant
        # role.add_to_policy(logging_permissions)

        # Set up cross-account Log trust relationships for every onboarded language.
        logging_trust = iam.PolicyStatement()
        logging_trust.add_service_principal('logs.amazonaws.com')
        logging_trust.add_actions("sts:AssumeRole")
        logging_trust.add_source_arn_condition('arn:aws:logs:us-east-1:808326389482:*')
        for id in account_ids:
            logging_trust.add_condition("StringLike", {"aws:SourceArn": f"arn:aws:logs:us-east-1:{id}:*"})

        # role.add_to_policy(logging_trust)
        # Uncommenting above line results in the following ValidationErrors:
            # [DataStack / CWLtoKinesisRole / DefaultPolicy] A PolicyStatement used in an identity-based policy cannot specify any IAM principals
            # [DataStack / CWLtoKinesisRole / DefaultPolicy] A PolicyStatement used in an identity-based policy must specify at least one resource.

        policy = {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Sid": "",
                    "Effect": "Allow",
                    "Principal": {
                        "AWS": account_ids
                    },
                    "Action": "logs:PutSubscriptionFilter",
                    "Resource": "arn:aws:logs:us-east-1:808326389482:destination:FirehoseLogDestination"
                }
            ]
        }

        # Create CloudWatch Logs Destination
        destination = logs.CfnDestination(
            self, "CWLtoKinesisDestination",
            destination_name="LogDestination",
            target_arn=delivery_stream.attr_arn,
            role_arn=role.role_arn,
            destination_policy=json.dumps(policy)
        )
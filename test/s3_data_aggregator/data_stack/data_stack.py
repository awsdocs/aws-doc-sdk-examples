# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0

import boto3
from aws_cdk import (
    aws_iam as iam,
    aws_s3 as s3,
    Aws,
    Stack,
    CfnOutput,
    Duration,
    Size
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

        #####################################
        ##                                 ##
        ##           S3 BUCKET             ##
        ##    (Where all the logs go)      ##
        ##                                 ##
        #####################################

        bucket = s3.Bucket(
            self,
            'BigLogBucket',
            versioned=False,
            block_public_access=s3.BlockPublicAccess.BLOCK_ALL,
        )

        ####################################################
        ##                                                ##
        ##            CROSS-ACCOUNT S3 LOG ROLE           ##
        ##    (Processes logs and puts them to S3)        ##
        ##                                                ##
        ####################################################

        # Define policy that allows cross-account Amazon SNS and Amazon SQS access.
        statement = iam.PolicyStatement()
        for account_id in account_ids:
            statement.add_arn_principal(f"arn:aws:iam::{account_id}:role/LogsLambdaExecutionRole")
        statement.add_actions("s3:PutObject", "s3:PutObjectAcl")
        statement.add_resources(f"{bucket.bucket_arn}/*")
        bucket.add_to_resource_policy(statement)


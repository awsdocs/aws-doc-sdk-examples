# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0

import boto3
import json
from aws_cdk import (
    aws_iam as iam,
    aws_s3 as s3,
    aws_lambda as _lambda,
    aws_logs as logs,
    Aws,
    Stack,
    CfnOutput,
    Duration,
    Size
)
from constructs import Construct
import aws_cdk.aws_kinesisfirehose_destinations_alpha as destinations
import aws_cdk.aws_kinesisfirehose_alpha as firehose_alpha

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
            versioned=False,  # If you want versioning, set this to True
            block_public_access=s3.BlockPublicAccess.BLOCK_ALL,  # Block public access
            # For us-east-1, there's no need to specify LocationConstraint
        )

        #############################################
        ##                                         ##
        ##           PROCESSING LAMBDA             ##
        ##    (Unzipping and decoding the data)    ##
        ##                                         ##
        #############################################

        # Create a Lambda function to unzip and decode records
        unzip_decode_lambda = _lambda.Function(
            self,
            "UnzipDecodeLambda",
            runtime=_lambda.Runtime.PYTHON_3_8,
            handler="unzip_decode.handler",
            # Assuming you have a Python file named 'unzip_decode.py' with a 'handler' function
            code=_lambda.Code.from_asset("data_stack"),  # Directory containing your Lambda code
            timeout=Duration.seconds(60),  # Adjust as needed
        )

        # Grant necessary permissions to Lambda
        unzip_decode_lambda.add_to_role_policy(
            iam.PolicyStatement(
                actions=["s3:GetObject"],
                resources=[bucket.bucket_arn + "/*"],
            )
        )

        #####################################################################################################
        ##                                                                                                 ##
        ##                                   IAM ROLE FOR FIREHOSE                                         ##
        ##           (Grants Kinesis Data Firehose permission to put data into the bucket)                 ##
        ##                                                                                                 ##
        #####################################################################################################

        principal_with_conditions = iam.ServicePrincipal(
            service="firehose.amazonaws.com",
            conditions={"StringEquals": {"sts:ExternalId": f"808326389482"}}
        )
        firehose_to_s3_role = iam.Role(
            self,
            "FirehosetoS3Role",
            assumed_by=principal_with_conditions
        )
        permissions_policy = iam.PolicyStatement(
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
        firehose_to_s3_role.add_to_policy(permissions_policy)

        #####################################
        ##                                 ##
        ##     KINESIS DELIVERY STREAM     ##
        ##                                 ##
        #####################################

        # Create a Kinesis Firehose delivery stream
        lambda_processor = firehose_alpha.LambdaFunctionProcessor(unzip_decode_lambda,
                                                            buffer_interval=Duration.minutes(5),
                                                            buffer_size=Size.mebibytes(1),
                                                            retries=5
                                                            )
        s3_destination = destinations.S3Bucket(bucket,
                                               processor=lambda_processor,
                                               # dataOutputPrefix=
                                               )
        delivery_stream = firehose_alpha.DeliveryStream(self, "Delivery Stream",
                                destinations=[s3_destination]
                                )

        #####################################################################################################
        ##                                                                                                 ##
        ##                                   IAM ROLE FOR CLOUDWATCH                                       ##
        ##       (Grants Batch logs the permission to put data into your Kinesis Data Firehose stream)     ##
        ##                                                                                                 ##
        #####################################################################################################

        log_origins = [f"arn:aws:logs:us-east-1:{Aws.ACCOUNT_ID}:*"]
        for id in account_ids:
            log_origins.append(f"arn:aws:logs:us-east-1:{id}:*")

        # Create Principal required for IAM role creation
        principal_with_conditions = iam.ServicePrincipal(
            service="logs.amazonaws.com",
            conditions={"StringLike": {"aws:SourceArn": log_origins}}
        )
        # Create IAM Role
        cloudwatch_role = iam.Role(
            self, "CWLtoFirehoseRole",
            role_name="CWLtoFirehoseRole",
            assumed_by=principal_with_conditions,
        )

        # Set up cross-account Log permissions
        cloudwatch_permissions = iam.PolicyStatement()
        cloudwatch_permissions.add_actions("firehose:*")
        cloudwatch_permissions.add_resources(f"arn:aws:firehose:us-east-1:{Aws.ACCOUNT_ID}:*")
        cloudwatch_role.add_to_policy(cloudwatch_permissions)

        ###################################################
        ##                                               ##
        ##                  DESTINATION                  ##
        ##  (The endpoint where Firehose delivers logs)  ##
        ##                                               ##
        ###################################################

        destination_name = 'FirehoseDestination'

        destination_policy = {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Sid": "",
                    "Effect": "Allow",
                    "Principal": {
                        "AWS": account_ids
                    },
                    "Action": "logs:PutSubscriptionFilter",
                    "Resource": f"arn:aws:logs:us-east-1:{Aws.ACCOUNT_ID}:destination:{destination_name}"
                }
            ]
        }

        # Create CloudWatch Logs Destination
        destination = logs.CfnDestination(
            self, destination_name,
            destination_name=destination_name,
            target_arn=delivery_stream.delivery_stream_arn,
            role_arn=cloudwatch_role.role_arn,
            destination_policy=json.dumps(destination_policy)
        )

        CfnOutput(self, "Destination", value=destination.attr_arn)

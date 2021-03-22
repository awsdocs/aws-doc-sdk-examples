#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// TODO: Customize this AWS Cloud Development Kit (AWS CDK) code for your
// specific AWS CDK solution. For more information, see the
// AWS CDK Developer Guide at
// https://docs.aws.amazon.com/cdk/latest/guide

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import { CfnOutput } from "@aws-cdk/core";
import * as s3 from '@aws-cdk/aws-s3';
import * as dynamodb from '@aws-cdk/aws-dynamodb';
import * as iam from '@aws-cdk/aws-iam';
import {Effect, PolicyStatement, ServicePrincipal} from '@aws-cdk/aws-iam';
import { AwsCustomResource } from '@aws-cdk/custom-resources';

export class SetupStackLambda extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

      const myBucket = new s3.Bucket(this, 'mybucket      ',{
          removalPolicy: cdk.RemovalPolicy.DESTROY,
          blockPublicAccess:  {
              blockPublicAcls: true,
              blockPublicPolicy: false,
              ignorePublicAcls: true,
              restrictPublicBuckets: false
          }
      });
      myBucket.grantPublicAccess('*', 's3:GetObject');
      myBucket.grantPublicAccess('*', 's3:PutObject');

    const myRole = new iam.Role(this, "myLambdaRole", {
      assumedBy: new ServicePrincipal("lambda.amazonaws.com")
    });
    myRole.addToPolicy(
        new PolicyStatement({
          effect: Effect.ALLOW,
          actions: [
            "logs:CreateLogGroup",
            "logs:CreateLogtream",
            "logs:PutLogEvents"
          ],
          resources: ["*"],
        })
    );
    myRole.addToPolicy(
        new PolicyStatement({
              effect: Effect.ALLOW,
              actions: ["sns:*"],
              resources: ["*"]
            }
        ));
    myRole.addToPolicy(
        new PolicyStatement({
              effect: Effect.ALLOW,
              actions: ["dynamodb:*",
                "dax:*",
                "application-autoscaling:DeleteScalingPolicy",
                "application-autoscaling:DeregisterScalableTarget",
                "application-autoscaling:DescribeScalableTargets",
                "application-autoscaling:DescribeScalingActivities",
                "application-autoscaling:DescribeScalingPolicies",
                "application-autoscaling:PutScalingPolicy",
                "application-autoscaling:RegisterScalableTarget",
                "cloudwatch:DeleteAlarms",
                "cloudwatch:DescribeAlarmHistory",
                "cloudwatch:DescribeAlarms",
                "cloudwatch:DescribeAlarmsForMetric",
                "cloudwatch:GetMetricStatistics",
                "cloudwatch:ListMetrics",
                "cloudwatch:PutMetricAlarm",
                "cloudwatch:GetMetricData",
                "datapipeline:ActivatePipeline",
                "datapipeline:CreatePipeline",
                "datapipeline:DeletePipeline",
                "datapipeline:DescribeObjects",
                "datapipeline:DescribePipelines",
                "datapipeline:GetPipelineDefinition",
                "datapipeline:ListPipelines",
                "datapipeline:PutPipelineDefinition",
                "datapipeline:QueryObjects",
                "ec2:DescribeVpcs",
                "ec2:DescribeSubnets",
                "ec2:DescribeSecurityGroups",
                "iam:GetRole",
                "iam:ListRoles",
                "kms:DescribeKey",
                "kms:ListAliases",
                "sns:CreateTopic",
                "sns:DeleteTopic",
                "sns:ListSubscriptions",
                "sns:ListSubscriptionsByTopic",
                "sns:ListTopics",
                "sns:Subscribe",
                "sns:Unsubscribe",
                "sns:SetTopicAttributes",
                "lambda:CreateFunction",
                "lambda:ListFunctions",
                "lambda:ListEventSourceMappings",
                "lambda:CreateEventSourceMapping",
                "lambda:DeleteEventSourceMapping",
                "lambda:GetFunctionConfiguration",
                "lambda:DeleteFunction",
                "resource-groups:ListGroups",
                "resource-groups:ListGroupResources",
                "resource-groups:GetGroup",
                "resource-groups:GetGroupQuery",
                "resource-groups:DeleteGroup",
                "resource-groups:CreateGroup",
                "tag:GetResources",
                "kinesis:ListStreams",
                "kinesis:DescribeStream",
                "kinesis:DescribeStreamSummary",
              ],
              resources: ["*"]
            }
        ));
    myRole.addToPolicy(
        new PolicyStatement({
              effect: Effect.ALLOW,
              actions: ["cloudwatch:GetInsightRuleReport"
              ],
              resources: ["arn:aws:cloudwatch:*:*:insight-rule/DynamoDBContributorInsights*"]
            }
        ))
    myRole.addToPolicy(
        new PolicyStatement({
              effect: Effect.ALLOW,
              actions: ["iam:PassRole"
              ],
              resources: ["*"],
              conditions: {
                "StringLike": {
                  "iam:PassedToService": [
                    "application-autoscaling.amazonaws.com",
                    "application-autoscaling.amazonaws.com.cn",
                    "dax.amazonaws.com"
                  ]
                }
              }
            }
        ))
    myRole.addToPolicy(
        new PolicyStatement({
              effect: Effect.ALLOW,
              actions: ["iam:CreateServiceLinkedRole"
              ],
              resources: ["*"],
              conditions: {
                "StringEquals": {
                  "iam:AWSServiceName": [
                    "replication.dynamodb.amazonaws.com",
                    "dax.amazonaws.com",
                    "dynamodb.application-autoscaling.amazonaws.com",
                    "contributorinsights.dynamodb.amazonaws.com",
                    "kinesisreplication.dynamodb.amazonaws.com"
                  ]
                }
              }
            }
        ))

    const table = new dynamodb.Table(this, 'table', {
      tableName: 'Employees',
      partitionKey: {
        name: 'id',
        type: dynamodb.AttributeType.NUMBER
      },
      sortKey: {
        name: 'firstName',
        type: dynamodb.AttributeType.STRING
      },
      readCapacity: 10,
      writeCapacity: 10,
      removalPolicy: cdk.RemovalPolicy.DESTROY
    });
    // Add global secondary index to the table.
    table.addGlobalSecondaryIndex({
      indexName: 'FirstNameIndex',
      partitionKey: {
        name: 'FirstName',
        type: dynamodb.AttributeType.STRING
      },
      projectionType: dynamodb.ProjectionType.ALL,
      readCapacity: 5,
      writeCapacity: 10
    });
    new CfnOutput(this, 'TableName', {value: table.tableName}),
    new CfnOutput(this, 'Bucket name', {value: myBucket.bucketName})
    new CfnOutput(this, 'IAM role', {value: myRole.roleName})
  }
}







const app = new cdk.App();
new SetupStackLambda(app, 'SetupStackLambda');


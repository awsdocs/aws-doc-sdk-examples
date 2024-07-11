#!/usr/bin/env node
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose
//
// Defines an AWS CloudFormation stack that creates resource for an IoT rule:
// 1. An SNS topic.
// 2. An IAM Role for an IoT rule.
//
// This stack is used by the IoT workflow.


import {CfnOutput, Stack, StackProps, App} from 'aws-cdk-lib';
import {Construct} from "constructs"
import {Effect, PolicyStatement, Role, ServicePrincipal} from 'aws-cdk-lib/aws-iam';
import {Topic} from "aws-cdk-lib/aws-sns";



export class SetupStack extends Stack {
    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);

        const snsTopic = new Topic(this, "iot-workflow-topic");

        const service_principal = new ServicePrincipal('iot.amazonaws.com');

        const rule_role = new Role(this, 'doc-example-import-role', {
            assumedBy: service_principal,
        });

        const policy_statement = new PolicyStatement({
            effect: Effect.ALLOW,
            actions: [
                'dynamodb:*'
            ]
        });
        policy_statement.addAllResources();

        rule_role.addToPolicy(policy_statement);

        new CfnOutput(this, 'SNSTopicArn', {value: snsTopic.topicArn});
        new CfnOutput(this, 'RoleArn', {value: rule_role.roleArn});
     }
}

const stackName = 'doc-example-iot-workflow-stack'

const app = new App();

new SetupStack(app, stackName);

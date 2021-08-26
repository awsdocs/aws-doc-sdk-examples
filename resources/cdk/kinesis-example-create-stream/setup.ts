#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose: This AWS Cloud Development Kit (AWS CDK) app
// creates the following AWS resources:
//
// This AWS CDK app creates the following resources:
//
// - An Amazon Kinesis stream.
// - An Amazon Cognito identity pool with access enabled for unauthenticated identities.
// - An AWS Identity and Access Management role whose policy grants permission to submit data to an Amazon Kinesis stream.

// For instructions on how to run any of these apps, see
// https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/resources/cdk/README.md#Running-a-CDK-app

import "source-map-support/register";
import * as cdk from "@aws-cdk/core";
import { CfnOutput } from "@aws-cdk/core";
import * as cognito from "@aws-cdk/aws-cognito";
import * as iam from "@aws-cdk/aws-iam";
import { Effect, PolicyStatement } from "@aws-cdk/aws-iam";
import {Stream} from "@aws-cdk/aws-kinesis";

export class SetupStackCreateTable extends cdk.Stack {
    constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
        super(scope, id, props);

        const myStream =   new Stream(this, "MyFirstStream", {
            streamName: 'my-stream-kinesis'
        });


        const myIdentityPool = new cognito.CfnIdentityPool(
            this,
            "ExampleIdentityPool",
            {
                allowUnauthenticatedIdentities: true,
            }
        );

        const unauthenticatedRole = new iam.Role(
            this,
            "CognitoDefaultUnauthenticatedRole",
            {
                assumedBy: new iam.FederatedPrincipal(
                    "cognito-identity.amazonaws.com",
                    {
                        StringEquals: {
                            "cognito-identity.amazonaws.com:aud": myIdentityPool.ref,
                        },
                        "ForAnyValue:StringLike": {
                            "cognito-identity.amazonaws.com:amr": "unauthenticated",
                        },
                    },
                    "sts:AssumeRoleWithWebIdentity"
                ),
            }
        );

        unauthenticatedRole.addToPolicy(
            new PolicyStatement({
                effect: Effect.ALLOW,
                actions: ["mobileanalytics:PutEvents", "cognito-sync:*"],
                resources: ["*"],
            }));

        unauthenticatedRole.addToPolicy(
            new PolicyStatement({
                effect: Effect.ALLOW,
                actions: ["kinesis:Put*"],
                resources: [myStream.streamArn],
            }));

        const defaultPolicy = new cognito.CfnIdentityPoolRoleAttachment(
            this,
            "DefaultValid",
            {
                identityPoolId: myIdentityPool.ref,
                roles: {
                    unauthenticated: unauthenticatedRole.roleArn,
                },
            }
        );
        new CfnOutput(this, 'Identity pool id', {value: myIdentityPool.ref})
        new CfnOutput(this, 'Stack name', {value: myStream.streamName})
        new CfnOutput(this, 'Stack arn', {value: myStream.streamArn})
    }
}

const stackName = "SetupStackCreateTable";

const app = new cdk.App();

new SetupStackCreateTable(app, stackName);

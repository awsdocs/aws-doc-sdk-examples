#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import { CfnOutput } from "@aws-cdk/core";
import * as cognito from '@aws-cdk/aws-cognito';
import * as iam from '@aws-cdk/aws-iam';
import { Effect, PolicyStatement } from "@aws-cdk/aws-iam";

export class stepfunctions extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const myIdentityPool = new cognito.CfnIdentityPool(
        this,
        "ExampleIdentityPool",
        {
          allowUnauthenticatedIdentities: true,
        }
    );

    const lambda_support_role = new iam.Role(this, 'LambdaRole', ({
      assumedBy: new iam.FederatedPrincipal('cognito-identity.amazonaws.com', {
        "StringEquals": {"cognito-identity.amazonaws.com:aud": myIdentityPool.ref},
        "ForAnyValue:StringLike": {"cognito-identity.amazonaws.com:amr": "unauthenticated"},
      }, "sts:AssumeRoleWithWebIdentity")
    }));

    lambda_support_role.assumeRolePolicy?.addStatements(
        new iam.PolicyStatement({
          effect: iam.Effect.ALLOW,
          principals: [new iam.ServicePrincipal('lambda.amazonaws.com')],
          actions: ['sts:AssumeRole'],
        }),
    )

    lambda_support_role.addToPolicy(
        new PolicyStatement({
          effect: Effect.ALLOW,
          actions: [
            "lambda:InvokeFunction",
            "mobileanalytics:PutEvents",
            "cognito-sync:*",
          ],
          resources: ["*"],
        })
    );
    lambda_support_role.addToPolicy(
        new PolicyStatement({
          effect: Effect.ALLOW,
          actions: ["dynamodb:*"],
          resources: ["*"],
        })
    );
    lambda_support_role.addToPolicy(
        new PolicyStatement({
          effect: Effect.ALLOW,
          actions: ["ses:*"],
          resources: ["*"],
        })
    );
    const defaultPolicy = new cognito.CfnIdentityPoolRoleAttachment(
        this,
        "DefaultValid",
        {
          identityPoolId: myIdentityPool.ref,
          roles: {
            unauthenticated: lambda_support_role.roleArn,
          },
        }
    );
    const myIdentityPool_workflow = new cognito.CfnIdentityPool(
        this,
        "ExampleIdentityPoolWorkflow",
        {
          allowUnauthenticatedIdentities: true,
        }
    );
    const workflow_support_role = new iam.Role(this, 'WorkflowRole', ({
      assumedBy: new iam.FederatedPrincipal('cognito-identity.amazonaws.com', {
        "StringEquals": {"cognito-identity.amazonaws.com:aud": myIdentityPool_workflow.ref},
        "ForAnyValue:StringLike": {"cognito-identity.amazonaws.com:amr": "unauthenticated"},
      }, "sts:AssumeRoleWithWebIdentity")
    }));
    workflow_support_role.assumeRolePolicy?.addStatements(
        new iam.PolicyStatement({
          effect: iam.Effect.ALLOW,
          principals: [new iam.ServicePrincipal('states.amazonaws.com')],
          actions: ['sts:AssumeRole'],
        }),
    )

    workflow_support_role.addToPolicy(
        new PolicyStatement({
          effect: Effect.ALLOW,
          actions: [
            "lambda:InvokeFunction",
            "mobileanalytics:PutEvents",
            "cognito-sync:*",
          ],
          resources: ["*"],
        })
    );

    const defaultPolicyworkflow = new cognito.CfnIdentityPoolRoleAttachment(
        this,
        "DefaultValidWorkflow",
        {
          identityPoolId: myIdentityPool_workflow.ref,
          roles: {
            unauthenticated: workflow_support_role.roleArn,
          },
        }
    );
    new CfnOutput(this, "Lambda support role", { value: lambda_support_role.roleName });
    new CfnOutput(this, "Workflow support role", { value: workflow_support_role.roleName });
  }
}

const app = new cdk.App();
new stepfunctions(app, 'stepfunctions');

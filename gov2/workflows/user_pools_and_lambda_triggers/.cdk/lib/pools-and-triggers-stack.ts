// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { PoolsAndTriggersBase } from './pools-and-triggers-base';
import * as cdk from 'aws-cdk-lib';
import {CfnOutput} from 'aws-cdk-lib';
import {Construct} from 'constructs';
import * as iam from 'aws-cdk-lib/aws-iam';
import {GoFunction} from "@aws-cdk/aws-lambda-go-alpha";


export class PoolsAndTriggersStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const baseConstruct = new PoolsAndTriggersBase(this, "PoolsAndTriggersBase");

    const cognitoPrincipal = new iam.ServicePrincipal("cognito-idp.amazonaws.com");

    const autoConfirmFunction = this.createFunction('autoConfirmHandler', '../handlers/auto_confirm',
      baseConstruct.tableName, baseConstruct.lambdaRole, cognitoPrincipal);
    const migrateUserFunction = this.createFunction('migrateUserHandler', '../handlers/migrate_user',
      baseConstruct.tableName, baseConstruct.lambdaRole, cognitoPrincipal);
    const activityLogFunction = this.createFunction('activityLogHandler', '../handlers/activity_log',
      baseConstruct.tableName, baseConstruct.lambdaRole, cognitoPrincipal);

    new CfnOutput(this, "AutoConfirmFunction", {value: autoConfirmFunction.functionName})
    new CfnOutput(this, "AutoConfirmFunctionArn", {value: autoConfirmFunction.functionArn})
    new CfnOutput(this, "MigrateUserFunction", {value: migrateUserFunction.functionName})
    new CfnOutput(this, "MigrateUserFunctionArn", {value: migrateUserFunction.functionArn})
    new CfnOutput(this, "ActivityLogFunction", {value: activityLogFunction.functionName})
    new CfnOutput(this, "ActivityLogFunctionArn", {value: activityLogFunction.functionArn})
    baseConstruct.outputs(this)
  }

  createFunction(name: string, path: string, tableName: string, lambdaRole: iam.IRole, principal: iam.IGrantable) {
    const fn = new GoFunction(this, name, {
      entry: path,
      environment: {
        "TABLE_NAME": tableName
      },
      role: lambdaRole,
      moduleDir: '..',
    });
    fn.grantInvoke(principal)
    return fn
  }
}

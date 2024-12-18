// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import type { StackProps } from "aws-cdk-lib";
import { CfnOutput, Stack } from "aws-cdk-lib";
import { ServicePrincipal } from "aws-cdk-lib/aws-iam";
import { Runtime } from "aws-cdk-lib/aws-lambda";
import { NodejsFunction } from "aws-cdk-lib/aws-lambda-nodejs";
import type { Construct } from "constructs";

import { PoolsAndTriggersBase } from "./base";

export class PoolsAndTriggersStack extends Stack {
  functions: Record<string, NodejsFunction>;
  poolsAndTriggersBase = new PoolsAndTriggersBase(this, "PoolsAndTriggersBase");
  cognitoPrincipal = new ServicePrincipal("cognito-idp.amazonaws.com");

  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);
    this.createFunction("autoConfirmHandler");
    this.poolsAndTriggersBase.outputs(this);
  }

  createFunction(name: string): NodejsFunction {
    const fn = new NodejsFunction(this, name, {
      // An "entry" property is optional. By default, NodejsFunction
      // will look for a file named `${this.stackName}.${name}.ts`
      // entry: "",
      environment: {
        TABLE_NAME: this.poolsAndTriggersBase.tableName,
      },
      role: this.poolsAndTriggersBase.lambdaRole,
      runtime: Runtime.NODEJS_20_X,
    });
    fn.grantInvoke(this.cognitoPrincipal);
    new CfnOutput(this, `${name}Name`, { value: fn.functionName });
    new CfnOutput(this, `${name}Arn`, { value: fn.functionArn });
    return fn;
  }
}

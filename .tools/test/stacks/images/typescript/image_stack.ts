// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import "source-map-support/register";
import * as cdk from "aws-cdk-lib";
import { Construct } from "constructs";
import { readAccountConfig } from "./../../config/targets";

class ImageStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const acctConfig = readAccountConfig("../../config/targets.yaml");

    for (const language of Object.keys(acctConfig)) {
      if (acctConfig[language].status === "enabled") {
        new cdk.ecr.Repository(this, `${language}-examples`, {
          repositoryName: `${language}`,
          imageScanOnPush: true,
          removalPolicy: cdk.RemovalPolicy.RETAIN,
        });
      }
    }
  }
}

const app = new cdk.App();

new ImageStack(app, "ImageStack", {
  env: {
    account: cdk.process.env.CDK_DEFAULT_ACCOUNT!,
    region: cdk.process.env.CDK_DEFAULT_REGION!,
  },
  terminationProtection: true
});

app.synth();

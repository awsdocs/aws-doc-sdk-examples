// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import "source-map-support/register";
import * as cdk from "aws-cdk-lib";
import { Stack, StackProps, aws_ecr as ecr, RemovalPolicy } from "aws-cdk-lib";
import { type Construct } from "constructs";
import { readAccountConfig } from "./../../config/types";

class ImageStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const acctConfig = readAccountConfig("../../config/targets.yaml");

    for (const language of Object.keys(acctConfig)) {
      if (acctConfig[language].status === "enabled") {
        new ecr.Repository(this, `${language}-examples`, {
          repositoryName: `${language}`,
          imageScanOnPush: true,
          removalPolicy: RemovalPolicy.RETAIN,
        });
      }
    }
  }
}

const app = new cdk.App();

new ImageStack(app, "ImageStack", {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT!,
    region: process.env.CDK_DEFAULT_REGION!,
  },
});

app.synth();

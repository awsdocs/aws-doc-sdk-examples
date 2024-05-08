#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import "source-map-support/register";
import * as cdk from "aws-cdk-lib";
import { Stack, StackProps, aws_ecr as ecr, RemovalPolicy } from "aws-cdk-lib";
import * as fs from "fs";
import { parse } from "yaml";
import { type Construct } from "constructs";

class ImageStack extends Stack {
  private readonly adminAccountId: string;

  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    interface TargetAccount {
      account_id: string;
      status: string;
      memory: string;
      vcpus: string;
    }

    interface ResourceConfig {
      admin_acct: string;
      topic_name: string;
      bucket_name: string;
      aws_region: string;
    }

    const acctConfig = this.getYamlConfig<Record<string, TargetAccount>>(
      "../../config/targets.yaml",
      this.isTargetAccount,
    );
    const resourceConfig = this.getYamlConfig<ResourceConfig>(
      "../../config/resources.yaml",
      this.isResourceConfig,
    );

    this.adminAccountId = resourceConfig.admin_acct;

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

  private isTargetAccount(acct: any): acct is TargetAccount {
    return (
      typeof acct.account_id === "string" && typeof acct.status === "string"
    );
  }

  private isResourceConfig(config: any): config is ResourceConfig {
    return (
      typeof config.admin_acct === "string" &&
      typeof config.topic_name === "string" &&
      typeof config.bucket_name === "string" &&
      typeof config.aws_region === "string"
    );
  }

  private getYamlConfig<T>(
    filepath: string,
    validator: (obj: any) => obj is T,
  ): T {
    const fileContents = fs.readFileSync(filepath, "utf8");
    const config = parse(fileContents);
    if (!validator(config)) {
      throw new Error(
        `Configuration at ${filepath} does not match expected format.`,
      );
    }
    return config;
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

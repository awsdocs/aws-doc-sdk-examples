#!/usr/bin/env node
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

    const resourceConfig = this.loadYamlConfig("../../.config/resources.yaml");
    this.adminAccountId = resourceConfig.admin_acct;

    const acctConfig = this.loadYamlConfig("../../.config/targets.yaml");
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

  private loadYamlConfig(filePath: string): Record<string, any> {
    try {
      const fileContent = fs.readFileSync(filePath, "utf8");
      return parse(fileContent) as Record<string, any>;
    } catch (error) {
      console.error(`Failed to read or parse YAML file at ${filePath}:`, {error});
      return {};
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

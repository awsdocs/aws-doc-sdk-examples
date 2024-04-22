#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import * as fs from 'fs';
import * as yaml from 'js-yaml';
import { Stack, aws_ecr as ecr, RemovalPolicy, aws_iam as iam } from 'aws-cdk-lib';
import { type Construct } from 'constructs';

class ImageStack extends Stack {
  private readonly adminAccountId: string;

  constructor(scope: Construct, id: string, props?: { env: { region: string; account: string } }) {
    super(scope, id, props);

    const resourceConfig = this.loadYamlConfig('../../config/resources.yaml');
    this.adminAccountId = resourceConfig.admin_acct;

    const acctConfig = this.loadYamlConfig('../../config/targets.yaml');
    for (const language of Object.keys(acctConfig)) {
      if (acctConfig[language].status === "enabled") {
        const repository = new ecr.Repository(this, `${language}-examples`, {
          repositoryName: `${language}`,
          imageScanOnPush: true,
          removalPolicy: RemovalPolicy.RETAIN,
        });
      }
    }
  }

  private loadYamlConfig(filePath: string): Record<string, any> {
    try {
      const fileContent = fs.readFileSync(filePath, 'utf8');
      return yaml.load(fileContent) as Record<string, any>;
    } catch (error) {
      console.error(`Failed to read or parse YAML file at ${filePath}:`, error);
      return {};
    }
  }
}

const app = new cdk.App();

new ImageStack(app, 'ImageStack', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT!,
    region: process.env.CDK_DEFAULT_REGION!,
  },
});

app.synth();

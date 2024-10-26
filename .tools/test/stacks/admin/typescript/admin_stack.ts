// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { Stack, StackProps } from "aws-cdk-lib";
import * as cdk from "aws-cdk-lib";
import { Construct } from "constructs";
import * as events from "aws-cdk-lib/aws-events";
import * as targets from "aws-cdk-lib/aws-events-targets";
import * as iam from "aws-cdk-lib/aws-iam";
import * as s3 from "aws-cdk-lib/aws-s3";
import * as sns from "aws-cdk-lib/aws-sns";
import { readAccountConfig } from "../../config/types";
import { readResourceConfig } from "../../config/resources";

class AdminStack extends Stack {
  private adminAccountId: string;

  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const acctConfig = readAccountConfig("../../config/targets.yaml");
    const resourceConfig = readResourceConfig("../../config/resources.yaml");
    const adminTopicName = resourceConfig["topic_name"];
    const adminBucketName = resourceConfig["bucket_name"];
    this.adminAccountId = resourceConfig["admin_acct"];

    const adminTopic = this.initGetTopic(adminTopicName);
    this.snsPermissions(adminTopic);
    this.initSubscribePermissions(adminTopic, acctConfig);
    this.initPublishPermissions(adminTopic, acctConfig);

    const bucket = this.initCreateBucket(adminBucketName);
    this.initCrossAccountLogRole(acctConfig, bucket);
    this.initRule(adminTopic);
  }

  private initGetTopic(topicName: string): sns.Topic {
    return new sns.Topic(this, "fanout-topic", {
      topicName: topicName,
    });
  }

  private initRule(topic: sns.Topic): void {
    const rule = new events.Rule(this, "trigger-rule", {
      schedule: events.Schedule.cron({ minute: "0", hour: "0" }),
    });
    rule.addTarget(new targets.SnsTopic(topic));
  }

  private snsPermissions(topic: sns.Topic): void {
    const snsPermissions = new iam.PolicyStatement({
      actions: [
        "SNS:AddPermission",
        "SNS:DeleteTopic",
        "SNS:GetTopicAttributes",
        "SNS:ListSubscriptionsByTopic",
        "SNS:Publish",
        "SNS:RemovePermission",
        "SNS:SetTopicAttributes",
        "SNS:Subscribe",
      ],
      principals: [new iam.AnyPrincipal()],
      resources: [topic.topicArn],
      conditions: {
        StringEquals: { "AWS:SourceOwner": this.adminAccountId },
      },
    });
    topic.addToResourcePolicy(snsPermissions);
  }

  private initSubscribePermissions(
    topic: sns.Topic,
    targetAccts: Record<string, any>,
  ): void {
    const subscribePermissions = new iam.PolicyStatement({
      actions: ["SNS:Subscribe"],
      resources: [topic.topicArn],
    });
    Object.keys(targetAccts).forEach((language) => {
      subscribePermissions.addPrincipals(
        new iam.ArnPrincipal(
          `arn:aws:iam::${targetAccts[language]["account_id"]}:root`,
        ),
      );
    });
    topic.addToResourcePolicy(subscribePermissions);
  }

  private initPublishPermissions(
    topic: sns.Topic,
    targetAccts: Record<string, any>,
  ): void {
    const publishPermissions = new iam.PolicyStatement({
      actions: ["SNS:Publish"],
      resources: [topic.topicArn],
    });
    Object.keys(targetAccts).forEach((language) => {
      publishPermissions.addPrincipals(
        new iam.ArnPrincipal(
          `arn:aws:iam::${targetAccts[language]["account_id"]}:root`,
        ),
      );
    });
    publishPermissions.addServicePrincipal("events.amazonaws.com");
    topic.addToResourcePolicy(publishPermissions);
  }

  private initCreateBucket(bucketName: string): s3.Bucket {
    return new s3.Bucket(this, bucketName, {
      bucketName: bucketName,
      versioned: false,
      blockPublicAccess: s3.BlockPublicAccess.BLOCK_ALL,
    });
  }

  private initCrossAccountLogRole(
    targetAccts: Record<string, any>,
    bucket: s3.Bucket,
  ): void {
    const logExportPermissions = new iam.PolicyStatement({
      actions: [
        "s3:PutObject",
        "s3:PutObjectAcl",
        "s3:DeleteObject",
        "s3:ListBucket",
        "s3:GetObject",
      ],
      resources: [`${bucket.bucketArn}/*`, bucket.bucketArn],
    });

    let addPolicy = false;
    Object.keys(targetAccts).forEach((language) => {
      if (targetAccts[language].status === "enabled") {
        logExportPermissions.addPrincipals(
          new iam.ArnPrincipal(
            `arn:aws:iam::${targetAccts[language].account_id}:role/CloudWatchExecutionRole`,
          ),
        );
        addPolicy = true;
      }
    });
    if (addPolicy) {
      bucket.addToResourcePolicy(logExportPermissions);
    }
  }
}

const app = new cdk.App();

new AdminStack(app, "AdminStack", {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION,
  },
});

app.synth();

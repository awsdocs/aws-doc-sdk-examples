// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import * as cdk from "aws-cdk-lib";
import type { Construct } from "constructs";
import * as kinesis from "aws-cdk-lib/aws-kinesis";

export class KinesisCdkStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const stream = new kinesis.Stream(this, "ExampleStream", {
      streamName: "example-stream",
      encryption: kinesis.StreamEncryption.KMS,
      removalPolicy: cdk.RemovalPolicy.DESTROY,
    });

    new cdk.CfnOutput(this, "ExampleStreamName", {
      key: "ExampleStreamName",
      value: stream.streamName,
    });
    new cdk.CfnOutput(this, "ExampleStreamArn", {
      key: "ExampleStreamArn",
      value: stream.streamArn,
    });
  }
}

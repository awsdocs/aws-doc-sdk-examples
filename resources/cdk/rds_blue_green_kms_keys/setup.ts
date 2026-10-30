#!/usr/bin/env node
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose
//
// Defines an AWS CloudFormation stack that creates the following resources for the
// Amazon RDS Blue/Green Deployment KMS encryption key migration example:
//
//  * An original AWS Key Management Service (AWS KMS) customer managed key, used to
//    encrypt the source (blue) Amazon RDS DB instance.
//  * A target AWS KMS customer managed key, used to encrypt the green DB instance
//    that the Blue/Green Deployment creates.
//
// Both keys use the default key policy, which grants the account root principal full
// access and delegates permissions to IAM. Amazon RDS uses the keys through grants it
// creates on behalf of the caller. When the stack is deleted, the keys are scheduled
// for deletion after a 7-day pending window.
//

import "source-map-support/register";
import { Construct } from "constructs";
import {
  App,
  CfnOutput,
  DefaultStackSynthesizer,
  Duration,
  RemovalPolicy,
  Stack,
  StackProps,
} from "aws-cdk-lib";
import { Key } from "aws-cdk-lib/aws-kms";

export class RdsBlueGreenKmsKeysStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const originalKey = new Key(this, "OriginalKmsKey", {
      description:
        "Original KMS key used to encrypt the source RDS DB instance in the Blue/Green encryption migration example.",
      enableKeyRotation: true,
      removalPolicy: RemovalPolicy.DESTROY,
      pendingWindow: Duration.days(7),
    });

    const targetKey = new Key(this, "TargetKmsKey", {
      description:
        "Target KMS key used to encrypt the green RDS DB instance in the Blue/Green encryption migration example.",
      enableKeyRotation: true,
      removalPolicy: RemovalPolicy.DESTROY,
      pendingWindow: Duration.days(7),
    });

    new CfnOutput(this, "OriginalKmsKeyArn", {
      value: originalKey.keyArn,
      description: "ARN of the original KMS key for the source RDS DB instance.",
    });
    new CfnOutput(this, "TargetKmsKeyArn", {
      value: targetKey.keyArn,
      description: "ARN of the target KMS key for the green RDS DB instance.",
    });
  }
}

const stackName = "RdsBlueGreenKmsKeysStack";

const app = new App();

// Skip the bootstrap version rule so the synthesized template can be deployed
// with a plain CloudFormation CreateStack call.
new RdsBlueGreenKmsKeysStack(app, stackName, {
  synthesizer: new DefaultStackSynthesizer({
    generateBootstrapVersionRule: false,
  }),
});

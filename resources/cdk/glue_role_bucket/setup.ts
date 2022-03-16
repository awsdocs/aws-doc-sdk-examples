#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose
//
// Defines an AWS CloudFormation stack that creates the following:
//
//  * An Amazon Simple Storage Service (Amazon S3) bucket.
//  * An AWS Identity and Access Management (IAM) role that can be assumed by AWS Glue,
//    grants permission to read from and write to the bucket, and attaches the
//    AWSGlueServiceRole managed policy.
//

import 'source-map-support/register';
import {Construct} from "constructs";
import {App, CfnOutput, RemovalPolicy, Stack, StackProps} from 'aws-cdk-lib';
import {Role, ServicePrincipal, ManagedPolicy} from "aws-cdk-lib/aws-iam";
import {Bucket} from "aws-cdk-lib/aws-s3";

export class SetupStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const glue = new ServicePrincipal('glue.amazonaws.com');

    const glue_service_role: Role = new Role(this, 'AWSGlueServiceRole-DocExample', {
      roleName: 'AWSGlueServiceRole-DocExample',
      assumedBy: glue,
      managedPolicies: [ManagedPolicy.fromAwsManagedPolicyName('service-role/AWSGlueServiceRole')]
    })

    let bucket = new Bucket(this, 'doc-example-glue', {
      removalPolicy: RemovalPolicy.DESTROY
    });
    bucket.grantReadWrite(glue_service_role);

    new CfnOutput(this, 'BucketName', {value: bucket.bucketName})
    new CfnOutput(this, 'RoleName', {value: glue_service_role.roleName})
  }
}

const stackName = 'doc-example-glue-scenario-stack'

const app = new App();

new SetupStack(app, stackName);

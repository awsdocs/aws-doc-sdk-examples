#!/usr/bin/env node
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import "source-map-support/register";
import * as cdk from '@aws-cdk/core';
import { ContainerImage } from "../lib/ecr_repositories-stack";

const app = new cdk.App();
new ContainerImage(app, "EcrRepositoriesStack", {
    env: { account: process.env.CDK_DEFAULT_ACCOUNT, region: process.env.CDK_DEFAULT_REGION },
});
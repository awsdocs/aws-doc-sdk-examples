#!/usr/bin/env node
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { PoolsAndTriggersStack } from '../lib/pools-and-triggers-stack';

const app = new cdk.App();
new PoolsAndTriggersStack(app, 'PoolsAndTriggersStack', {
});
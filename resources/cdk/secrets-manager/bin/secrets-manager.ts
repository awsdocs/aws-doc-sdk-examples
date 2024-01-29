#!/usr/bin/env node
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { SecretsManagerStack } from '../lib/secrets-manager-stack';

const myapp = new cdk.App();
new SecretsManagerStack(myapp, 'SecretsManagerStack', {});
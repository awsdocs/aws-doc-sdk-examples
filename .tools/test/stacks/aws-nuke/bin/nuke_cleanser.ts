#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { NukeCleanserStack } from '../lib/nuke_cleanser-stack';

const app = new cdk.App();

new NukeCleanserStack(app, 'NukeCleanser', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION,
  },
});

app.synth();
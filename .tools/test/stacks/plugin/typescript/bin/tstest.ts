#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { PluginStack } from '../lib/plugin_stack';

const app = new cdk.App();

new PluginStack(app, `PluginStack-${process.env.TOOL_NAME?.replace('_', '-')}`, {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION,
  },
});

app.synth();


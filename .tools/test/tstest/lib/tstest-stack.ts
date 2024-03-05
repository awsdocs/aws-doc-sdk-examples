#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import { S3Stack } from '../lib/s3-stack';

const app = new cdk.App();

const accountMapping: { [accountId: string]: string } = {
  '616362385685': 'ruby',
  '664857444588': 'python',
};

Object.entries(accountMapping).forEach(([accountId, customName]) => {
  new S3Stack(app, `${customName}S3Stack`, {
    env: { account: accountId, region: 'us-east-1' },
    customBucketName: `${customName}-S3`,
  });
});


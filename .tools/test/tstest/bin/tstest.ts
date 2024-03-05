#!/usr/bin/env node
import 'source-map-support/register';
import { App } from '@aws-cdk/core';
import { S3Stack } from '../lib/s3-stack';

const app = new App();

// Construct the bucket name using TOOL_NAME environment variable
const toolName = process.env.TOOL_NAME;
if (!toolName) {
  console.error('TOOL_NAME environment variable is not set.');
  process.exit(1); // Exit if TOOL_NAME is not set
}

const customBucketName = `${toolName.toLowerCase()}-unique-bucket-${Date.now()}`;

new S3Stack(app, 'S3StackTotal', {
  customBucketName,
});

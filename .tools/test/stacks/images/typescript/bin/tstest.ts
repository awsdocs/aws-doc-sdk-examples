#!/usr/bin/env node
import 'source-map-support/register'
import * as cdk from 'aws-cdk-lib'
import { ImageStack } from '../lib/image_stack'

const app = new cdk.App()

new ImageStack(app, 'ImageStack', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT!,
    region: process.env.CDK_DEFAULT_REGION!
  }
})

app.synth()

#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import { GoLambdaCdkStack } from '../lib/go-lambda-cdk-stack';

const app = new cdk.App();
new GoLambdaCdkStack(app, 'GoLambdaCdkStack');

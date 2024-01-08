#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { SecretsManagerStack } from '../lib/secrets-manager-stack';

const myapp = new cdk.App();
new SecretsManagerStack(myapp, 'SecretsManagerStack', {});
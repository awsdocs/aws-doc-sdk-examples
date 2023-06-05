#!/usr/bin/env node
import "source-map-support/register";
import * as cdk from "aws-cdk-lib";

import { BackendStack } from "../lib/backend-stack";

const app = new cdk.App();

/**
 * Create the backend stack.
 */
const backendStack = new BackendStack(app);

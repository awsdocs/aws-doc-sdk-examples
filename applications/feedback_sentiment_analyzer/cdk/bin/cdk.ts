#!/usr/bin/env node
import "source-map-support/register";
import * as cdk from "aws-cdk-lib";

import { AppStack } from "../lib/stack";

const app = new cdk.App();

/**
 * Create the stack.
 */
const appStack = new AppStack(app);

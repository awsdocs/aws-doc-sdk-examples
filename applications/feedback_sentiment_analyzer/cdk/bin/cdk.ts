#!/usr/bin/env node
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import "source-map-support/register";
import * as cdk from "aws-cdk-lib";

import { AppStack } from "../lib/stack";

const app = new cdk.App();

/**
 * Create the stack.
 */
const appStack = new AppStack(app);

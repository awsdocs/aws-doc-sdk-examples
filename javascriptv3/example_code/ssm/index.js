#!/usr/bin/env node
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {
  Scenario,
  parseScenarioArgs,
} from "@aws-doc-sdk-examples/lib/scenario/index.js";

const context = {};

export const scenarios = {
  demo: new Scenario("SSM Basics example", [], context),
};

export const cleanup = {};

// Call function if run directly
import { fileURLToPath } from "url";

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  parseScenarioArgs();
}

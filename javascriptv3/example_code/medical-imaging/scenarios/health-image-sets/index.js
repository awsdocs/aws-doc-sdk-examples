// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {
  parseScenarioArgs,
  Scenario,
} from "@aws-doc-sdk-examples/lib/scenario/index.js";

import { step1 } from "./step-1.js";
import { step2 } from "./step-2.js";
import { step3 } from "./step-3.js";
import { step4 } from "./step-4.js";
import { step5 } from "./step-5.js";
import { step6 } from "./step-6.js";
import { step7 } from "./step-7.js";

const context = {};

const scenarios = {
  deploy: new Scenario("Deploy Resources", [step1], context),
  demo: new Scenario("Run Demo", [step2, step3, step4, step5, step6], context),
  destroy: new Scenario("Clean Up Resources", [step7], context),
};

// Call function if run directly
import { fileURLToPath } from "url";
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  parseScenarioArgs(scenarios);
}

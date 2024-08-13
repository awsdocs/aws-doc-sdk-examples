// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import * as scenario from "./scenario.js";
import * as scenarioParser from "./scenario-parser.js";

export * from "./scenario.js";
export * from "./scenario-parser.js";

export default {
  ...scenario,
  ...scenarioParser,
};

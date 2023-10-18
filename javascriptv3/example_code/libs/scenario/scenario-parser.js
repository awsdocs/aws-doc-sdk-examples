/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { parseArgs } from "node:util";

/**
 * @param {Record<string, import('./scenario.js').Scenario>} scenarios
 */
export function parseScenarioArgs(scenarios) {
  const help = `Usage:
node . -s <${Object.keys(scenarios).join("|")}>
node . -h

Options:
[-s|--scenario, <scenario>] [-h|--help]

-s, --scenario    The name of a scenario to run.
`;

  const { values } = parseArgs({
    options: {
      help: {
        type: "boolean",
        short: "h",
      },
      scenario: {
        short: "s",
        type: "string",
      },
    },
  });

  if (values.help) {
    console.log(help);
    return;
  }

  if (!values.scenario) {
    console.log(`Missing required argument: -s, --scenario\n\n${help}`);
    return;
  }

  if (!(values.scenario in scenarios)) {
    throw new Error(`Invalid scenario: ${values.scenario}\n${help}`);
  }

  scenarios[values.scenario].run();
}

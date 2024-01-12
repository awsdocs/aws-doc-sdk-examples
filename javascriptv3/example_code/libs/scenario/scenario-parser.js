// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { parseArgs } from "node:util";

/**
 * @param {Record<string, import('./scenario.js').Scenario>} scenarios
 */
export function parseScenarioArgs(scenarios) {
  const help = `Usage:
node . -s <${Object.keys(scenarios).join("|")}>
node . -h

Options:
[-s|--scenario, <scenario>] [-h|--help] [-y|--yes] [-v|--verbose]

-h, --help        Show this help message.
-s, --scenario    The name of a scenario to run.
-y, --yes         Assume "yes" to all prompts.
-v, --verbose     Show debug information.
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
      yes: {
        short: "y",
        type: "boolean",
      },
      verbose: {
        short: "v",
        type: "boolean",
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

  if (values.verbose) {
    console.log(
      `[DEBUG ${new Date().toISOString()}] Running scenario: ${
        scenarios[values.scenario].name
      }`,
    );
    console.log(
      `[DEBUG ${new Date().toISOString()}] State: ${JSON.stringify(
        scenarios[values.scenario].state,
      )}`,
    );
  }

  scenarios[values.scenario].run({
    confirmAll: values.yes,
    verbose: values.verbose,
  });
}

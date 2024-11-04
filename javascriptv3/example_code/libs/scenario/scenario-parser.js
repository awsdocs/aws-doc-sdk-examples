// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { parseArgs } from "node:util";
import { printManPage } from "../utils/util-node.js";
import { logger } from "../utils/util-log.js";

/**
 * @param {Record<string, import('./scenario.js').Scenario>} scenarios
 * @param {{name: string, synopsis: string, description: string}} [info] - High level info describing the suite of scenarios.
 */
export const parseScenarioArgs = (
  scenarios,
  { name = "", synopsis, description = "" } = {},
) => {
  const options = {
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
  };

  const { values } = parseArgs({ options });
  const helpPage = () => {
    printManPage(options, {
      name,
      synopsis: synopsis ?? `node . -s <${Object.keys(scenarios).join("|")}>`,
      description,
    });
  };

  if (values.help) {
    helpPage();
    return;
  }

  if (!values.scenario) {
    logger.error("Missing required argument: -s, --scenario");
    helpPage();
    return;
  }

  if (!(values.scenario in scenarios)) {
    logger.error(`Invalid scenario: ${values.scenario}`);
  }

  if (values.verbose) {
    logger.debug(`Running scenario: ${scenarios[values.scenario].name}`);
    logger.debug(`State: ${JSON.stringify(scenarios[values.scenario].state)}`);
  }

  return scenarios[values.scenario].run({
    confirmAll: values.yes,
    verbose: values.verbose,
  });
};

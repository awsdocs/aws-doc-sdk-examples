// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import * as Scenarios from "@aws-doc-sdk-examples/lib/scenario/scenario.js";
import fs from "node:fs/promises";

/**
 * Reusable steps.
 */

// TODO: Refactor to allow injection of Scenarios
export const saveState = new Scenarios.ScenarioAction(
  "saveState",
  async (state) => {
    await fs.writeFile("state.json", JSON.stringify(state));
  },
);

// TODO: Refactor to allow injection of Scenarios
export const loadState = new Scenarios.ScenarioAction(
  "loadState",
  async (state) => {
    try {
      const stateFromDisk = await fs.readFile("state.json", "utf8");
      const parsedState = JSON.parse(stateFromDisk);
      Object.assign(state, parsedState);
    } catch (err) {
      console.warn("Failed to load state from disk:", err);
      console.info("State unmodified.");
    }
  },
);

/**
 * Step factories.
 */

/**
 * @param {Scenarios} scenarios
 * @param {string} stateKey
 */
export const exitOnFalse = (scenarios, stateKey) =>
  new scenarios.ScenarioAction(`exitOn${stateKey}False`, (state) => {
    if (!state[stateKey]) {
      process.exit(0);
    }
  });

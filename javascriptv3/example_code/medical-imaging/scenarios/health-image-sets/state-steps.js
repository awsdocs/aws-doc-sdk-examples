// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { ScenarioAction } from "@aws-doc-sdk-examples/lib/scenario/scenario.js";
import fs from "node:fs/promises";

export const saveState = new ScenarioAction("saveState", async (state) => {
  await fs.writeFile("state.json", JSON.stringify(state));
});

export const loadState = new ScenarioAction("loadState", async (state) => {
  try {
    const stateFromDisk = await fs.readFile("state.json", "utf8");
    const parsedState = JSON.parse(stateFromDisk);
    Object.assign(state, parsedState);
  } catch (err) {
    console.error("Failed to load state from disk:", err);
  }
});

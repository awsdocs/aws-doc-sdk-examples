/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { DynamoDBClient, DeleteTableCommand } from "@aws-sdk/client-dynamodb";

import {
  ScenarioOutput,
  ScenarioInput,
  ScenarioAction,
} from "@aws-sdk-examples/libs/scenario/index.js";

import { MESSAGES } from "./constants.js";

/**
 * @type {import('@aws-sdk-examples/libs/scenario.js').Step[]}
 */
export const destroySteps = [
  new ScenarioInput("destroy", MESSAGES.destroy, { type: "confirm" }),
  new ScenarioAction("abort", (c) => c.destroy === false && process.exit()),
  new ScenarioAction("deleteTable", async (c) => {
    try {
      const client = new DynamoDBClient({});
      await client.send(new DeleteTableCommand({ TableName: c.tableName }));
    } catch (e) {
      c.deleteTableError = e;
    }
  }),
  new ScenarioOutput("deleteTableResult", (c) => {
    if (c.deleteTableError) {
      console.error(c.deleteTableError);
      return MESSAGES.deleteTableError.replace("${TABLE_NAME}", c.tableName);
    } else {
      return MESSAGES.deletedTable.replace("${TABLE_NAME}", c.tableName);
    }
  }),
];

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import type { RequestHandler } from "express";
import { command as createTableCommand } from "../statement-commands/create-table.js";
import { command as getAllItemsCommand } from "../statement-commands/get-all-items.js";
import type { Handler } from "src/types/handler.js";
import type { Sendable } from "src/types/sendable.js";

const errorCodes = {
  TABLE_NOT_FOUND: "Error code: 1146",
};

const createTable = async (
  rdsDataClient: Sendable,
  ...[_req, res, next]: Parameters<RequestHandler>
) => {
  try {
    await rdsDataClient.send(createTableCommand);
    next();
  } catch (err) {
    res.send(err);
  }
};

const nextIfTableExists = async (
  rdsDataClient: Sendable,
  ...[_req, _res, next]: Parameters<RequestHandler>
) => {
  await rdsDataClient.send(getAllItemsCommand);
  next();
};

const validateDb: Handler = {
  withClient:
    ({ rdsDataClient }) =>
    async (...[req, res, next]) => {
      try {
        await nextIfTableExists(rdsDataClient, req, res, next);
      } catch (err) {
        if (err.message.includes(errorCodes.TABLE_NOT_FOUND)) {
          createTable(rdsDataClient, req, res, next);
        } else {
          res.send(err);
        }
      }
    },
};

export { validateDb, errorCodes };

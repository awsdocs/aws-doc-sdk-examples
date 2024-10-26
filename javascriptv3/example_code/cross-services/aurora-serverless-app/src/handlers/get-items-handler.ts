// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import type { Handler } from "src/types/handler.js";
import type { DBRecords } from "src/types/db-record.js";
import { command as getAllItemsCommand } from "../statement-commands/get-all-items.js";
import { command as getArchivedItemsCommand } from "../statement-commands/get-archived-items.js";
import { command as getActiveItemsCommand } from "../statement-commands/get-active-items.js";
import { parseItem } from "./parse-item.js";

const getItemsHandler: Handler = {
  withClient:
    ({ rdsDataClient }) =>
    async (req, res) => {
      const archived = req.query.archived as string;

      const commands = {
        true: getArchivedItemsCommand,
        false: getActiveItemsCommand,
      };

      const response = await rdsDataClient.send<{ records: DBRecords }>(
        commands[archived] || getAllItemsCommand,
      );

      res.send(response.records.map(parseItem));
    },
};

export { getItemsHandler };

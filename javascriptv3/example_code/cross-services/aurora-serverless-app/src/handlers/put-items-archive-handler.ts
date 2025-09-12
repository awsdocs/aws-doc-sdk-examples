// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import type { Handler } from "src/types/handler.js";
import { buildStatementCommand } from "../statement-commands/command-helper.js";

const putItemsArchiveHandler: Handler = {
  withClient:
    ({ rdsDataClient }) =>
    async (req, res) => {
      const { itemId } = req.params;
      const values = {
        itemId: { StringValue: itemId },
      };
      const command = buildStatementCommand(
        `update items
         set archived = 1
         where iditem = ":itemId"`,
        values,
      );

      await rdsDataClient.send(command);
      res.send({});
    },
};

export { putItemsArchiveHandler };

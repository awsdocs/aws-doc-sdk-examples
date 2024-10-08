// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/* eslint-disable -- This file existed pre-eslint configuration. Fix the next time the file is touched. */

import { v4 as uuidv4 } from "uuid";
import { Handler } from "src/types/handler.js";
import { Item } from "src/types/item.js";
import { buildStatementCommand } from "../statement-commands/command-helper.js";

const postItemsHandler: Handler = {
  withClient:
    ({ rdsDataClient }) =>
    async (req, res) => {
      const { description, guide, status, name }: Item = req.body;
      const command = buildStatementCommand(
        "insert into items (iditem, description, guide, status, username, archived)\n" +
          `values ("${uuidv4()}", "${description}", "${guide}", "${status}", "${name}", 0)`,
      );

      await rdsDataClient.send(command);
      res.status(200).send({});
    },
};

export { postItemsHandler };

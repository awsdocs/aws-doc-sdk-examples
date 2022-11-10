/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { log } from "../log.js";

/** snippet-start:[javascript.v3.glue.scenarios.basic.CleanUpTablesStep] */
const deleteTables = (deleteTable, databaseName, tableNames) =>
  Promise.all(
    tableNames.map((tableName) =>
      deleteTable(databaseName, tableName).catch(console.error)
    )
  );

const makeCleanUpTablesStep =
  ({ getTables, deleteTable }) =>
  async (context) => {
    const { TableList } = await getTables(context.envVars.DATABASE_NAME).catch(
      () => ({ TableList: null })
    );

    if (TableList && TableList.length > 0) {
      const { tableNames } = await context.prompter.prompt({
        name: "tableNames",
        type: "checkbox",
        message: "Let's clean up tables. Select tables to delete.",
        choices: TableList.map((t) => t.Name),
      });

      if (tableNames.length === 0) {
        log("No tables selected.");
      } else {
        log("Deleting tables.");
        await deleteTables(
          deleteTable,
          context.envVars.DATABASE_NAME,
          tableNames
        );
        log("Tables deleted.", { type: "success" });
      }
    }

    return { ...context };
  };
/** snippet-end:[javascript.v3.glue.scenarios.basic.CleanUpTablesStep] */

export { makeCleanUpTablesStep };

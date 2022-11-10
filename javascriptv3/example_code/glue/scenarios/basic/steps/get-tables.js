/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { log } from "../log.js";

/** snippet-start:[javascript.v3.glue.scenarios.basic.GetTablesStep] */
const makeGetTablesStep =
  ({ getTables }) =>
  async (context) => {
    const { TableList } = await getTables(context.envVars.DATABASE_NAME);
    log("Tables:");
    log(TableList.map((table) => `  • ${table.Name}\n`));
    return { ...context };
  };
/** snippet-end:[javascript.v3.glue.scenarios.basic.GetTablesStep] */

export { makeGetTablesStep };

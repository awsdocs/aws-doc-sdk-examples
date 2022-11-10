/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { log } from "../log.js";

/** snippet-start:[javascript.v3.glue.scenarios.basic.GetDatabaseStep] */
const makeGetDatabaseStep =
  ({ getDatabase }) =>
  async (context) => {
    const {
      Database: { Name },
    } = await getDatabase(context.envVars.DATABASE_NAME);
    log(`Database: ${Name}`);
    return { ...context };
  };
/** snippet-end:[javascript.v3.glue.scenarios.basic.GetDatabaseStep] */

export { makeGetDatabaseStep };

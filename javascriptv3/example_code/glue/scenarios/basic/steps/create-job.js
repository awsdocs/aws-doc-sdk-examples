/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { log } from "../log.js";

/** snippet-start:[javascript.v3.glue.scenarios.basic.CreateJobStep] */
const makeCreateJobStep =
  ({ createJob }) =>
  async (context) => {
    log("Creating Job.");
    await createJob(
      context.envVars.JOB_NAME,
      context.envVars.ROLE_NAME,
      context.envVars.BUCKET_NAME,
      context.envVars.PYTHON_SCRIPT_KEY
    );
    log("Job created.", { type: "success" });

    return { ...context };
  };
/** snippet-end:[javascript.v3.glue.scenarios.basic.CreateJobStep] */

export { makeCreateJobStep };

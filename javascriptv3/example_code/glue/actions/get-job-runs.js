/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { GetJobRunsCommand, GlueClient } from "@aws-sdk/client-glue";
import { DEFAULT_REGION } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.glue.actions.GetJobRuns] */
const getJobRuns = (jobName) => {
  const client = new GlueClient({ region: DEFAULT_REGION });
  const command = new GetJobRunsCommand({
    JobName: jobName,
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.glue.actions.GetJobRuns] */

export { getJobRuns };

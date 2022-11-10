/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { GetJobRunCommand, GlueClient } from "@aws-sdk/client-glue";
import { DEFAULT_REGION } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.glue.actions.GetJobRun] */
const getJobRun = (jobName, jobRunId) => {
  const client = new GlueClient({ region: DEFAULT_REGION });
  const command = new GetJobRunCommand({
    JobName: jobName,
    RunId: jobRunId,
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.glue.actions.GetJobRun] */

export { getJobRun };

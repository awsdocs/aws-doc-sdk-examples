/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { GetJobCommand, GlueClient } from "@aws-sdk/client-glue";
import { DEFAULT_REGION } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.glue.actions.GetJob] */
const getJob = (jobName) => {
  const client = new GlueClient({ region: DEFAULT_REGION });

  const command = new GetJobCommand({
    JobName: jobName,
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.glue.actions.GetJob] */

export { getJob };

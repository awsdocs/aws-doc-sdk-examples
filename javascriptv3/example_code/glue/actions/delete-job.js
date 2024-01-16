// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { DeleteJobCommand, GlueClient } from "@aws-sdk/client-glue";

/** snippet-start:[javascript.v3.glue.actions.DeleteJob] */
const deleteJob = (jobName) => {
  const client = new GlueClient({});

  const command = new DeleteJobCommand({
    JobName: jobName,
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.glue.actions.DeleteJob] */

export { deleteJob };

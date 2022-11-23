/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[cwLogs.JavaScript.cwl.deleteSubscriptionFilterV3]
import { DeleteSubscriptionFilterCommand } from "@aws-sdk/client-cloudwatch-logs";
import { client } from "../libs/client.js";

const run = async () => {
  const command = new DeleteSubscriptionFilterCommand({
    filterName: "FILTER", // The name of the subscription filter.
    logGroupName: "LOG_GROUP", // The name of the log group.
  });

  try {
    return await client.send(command);
  } catch (err) {
    console.error(err);
  }
};

export default run();
// snippet-end:[cwLogs.JavaScript.cwl.deleteSubscriptionFilterV3]

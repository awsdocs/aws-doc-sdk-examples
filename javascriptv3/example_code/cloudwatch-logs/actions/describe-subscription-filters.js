/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[cwLogs.JavaScript.cwl.describeSubscriptionFiltersV3]
import { DescribeSubscriptionFiltersCommand } from "@aws-sdk/client-cloudwatch-logs";
import { client } from "../libs/client.js";

const run = async () => {
  const command = new DescribeSubscriptionFiltersCommand({
    logGroupName: "GROUP_NAME", //GROUP_NAME
    limit: 5,
  });

  try {
    return await client.send(command);
  } catch (err) {
    console.error(err);
  }
};

export default run();
// snippet-end:[cwLogs.JavaScript.cwl.describeSubscriptionFiltersV3]

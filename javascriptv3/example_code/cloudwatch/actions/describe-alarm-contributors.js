// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[javascript.v3.cloudwatch.actions.DescribeAlarmContributors]
import { DescribeAlarmContributorsCommand } from "@aws-sdk/client-cloudwatch";
import { client } from "../libs/client.js";

// Get the contributors for a PromQL alarm. Each contributor is one series that the
// alarm's query matched, identified by its label set. This is how you find out which
// hosts, services, or pods are breaching, rather than only that something is.
const run = async () => {
  const contributors = [];
  let nextToken;

  try {
    do {
      const command = new DescribeAlarmContributorsCommand({
        AlarmName: process.env.CLOUDWATCH_ALARM_NAME, // Set CLOUDWATCH_ALARM_NAME to the name of an existing PromQL alarm.
        NextToken: nextToken,
      });
      const response = await client.send(command);
      contributors.push(...response.AlarmContributors);
      nextToken = response.NextToken;
    } while (nextToken);

    for (const contributor of contributors) {
      const labels = Object.entries(contributor.ContributorAttributes)
        .map(([key, value]) => `${key}=${value}`)
        .join(", ");
      console.log(`${contributor.ContributorId}: ${labels}`);
      console.log(`  reason: ${contributor.StateReason}`);
    }
    return contributors;
  } catch (err) {
    console.error(err);
  }
};

export default run();
// snippet-end:[javascript.v3.cloudwatch.actions.DescribeAlarmContributors]

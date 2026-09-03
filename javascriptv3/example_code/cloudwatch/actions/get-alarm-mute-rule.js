// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[javascript.v3.cloudwatch.actions.GetAlarmMuteRule]
import { GetAlarmMuteRuleCommand } from "@aws-sdk/client-cloudwatch";
import { client } from "../libs/client.js";

// Get the full configuration of an alarm mute rule, including its schedule, the alarms
// it targets, and whether it is currently SCHEDULED, ACTIVE, or EXPIRED.
const run = async () => {
  const command = new GetAlarmMuteRuleCommand({
    AlarmMuteRuleName: process.env.CLOUDWATCH_MUTE_RULE_NAME, // Set CLOUDWATCH_MUTE_RULE_NAME to the name of an existing mute rule.
  });

  try {
    const response = await client.send(command);
    console.log(`Mute rule ${response.Name} is ${response.Status}.`);
    return response;
  } catch (err) {
    console.error(err);
  }
};

export default run();
// snippet-end:[javascript.v3.cloudwatch.actions.GetAlarmMuteRule]

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[javascript.v3.cloudwatch.actions.DeleteAlarmMuteRule]
import { DeleteAlarmMuteRuleCommand } from "@aws-sdk/client-cloudwatch";
import { client } from "../libs/client.js";

// Delete an alarm mute rule. The alarms it targeted resume firing their actions.
const run = async () => {
  const command = new DeleteAlarmMuteRuleCommand({
    AlarmMuteRuleName: process.env.CLOUDWATCH_MUTE_RULE_NAME, // Set CLOUDWATCH_MUTE_RULE_NAME to the name of an existing mute rule.
  });

  try {
    return await client.send(command);
  } catch (err) {
    console.error(err);
  }
};

export default run();
// snippet-end:[javascript.v3.cloudwatch.actions.DeleteAlarmMuteRule]

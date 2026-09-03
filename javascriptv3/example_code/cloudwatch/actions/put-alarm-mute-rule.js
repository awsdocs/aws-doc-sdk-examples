// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[javascript.v3.cloudwatch.actions.PutAlarmMuteRule]
import { PutAlarmMuteRuleCommand } from "@aws-sdk/client-cloudwatch";
import { client } from "../libs/client.js";

// Create or update an alarm mute rule. While a mute rule is active the targeted alarms
// keep evaluating and keep transitioning between states, but their configured actions
// do not fire. This is the supported way to suppress notifications during a known
// maintenance window, instead of disabling alarm actions and hoping someone remembers
// to turn them back on.
const run = async () => {
  const command = new PutAlarmMuteRuleCommand({
    Name: process.env.CLOUDWATCH_MUTE_RULE_NAME, // Set CLOUDWATCH_MUTE_RULE_NAME to the name of the mute rule.
    Description: "Suppress checkout CPU pages during Sunday patching.",
    Rule: {
      Schedule: {
        // For a recurring window, use a five-field cron expression,
        // cron(Minutes Hours Day-of-month Month Day-of-week). Note that this is five
        // fields, not the six that Amazon EventBridge uses. For a one-time window, use
        // an at expression such as "at(2026-09-05T02:00)".
        Expression: "cron(0 2 * * SUN)",
        // How long the mute window lasts once it activates, in ISO 8601 duration
        // format, from PT1M (one minute) to P15D (15 days).
        Duration: "PT2H",
        Timezone: "America/Los_Angeles",
      },
    },
    // Target up to 100 alarms by name. Omit MuteTargets to mute every alarm in the
    // account.
    MuteTargets: {
      AlarmNames: [process.env.CLOUDWATCH_ALARM_NAME], // Set CLOUDWATCH_ALARM_NAME to the name of an existing alarm.
    },
  });

  try {
    return await client.send(command);
  } catch (err) {
    console.error(err);
  }
};

export default run();
// snippet-end:[javascript.v3.cloudwatch.actions.PutAlarmMuteRule]

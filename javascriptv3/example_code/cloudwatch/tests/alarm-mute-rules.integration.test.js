// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, beforeAll, afterAll } from "vitest";
import {
  createPromQlAlarm,
  deleteAlarmMuteRule,
  deleteAlarms,
} from "../libs/cloudwatch-helper.js";

// The mute rule lifecycle is self-cleaning: it creates a PromQL alarm and a mute rule
// that targets it, then removes both. StartOTelEnrichment and StopOTelEnrichment are
// deliberately not covered here, because they change an account-wide setting that other
// tests and workloads in the same account depend on.
describe("alarm-mute-rules", () => {
  const alarmName = "AlarmMuteRuleTestAlarm";
  const muteRuleName = "AlarmMuteRuleTestRule";
  const query = 'avg by (host_name) (cpu_utilization_percent{service_name="checkout"}) > 80';

  beforeAll(async () => {
    process.env.CLOUDWATCH_ALARM_NAME = alarmName;
    process.env.CLOUDWATCH_MUTE_RULE_NAME = muteRuleName;
    await createPromQlAlarm(alarmName, query);
  });

  afterAll(async () => {
    await deleteAlarmMuteRule(muteRuleName).catch(() => {});
    await deleteAlarms(alarmName);
  });

  it("should create, get, list, and delete a mute rule", async () => {
    const putMod = await import("../actions/put-alarm-mute-rule.js");
    await putMod.default;

    const getMod = await import("../actions/get-alarm-mute-rule.js");
    const rule = await getMod.default;
    expect(rule.Name).toBe(muteRuleName);
    // A rule whose window has not opened yet is SCHEDULED.
    expect(["SCHEDULED", "ACTIVE"]).toContain(rule.Status);

    const listMod = await import("../actions/list-alarm-mute-rules.js");
    const summaries = await listMod.default;
    // AlarmMuteRuleSummary has no Name field, so match on the ARN suffix.
    expect(
      summaries.some((summary) =>
        summary.AlarmMuteRuleArn.endsWith(muteRuleName),
      ),
    ).toBe(true);

    const deleteMod = await import("../actions/delete-alarm-mute-rule.js");
    await deleteMod.default;
  });
});

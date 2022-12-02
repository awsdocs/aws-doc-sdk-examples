/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, beforeAll, afterAll } from "vitest";
import {
  createAlarm,
  deleteAlarms,
  describeAlarm,
} from "../libs/cloudwatch-helper.js";
import { runEC2Instance, terminateEC2Instance } from "../libs/ec2-helper.js";

describe("disable-alarm-actions", () => {
  /** @type {string} */
  const alarmName = "DisableAlarmTest";

  /** @type {string} */
  let instanceIdToTerminate;

  beforeAll(async () => {
    instanceIdToTerminate = await runEC2Instance();
    await createAlarm(alarmName, instanceIdToTerminate);
  });

  afterAll(async () => {
    await terminateEC2Instance(instanceIdToTerminate);
    await deleteAlarms([alarmName]);
  });

  it("should set an alarm to enabled", async () => {
    process.env.CLOUDWATCH_ALARM_NAME = alarmName;

    const mod = await import("../actions/disable-alarm-actions.js");
    await mod.default;

    const { ActionsEnabled } = await describeAlarm(alarmName);

    expect(ActionsEnabled).toBe(false);
  });
});

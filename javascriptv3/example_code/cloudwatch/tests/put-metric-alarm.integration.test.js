/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, beforeAll, afterAll } from "vitest";
import { deleteAlarms, describeAlarm } from "../libs/cloudwatch-helper.js";
import { runEC2Instance, terminateEC2Instance } from "../libs/ec2-helper.js";

describe("put-metric-alarm", () => {
  /** @type {string} */
  let instanceIdToTerminate;

  beforeAll(async () => {
    instanceIdToTerminate = await runEC2Instance();
  });
  afterAll(async () => {
    await deleteAlarms([process.env.CLOUDWATCH_ALARM_NAME]);
    await terminateEC2Instance(instanceIdToTerminate);
  });

  it("should create an alarm", async () => {
    process.env.CLOUDWATCH_ALARM_NAME = "CreateAlarmTest";
    process.env.EC2_INSTANCE_ID = instanceIdToTerminate;

    const mod = await import("../actions/put-metric-alarm.js");
    await mod.default;

    const { AlarmName } = await describeAlarm(
      process.env.CLOUDWATCH_ALARM_NAME
    );

    expect(AlarmName).toBe(process.env.CLOUDWATCH_ALARM_NAME);
  });
});

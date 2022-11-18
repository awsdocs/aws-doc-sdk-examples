/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, beforeAll, afterAll } from "vitest";
import { createAlarm, deleteAlarms } from "../libs/cloudwatch-helper.js";
import { runEC2Instance, terminateEC2Instance } from "../libs/ec2-helper.js";

describe("describe-alarms", () => {
  /** @type {string} */
  const alarmName = "DeleteAlarmsTest";

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

    const mod = await import("../actions/describe-alarms.js");

    /** @type {{ MetricAlarms: { AlarmName: string  }[]}} */
    const { MetricAlarms } = await mod.default;

    const matchingAlarm = MetricAlarms.find(
      ({ AlarmName }) => AlarmName === process.env.CLOUDWATCH_ALARM_NAME
    );

    return expect(matchingAlarm).toBeTruthy();
  });
});

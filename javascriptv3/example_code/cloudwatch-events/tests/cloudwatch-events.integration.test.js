// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, beforeAll, afterAll } from "vitest";
import {
  CloudWatchLogsClient,
  CreateLogGroupCommand,
  DeleteLogGroupCommand,
  DescribeLogGroupsCommand,
} from "@aws-sdk/client-cloudwatch-logs";
import {
  DeleteRuleCommand,
  CloudWatchEventsClient,
  RemoveTargetsCommand,
} from "@aws-sdk/client-cloudwatch-events";

/**
 * Create a log group.
 * @param {string} logGroupName - The name of the log group.
 */
const createLogGroup = async (logGroupName) => {
  const cwlClient = new CloudWatchLogsClient({});
  const command = new CreateLogGroupCommand({
    logGroupName: logGroupName,
  });

  await cwlClient.send(command);

  const describeLogGroupsCommand = new DescribeLogGroupsCommand({
    logGroupNamePrefix: logGroupName,
  });

  const { logGroups } = await cwlClient.send(describeLogGroupsCommand);
  return logGroups[0].arn;
};

/**
 * Delete a rule.
 * @param {string} ruleName - The name of the rule.
 */
const deleteRule = async (ruleName) => {
  try {
    const cweClient = new CloudWatchEventsClient({});
    const command = new DeleteRuleCommand({
      Name: ruleName,
    });
    await cweClient.send(command);
  } catch (caught) {
    if (caught instanceof Error) {
      console.error(
        `Failed to delete rule: ${ruleName}. ${caught.name}: ${caught.message}`,
      );
    } else {
      throw caught;
    }
  }
};

/**
 * Remove a target from a rule.
 * @param {string} ruleName - The name of the rule.
 * @param {string[]} targetIds - The IDs of the targets to remove.
 */
const removeTargets = async (ruleName, targetIds) => {
  try {
    const cweClient = new CloudWatchEventsClient({});
    const command = new RemoveTargetsCommand({
      Rule: ruleName,
      Ids: targetIds,
    });
    await cweClient.send(command);
  } catch (caught) {
    if (caught instanceof Error) {
      console.error(
        `Failed to remove targets: ${targetIds} from rule: ${ruleName}. ${caught.name}: ${caught.message}`,
      );
    } else {
      throw caught;
    }
  }
};

/**
 * Delete a log group.
 * @param {string} logGroupName - The name of the log group.
 */
const deleteLogGroup = async (logGroupName) => {
  try {
    const cwlClient = new CloudWatchLogsClient({});
    const command = new DeleteLogGroupCommand({
      logGroupName: logGroupName,
    });
    await cwlClient.send(command);
  } catch (caught) {
    if (caught instanceof Error) {
      console.error(
        `Failed to delete log group: ${logGroupName}. ${caught.name}: ${caught.message}`,
      );
    } else {
      throw caught;
    }
  }
};

describe("CloudWatch Events integration test", () => {
  const logGroupName = "cloudwatch-events-test-log-group";
  const ruleName = "test-rule";
  const rulePattern = JSON.stringify({
    source: ["my.app"],
    "detail-type": ["My Custom Event"],
  });
  const targetId = "test-target";
  let targetArn = "";

  beforeAll(async () => {
    /**
     * Create a log group to use as a target for the rule.
     */
    targetArn = await createLogGroup(logGroupName);
  });

  afterAll(async () => {
    await removeTargets(ruleName, [targetId]);
    await deleteRule(ruleName);
    await deleteLogGroup(logGroupName);
  });

  it("should create a rule, add targets, and send an event", async () => {
    /**
     * Set environment variables for the rule and target.
     */
    process.env.CLOUDWATCH_EVENTS_RULE = ruleName;
    process.env.CLOUDWATCH_EVENTS_RULE_PATTERN = rulePattern;
    process.env.CLOUDWATCH_EVENTS_TARGET_ARN = targetArn;
    process.env.CLOUDWATCH_EVENTS_TARGET_ID = targetId;

    /**
     * Create a rule.
     */
    const putRule = await import("../actions/put-rule.js");
    const rule = await putRule.default;
    expect(rule).toBeDefined();
    expect(rule.RuleArn).toBeDefined();

    /**
     * Add a target to the rule.
     */
    const putTargets = await import("../actions/put-targets.js");
    const targets = await putTargets.default;
    expect(targets).toBeDefined();
    expect(targets.FailedEntryCount).toEqual(0);

    /**
     * Send an event to the rule.
     */
    const putEvents = await import("../actions/put-events.js");
    const events = await putEvents.default;
    expect(events).toBeDefined();
    expect(events.FailedEntryCount).toEqual(0);
  });
});

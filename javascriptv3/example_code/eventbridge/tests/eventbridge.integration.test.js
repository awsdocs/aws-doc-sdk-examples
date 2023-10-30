/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { test, expect, beforeAll, afterAll } from "vitest";

import {
  EventBridgeClient,
  DeleteRuleCommand,
  RemoveTargetsCommand,
} from "@aws-sdk/client-eventbridge";
import { IAMClient } from "@aws-sdk/client-iam";
import {
  SQSClient,
  ReceiveMessageCommand,
  CreateQueueCommand,
  GetQueueAttributesCommand,
  DeleteQueueCommand,
  SetQueueAttributesCommand,
} from "@aws-sdk/client-sqs";

import { retry, wait } from "@aws-sdk-examples/libs/utils/util-timers.js";

import { putEvents } from "../actions/put-events.js";
import { putRule } from "../actions/put-rule.js";
import { putTarget } from "../actions/put-targets.js";

const Clients = {
  EventBridge: new EventBridgeClient({}),
  SQSClient: new SQSClient({}),
  IAM: new IAMClient({}),
};

const ruleName = `EventBridgeTestRule-${Date.now()}`;
const targetId = `EventBridgeTarget-${Date.now()}`;

let queueUrl;
let queueArn;

/**
 * @type {(() => Promise<void>)[]}
 */
const cleanUpActions = [];

beforeAll(async () => {
  queueUrl = await createQueue(Clients.SQSClient);
  queueArn = await getQueueArn(Clients.SQSClient, queueUrl);
});

afterAll(async () => {
  for (let i = cleanUpActions.length - 1; i >= 0; i--) {
    await retry({ intervalInMs: 1000, maxRetries: 20 }, cleanUpActions[i]);
  }

  // Delete the target.
  await Clients.EventBridge.send(
    new RemoveTargetsCommand({
      Rule: ruleName,
      Ids: [targetId],
    }),
  );

  // Delete the rule.
  await Clients.EventBridge.send(
    new DeleteRuleCommand({
      Name: ruleName,
    }),
  );
});

test("target should receive message", async () => {
  // Put the rule.
  const { RuleArn } = await retry({ intervalInMs: 1000, maxRetries: 20 }, () =>
    putRule(ruleName, "eventbridge.integration.test"),
  );

  // Ensure the queue allows the rule to send messages.
  await addQueuePolicy(Clients.SQSClient, queueArn, RuleArn, queueUrl);
  await wait(15);

  // Put the target.
  await putTarget(ruleName, queueArn, targetId);
  await wait(15);

  // Put the event.
  await putEvents("eventbridge.integration.test", "greeting", []);

  // Get message from the SQS queue.
  const messages = await getMessagesFromQueue(Clients.SQSClient, queueUrl);

  expect(messages[0].Body).toContain("Hello there.");
});

/**
 * @param {SQSClient} sqsClient
 */
async function createQueue(sqsClient) {
  const { QueueUrl } = await sqsClient.send(
    new CreateQueueCommand({
      QueueName: `EventBridgeTestQueue-${Date.now()}`,
    }),
  );

  cleanUpActions.push(async () => {
    await sqsClient.send(new DeleteQueueCommand({ QueueUrl }));
  });

  return QueueUrl;
}

/**
 * @param {SQSClient} sqsClient
 * @param {string} queueUrl
 */
async function getQueueArn(sqsClient, queueUrl) {
  const { Attributes } = await sqsClient.send(
    new GetQueueAttributesCommand({
      QueueUrl: queueUrl,
      AttributeNames: ["QueueArn"],
    }),
  );
  return Attributes.QueueArn;
}

/**
 * @param {SQSClient} sqsClient
 * @param {string} queueArn
 * @param {string} ruleArn
 * @param {string} queueUrl
 */
async function addQueuePolicy(sqsClient, queueArn, ruleArn, queueUrl) {
  const policy = JSON.stringify({
    Version: "2012-10-17",
    Statement: [
      {
        Sid: "SQS-Allow-EventBridge-SendMessage",
        Effect: "Allow",
        Principal: {
          Service: "events.amazonaws.com",
        },
        Action: "sqs:SendMessage",
        Resource: queueArn,
        Condition: {
          ArnEquals: {
            "aws:SourceArn": ruleArn,
          },
        },
      },
    ],
  });

  await sqsClient.send(
    new SetQueueAttributesCommand({
      QueueUrl: queueUrl,
      Attributes: {
        Policy: policy,
      },
    }),
  );
}

/**
 * @param {SQSClient} sqsClient
 * @param {string} queueUrl
 */
function getMessagesFromQueue(sqsClient, queueUrl) {
  return retry({ intervalInMs: 0, maxRetries: 3 }, async () => {
    const { Messages } = await sqsClient.send(
      new ReceiveMessageCommand({
        QueueUrl: queueUrl,
        MaxNumberOfMessages: 1,
        WaitTimeSeconds: 20,
      }),
    );

    if (!Messages || Messages.length === 0) {
      throw new Error("No messages received.");
    }

    return Messages;
  });
}

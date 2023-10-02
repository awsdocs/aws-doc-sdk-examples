/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, beforeAll, afterAll } from "vitest";
import {
  CreateTopicCommand,
  UnsubscribeCommand,
  DeleteTopicCommand,
  paginateListSubscriptions,
} from "@aws-sdk/client-sns";

import { snsClient } from "../libs/snsClient.js";
import { subscribeQueue } from "../actions/subscribe-queue.js";
import {
  CreateQueueCommand,
  DeleteQueueCommand,
  GetQueueAttributesCommand,
  SQSClient,
} from "@aws-sdk/client-sqs";
import { subscribeQueueFiltered } from "../actions/subscribe-queue-filtered.js";

describe("subscribeQueue", () => {
  let topicArn, queueArn, queueUrl, subscriptionArn;
  const sqsClient = new SQSClient({});
  const affix = Math.floor(Math.random() * 1000000);

  beforeAll(async () => {
    const { TopicArn } = await snsClient.send(
      new CreateTopicCommand({ Name: `subscribe-queue-test-${affix}` }),
    );
    topicArn = TopicArn;

    const { QueueUrl } = await sqsClient.send(
      new CreateQueueCommand({ QueueName: `subscribe-queue-test-${affix}` }),
    );

    queueUrl = QueueUrl;

    const {
      Attributes: { QueueArn },
    } = await sqsClient.send(
      new GetQueueAttributesCommand({
        QueueUrl,
        AttributeNames: ["QueueArn"],
      }),
    );

    queueArn = QueueArn;
  });

  afterAll(async () => {
    await snsClient.send(
      new UnsubscribeCommand({ SubscriptionArn: subscriptionArn }),
    );
    await snsClient.send(new DeleteTopicCommand({ TopicArn: topicArn }));
    await sqsClient.send(new DeleteQueueCommand({ QueueUrl: queueUrl }));
  });

  it("should subscribe a queue to an SNS topic", async () => {
    const { SubscriptionArn } = await subscribeQueue(topicArn, queueArn);
    subscriptionArn = SubscriptionArn;

    const paginator = paginateListSubscriptions(
      { client: snsClient },
      { TopicArn: topicArn },
    );

    const subscriptionArns = [];

    for await (const page of paginator) {
      subscriptionArns.push(
        ...page.Subscriptions.map((s) => s.SubscriptionArn),
      );
    }

    expect(subscriptionArns).toContain(SubscriptionArn);
  });
});

describe("subscribeQueueFiltered", () => {
  let topicArn, queueArn, queueUrl, subscriptionArn;
  const sqsClient = new SQSClient({});
  const affix = Math.floor(Math.random() * 1000000);

  beforeAll(async () => {
    const { TopicArn } = await snsClient.send(
      new CreateTopicCommand({ Name: `subscribe-queue-test-${affix}` }),
    );
    topicArn = TopicArn;

    const { QueueUrl } = await sqsClient.send(
      new CreateQueueCommand({ QueueName: `subscribe-queue-test-${affix}` }),
    );

    queueUrl = QueueUrl;

    const {
      Attributes: { QueueArn },
    } = await sqsClient.send(
      new GetQueueAttributesCommand({
        QueueUrl,
        AttributeNames: ["QueueArn"],
      }),
    );

    queueArn = QueueArn;
  });

  afterAll(async () => {
    await snsClient.send(
      new UnsubscribeCommand({ SubscriptionArn: subscriptionArn }),
    );
    await snsClient.send(new DeleteTopicCommand({ TopicArn: topicArn }));
    await sqsClient.send(new DeleteQueueCommand({ QueueUrl: queueUrl }));
  });

  it("should subscribe a queue to an SNS topic", async () => {
    const { SubscriptionArn } = await subscribeQueueFiltered(
      topicArn,
      queueArn,
    );
    subscriptionArn = SubscriptionArn;

    const paginator = paginateListSubscriptions(
      { client: snsClient },
      { TopicArn: topicArn },
    );

    const subscriptionArns = [];

    for await (const page of paginator) {
      subscriptionArns.push(
        ...page.Subscriptions.map((s) => s.SubscriptionArn),
      );
    }

    expect(subscriptionArns).toContain(SubscriptionArn);
  });
});

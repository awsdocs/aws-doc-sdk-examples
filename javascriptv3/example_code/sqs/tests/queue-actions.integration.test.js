import { describe, it, expect, afterAll, vi } from "vitest";

import { getUniqueName } from "@aws-sdk-examples/libs/utils/util-string.js";
import { retry } from "@aws-sdk-examples/libs/utils/util-timers.js";

import { main as createQueue } from "../actions/create-queue.js";
import { main as listQueues } from "../actions/list-queues.js";
import { main as deleteQueue } from "../actions/delete-queue.js";
import { main as getQueueUrl } from "../actions/get-queue-url.js";
import { main as setQueueAttributes } from "../actions/set-queue-attributes.js";
import { main as sendMessage } from "../actions/send-message.js";
import { main as receiveDeleteMessage } from "../actions/receive-delete-message.js";
import { getQueueAttributes } from "../actions/get-queue-attributes.js";

describe("queue actions", () => {
  const queueName = getUniqueName("test-queue");
  let queueUrl;

  afterAll(async () => {
    try {
      await deleteQueue(queueUrl);
    } catch (err) {
      console.error(err.message);
    }
  });

  it("should create, describe, list, set attributes for, get attributes for, send a message to, and delete a queue", async () => {
    const { QueueUrl } = await createQueue(queueName);
    queueUrl = QueueUrl;

    const { QueueUrl: queueUrlResponse } = await getQueueUrl(queueName);
    expect(queueUrlResponse).toEqual(QueueUrl);

    await retry({ intervalInMs: 1000, maxRetries: 60 }, async () => {
      const urls = await listQueues();

      expect(urls[0]).toEqual(expect.stringContaining(queueName));
    });

    await setQueueAttributes(queueUrl);

    const { Attributes } = await getQueueAttributes(queueUrl);

    expect(Attributes.DelaySeconds).toEqual("1");

    await sendMessage(queueUrl);

    const consoleSpy = vi.spyOn(console, "log");

    await retry({ intervalInMs: 10000, maxRetries: 24 }, async () => {
      await receiveDeleteMessage(queueUrl);
      expect(consoleSpy).toHaveBeenCalledWith(
        "Information about current NY Times fiction bestseller for week of 12/11/2016.",
      );
    });

    await deleteQueue(QueueUrl);

    await retry({ intervalInMs: 1000, maxRetries: 60 }, async () => {
      const urlsAfterDelete = await listQueues();
      expect(urlsAfterDelete.length).toBe(0);
    });
  });
});

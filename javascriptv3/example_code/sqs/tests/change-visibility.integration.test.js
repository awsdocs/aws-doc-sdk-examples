import { describe, it, afterAll, beforeAll } from "vitest";

import { getUniqueName } from "@aws-sdk-examples/libs/utils/util-string.js";
import { retry } from "@aws-sdk-examples/libs/utils/util-timers.js";

import { main as deleteQueue } from "../actions/delete-queue.js";
import { main as createQueue } from "../actions/create-queue.js";
import { main as sendMessage } from "../actions/send-message.js";
import { main as changeVisibility } from "../actions/change-message-visibility.js";

describe("queue actions", () => {
  const queueName = getUniqueName("test-queue");
  let queueUrl;

  beforeAll(async () => {
    const { QueueUrl } = await createQueue(queueName);
    queueUrl = QueueUrl;
  });

  afterAll(async () => {
    try {
      await deleteQueue(queueUrl);
    } catch (err) {
      console.error(err.message);
    }
  });

  it("should change visibility of a message", async () => {
    await sendMessage(queueUrl);
    await retry({ intervalInMs: 1000, maxRetries: 20 }, async () => {
      await changeVisibility(queueUrl);
    });
  });
});

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi, beforeEach } from "vitest";
import { TopicsQueuesWkflw } from "../TopicsQueuesWkflw.js";
import {
  CreateTopicCommand,
  SNSClient,
  SubscribeCommand,
} from "@aws-sdk/client-sns";
import {
  CreateQueueCommand,
  GetQueueAttributesCommand,
  ReceiveMessageCommand,
  SQSClient,
  SetQueueAttributesCommand,
} from "@aws-sdk/client-sqs";
import { MESSAGES } from "../messages.js";

const SQSClientMock = {
  send: vi.fn((command) => {
    if (command instanceof CreateQueueCommand) {
      return Promise.resolve({
        QueueUrl: "queue-url",
      });
    }

    if (command instanceof GetQueueAttributesCommand) {
      return Promise.resolve({
        Attributes: {
          QueueArn: "queue-arn",
        },
      });
    }

    if (command instanceof ReceiveMessageCommand) {
      return Promise.resolve({
        Messages: [
          {
            MessageId: "cfcb7340-89b5-420f-8021-fcf38fa78a38",
            ReceiptHandle: "123",
            MD5OfBody: "2ed986e2481e216cb2328d1478df527b",
            Body:
              "{\n" +
              '  "Type" : "Notification",\n' +
              '  "MessageId" : "xxxxxxxx-a13d-584a-98fa-b95db12bee66",\n' +
              '  "SequenceNumber" : "10000000000000007000",\n' +
              '  "TopicArn" : "arn:aws:sns:us-east-1:xxxxxxxxxxxx:breaking-news.fifo",\n' +
              '  "Message" : "good and bad news ",\n' +
              '  "Timestamp" : "2023-08-03T20:28:06.822Z",\n' +
              '  "UnsubscribeURL" : "https://sns.us-east-1.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-east-1:xxxxxxxxxxxx:breaking-news.fifo:24b40a3b-4ef7-4689-8951-a9e606b7859f",\n' +
              '  "MessageAttributes" : {\n' +
              '    "tone" : {"Type":"String.Array","Value":"[\\"cheerful\\",\\"serious\\"]"}\n' +
              "  }\n" +
              "}",
          },
        ],
      });
    }
  }),
};

const SNSClientMock = {
  send: vi.fn((command) => {
    if (command instanceof SubscribeCommand) {
      return Promise.resolve({ SubscriptionArn: "subscription-arn" });
    }
  }),
};

const LoggerMock = {
  log: vi.fn(),
  logSeparator: vi.fn(),
};

describe("TopicsQueuesWkflw", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should have properties for SNS and SQS clients", () => {
    const topicsQueuesWkflw = new TopicsQueuesWkflw(
      new SNSClient({}),
      new SQSClient({}),
    );
    expect(topicsQueuesWkflw.snsClient).toBeTruthy();
    expect(topicsQueuesWkflw.sqsClient).toBeTruthy();
  });

  it("should default to a FIFO topic", () => {
    const topicsQueuesWkflw = new TopicsQueuesWkflw(
      new SNSClient({}),
      new SQSClient({}),
    );
    expect(topicsQueuesWkflw.isFifo).toBe(true);
  });

  it("should default to no auto-dedup", () => {
    const topicsQueuesWkflw = new TopicsQueuesWkflw(
      new SNSClient({}),
      new SQSClient({}),
    );
    expect(topicsQueuesWkflw.autoDedup).toBe(false);
  });

  describe("confirmFifo", () => {
    it("should set isFifo to true if the the user confirms", async () => {
      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        new SNSClient({}),
        new SQSClient({}),
        {
          confirm: () => Promise.resolve(true),
        },
        LoggerMock,
      );

      await topicsQueuesWkflw.confirmFifo();

      expect(topicsQueuesWkflw.isFifo).toBe(true);
    });

    it("should set isFifo to false if the user denies", async () => {
      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        new SNSClient({}),
        new SQSClient({}),
        {
          confirm: () => Promise.resolve(false),
        },
        LoggerMock,
      );

      await topicsQueuesWkflw.confirmFifo();

      expect(topicsQueuesWkflw.isFifo).toBe(false);
    });
  });

  describe("createTopic", () => {
    it("should create a FIFO topic with content deduplication if those were selected", async () => {
      const send = vi.fn(() => {
        return Promise.resolve({
          TopicArn: "topic-arn",
        });
      });
      const mockSnsClient = { send };
      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        mockSnsClient,
        new SQSClient({}),
        {
          confirm: () => Promise.resolve(true),
          input: () => Promise.resolve("user-input"),
        },
        LoggerMock,
      );

      await topicsQueuesWkflw.createTopic();
      // Verify the mocked 'send' function was called with an instance of CreateTopicCommand
      expect(send.mock.calls[0][0]).toBeInstanceOf(CreateTopicCommand);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(send.mock.calls[0][0].input.Attributes.FifoTopic).toBe("true");
    });
  });

  describe("createQueues", () => {
    it("should create two SQS queues", async () => {
      const PrompterMock = {
        input: vi
          .fn()
          .mockImplementationOnce(() => Promise.resolve("queue-one"))
          .mockImplementationOnce(() => Promise.resolve("queue-two")),
      };

      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        new SNSClient({}),
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      await topicsQueuesWkflw.createQueues();
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(SQSClientMock.send.mock.calls[0][0].input.QueueName).toBe(
        "queue-one.fifo",
      );

      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(SQSClientMock.send.mock.calls[2][0].input.QueueName).toBe(
        "queue-two.fifo",
      );
    });
  });

  describe("attachQueueIamPolicies", () => {
    it("should attach a policy to each of the SQS queues", async () => {
      const PrompterMock = {
        confirm: vi.fn(() => Promise.resolve(true)),
      };

      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        new SNSClient({}),
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      topicsQueuesWkflw.queues = [
        {
          queueArn: "queue-one-arn",
          queueUrl: "queue-one-url",
          queueName: "queue-one",
        },
        {
          queueArn: "queue-two-arn",
          queueUrl: "queue-two-url",
          queueName: "queue-two",
        },
      ];

      await topicsQueuesWkflw.attachQueueIamPolicies();

      expect(
        SQSClientMock.send.mock.calls[0][0] instanceof
          SetQueueAttributesCommand,
      ).toBe(true);

      expect(
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        SQSClientMock.send.mock.calls[0][0].input.Attributes.Policy,
      ).toBeTruthy();

      expect(
        SQSClientMock.send.mock.calls[1][0] instanceof
          SetQueueAttributesCommand,
      ).toBe(true);

      expect(
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        SQSClientMock.send.mock.calls[1][0].input.Attributes.Policy,
      ).toBeTruthy();
    });
  });

  describe("subscribeQueuesToTopic", () => {
    it("should exist", () => {
      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        new SNSClient({}),
        new SQSClient({}),
        {},
        LoggerMock,
      );
      expect(topicsQueuesWkflw.subscribeQueuesToTopic).toBeTruthy();
    });

    it("should subscribe each queue to the topic", async () => {
      const PrompterMock = {
        checkbox: vi.fn(() => Promise.resolve(["cheerful", "serious"])),
      };

      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        SNSClientMock,
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      topicsQueuesWkflw.topicArn = "topic-arn";
      topicsQueuesWkflw.topicName = "topic-name";
      topicsQueuesWkflw.queues = [
        {
          queueArn: "queue-one-arn",
          queueUrl: "queue-one-url",
          queueName: "queue-one",
        },
        {
          queueArn: "queue-two-arn",
          queueUrl: "queue-two-url",
          queueName: "queue-two",
        },
      ];

      await topicsQueuesWkflw.subscribeQueuesToTopic();

      topicsQueuesWkflw.queues.forEach((queue, index) => {
        expect(
          SNSClientMock.send.mock.calls[index][0] instanceof SubscribeCommand,
        ).toBe(true);

        expect(
          // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
          SNSClientMock.send.mock.calls[index][0].input.TopicArn,
        ).toBe(topicsQueuesWkflw.topicArn);

        expect(
          // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
          SNSClientMock.send.mock.calls[index][0].input.Protocol,
        ).toBe("sqs");

        expect(
          // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
          SNSClientMock.send.mock.calls[index][0].input.Endpoint,
        ).toBe(queue.queueArn);
      });
    });
  });

  describe("publishMessages", () => {
    it("should exist", () => {
      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        SNSClientMock,
        SQSClientMock,
        {},
        LoggerMock,
      );

      expect(topicsQueuesWkflw.publishMessages).toBeTruthy();
    });

    it("should publish messages until the user says no", async () => {
      const PrompterMock = {
        input: vi.fn().mockImplementationOnce(() => Promise.resolve("message")),
        confirm: vi
          .fn()
          .mockImplementationOnce(() => Promise.resolve(true))
          .mockImplementationOnce(() => Promise.resolve(true))
          .mockImplementationOnce(() => Promise.resolve(false)),
        checkbox: vi
          .fn()
          .mockImplementationOnce(() =>
            Promise.resolve(["cheerful", "serious"]),
          ),
      };

      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        SNSClientMock,
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      await topicsQueuesWkflw.publishMessages();

      expect(SNSClientMock.send.mock.calls.length).toBe(3);
    });

    it("should prompt the user for a group id if FIFO is enabled", async () => {
      const PrompterMock = {
        input: vi
          .fn()
          .mockImplementationOnce(() => Promise.resolve("message"))
          .mockImplementationOnce(() => Promise.resolve("group-id")),
        confirm: vi.fn().mockImplementationOnce(() => Promise.resolve(false)),
        checkbox: vi
          .fn()
          .mockImplementationOnce(() =>
            Promise.resolve(["cheerful", "serious"]),
          ),
      };

      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        SNSClientMock,
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      await topicsQueuesWkflw.publishMessages();

      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(PrompterMock.input.mock.calls[1][0]).toEqual({
        message: MESSAGES.groupIdPrompt,
      });

      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(SNSClientMock.send.mock.calls[0][0].input.MessageGroupId).toBe(
        "group-id",
      );
    });

    it("should not prompt the user for a group id if FIFO is not enabled", async () => {
      const PrompterMock = {
        input: vi.fn().mockImplementationOnce(() => Promise.resolve("message")),
        confirm: vi.fn().mockImplementationOnce(() => Promise.resolve(false)),
      };

      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        SNSClientMock,
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      topicsQueuesWkflw.isFifo = false;

      await topicsQueuesWkflw.publishMessages();

      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(PrompterMock.input.mock.calls[1]).toBeUndefined();
    });

    it("should prompt for a deduplication ID if content deduplication is not enabled", async () => {
      const PrompterMock = {
        input: vi
          .fn()
          .mockImplementationOnce(() => Promise.resolve("message"))
          .mockImplementationOnce(() => Promise.resolve("group-id"))
          .mockImplementationOnce(() => Promise.resolve("dedup-id")),
        confirm: vi.fn().mockImplementationOnce(() => Promise.resolve(false)),
        checkbox: vi
          .fn()
          .mockImplementationOnce(() =>
            Promise.resolve(["cheerful", "serious"]),
          ),
      };

      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        SNSClientMock,
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      topicsQueuesWkflw.autoDedup = false;

      await topicsQueuesWkflw.publishMessages();

      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(PrompterMock.input.mock.calls[2][0]).toEqual({
        message: MESSAGES.deduplicationIdPrompt,
      });

      expect(
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        SNSClientMock.send.mock.calls[0][0].input.MessageDeduplicationId,
      ).toBe("dedup-id");
    });

    it("should not prompt for a deduplication ID if content deduplication is enabled", async () => {
      const PrompterMock = {
        input: vi
          .fn()
          .mockImplementationOnce(() => Promise.resolve("message"))
          .mockImplementationOnce(() => Promise.resolve("group-id")),
        confirm: vi.fn().mockImplementationOnce(() => Promise.resolve(false)),
        checkbox: vi
          .fn()
          .mockImplementationOnce(() =>
            Promise.resolve(["cheerful", "serious"]),
          ),
      };

      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        SNSClientMock,
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      topicsQueuesWkflw.autoDedup = true;

      await topicsQueuesWkflw.publishMessages();

      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(PrompterMock.input.mock.calls[2]).toBeUndefined();

      expect(
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        SNSClientMock.send.mock.calls[0][0].input.MessageDeduplicationId,
      ).toBeUndefined();
    });

    it("should prompt for message attributes", async () => {
      const PrompterMock = {
        input: vi
          .fn()
          .mockImplementationOnce(() => Promise.resolve("message"))
          .mockImplementationOnce(() => Promise.resolve("group-id")),

        confirm: vi.fn().mockImplementationOnce(() => Promise.resolve(false)),
        checkbox: vi
          .fn()
          .mockImplementationOnce(() =>
            Promise.resolve(["cheerful", "serious"]),
          ),
      };

      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        SNSClientMock,
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      await topicsQueuesWkflw.publishMessages();

      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(PrompterMock.checkbox.mock.calls[0][0]).toEqual({
        message: MESSAGES.messageAttributesPrompt,
        choices: [
          { name: "cheerful", value: "cheerful" },
          { name: "funny", value: "funny" },
          { name: "serious", value: "serious" },
          { name: "sincere", value: "sincere" },
        ],
      });

      expect(
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        SNSClientMock.send.mock.calls[0][0].input.MessageAttributes,
      ).toEqual({
        tone: {
          DataType: "String.Array",
          StringValue: '["cheerful","serious"]',
        },
      });
    });
  });

  describe("receiveAndDeleteMessages", () => {
    it("should exist", () => {
      const PrompterMock = {};
      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        SNSClientMock,
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      expect(topicsQueuesWkflw.receiveAndDeleteMessages).toBeTruthy();
    });

    it("should not log anything if there are no queues", async () => {
      const PrompterMock = {
        confirm: vi.fn().mockImplementationOnce(() => Promise.resolve(false)),
      };

      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        SNSClientMock,
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      await topicsQueuesWkflw.receiveAndDeleteMessages();

      expect(LoggerMock.log.mock.calls.length).toBe(0);
    });

    it("should log a message for each queue", async () => {
      const PrompterMock = {
        confirm: vi.fn().mockImplementationOnce(() => Promise.resolve(false)),
      };

      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        SNSClientMock,
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      topicsQueuesWkflw.queues = [
        {
          queueName: "queue-1",
          queueUrl: "queue-1-url",
          queueArn: "queue-1-arn",
        },
        {
          queueName: "queue-2",
          queueUrl: "queue-2-url",
          queueArn: "queue-2-arn",
        },
      ];

      await topicsQueuesWkflw.receiveAndDeleteMessages();

      expect(LoggerMock.log.mock.calls.length).toBe(2);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(LoggerMock.log.mock.calls[0][0]).toBe(
        "The following messages were received by the SQS queue 'queue-1'.",
      );
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(LoggerMock.log.mock.calls[1][0]).toBe(
        "The following messages were received by the SQS queue 'queue-2'.",
      );
    });

    it("should delete any received messages", async () => {
      const PrompterMock = {
        confirm: vi.fn().mockImplementationOnce(() => Promise.resolve(false)),
      };

      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        SNSClientMock,
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      topicsQueuesWkflw.queues = [
        {
          queueName: "queue-1",
          queueUrl: "queue-1-url",
          queueArn: "queue-1-arn",
        },
      ];

      await topicsQueuesWkflw.receiveAndDeleteMessages();
      expect(
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        SQSClientMock.send.mock.calls[1][0].input.Entries[0].ReceiptHandle,
      ).toBe("123");
    });

    it("should prompt the user to poll again until they say no", async () => {
      const PrompterMock = {
        confirm: vi
          .fn()
          .mockImplementationOnce(() => Promise.resolve(true))
          .mockImplementationOnce(() => Promise.resolve(false)),
      };

      const topicsQueuesWkflw = new TopicsQueuesWkflw(
        SNSClientMock,
        SQSClientMock,
        PrompterMock,
        LoggerMock,
      );

      topicsQueuesWkflw.queues = [
        {
          queueName: "queue-1",
          queueUrl: "queue-1-url",
          queueArn: "queue-1-arn",
        },
      ];

      await topicsQueuesWkflw.receiveAndDeleteMessages();
      expect(PrompterMock.confirm.mock.calls.length).toBe(2);
    });
  });
});

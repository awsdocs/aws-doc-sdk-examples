/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi, beforeEach } from "vitest";
import { SNSWorkflow } from "../SNSWorkflow.js";
import { CreateTopicCommand, SNSClient } from "@aws-sdk/client-sns";
import {
  CreateQueueCommand,
  GetQueueAttributesCommand,
  SQSClient,
  SetQueueAttributesCommand,
} from "@aws-sdk/client-sqs";

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
  }),
};

const LoggerMock = {
  log: vi.fn(),
};

describe("SNSWorkflow", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should have properties for SNS and SQS clients", () => {
    const snsWkflw = new SNSWorkflow(new SNSClient({}), new SQSClient({}));
    expect(snsWkflw.snsClient).toBeTruthy();
    expect(snsWkflw.sqsClient).toBeTruthy();
  });

  it("should default to a FIFO topic", () => {
    const snsWkflw = new SNSWorkflow(new SNSClient({}), new SQSClient({}));
    expect(snsWkflw.isFifo).toBe(true);
  });

  it("should default to auto-dedup", () => {
    const snsWkflw = new SNSWorkflow(new SNSClient({}), new SQSClient({}));
    expect(snsWkflw.autoDedup).toBe(true);
  });

  describe("confirmFifo", () => {
    it("should set isFifo to true if the the user confirms", async () => {
      const snsWkflw = new SNSWorkflow(
        new SNSClient({}),
        new SQSClient({}),
        {
          confirm: () => Promise.resolve(true),
        },
        LoggerMock
      );

      await snsWkflw.confirmFifo();

      expect(snsWkflw.isFifo).toBe(true);
    });

    it("should set isFifo to false if the user denies", async () => {
      const snsWkflw = new SNSWorkflow(
        new SNSClient({}),
        new SQSClient({}),
        {
          confirm: () => Promise.resolve(false),
        },
        LoggerMock
      );

      await snsWkflw.confirmFifo();

      expect(snsWkflw.isFifo).toBe(false);
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
      const snsWkflw = new SNSWorkflow(
        mockSnsClient,
        new SQSClient({}),
        {
          confirm: () => Promise.resolve(true),
          input: () => Promise.resolve("user-input"),
        },
        LoggerMock
      );

      await snsWkflw.createTopic();
      // Verify the mocked 'send' function was called with an instance of CreateTopicCommand
      expect(send.mock.calls[0][0]).toBeInstanceOf(CreateTopicCommand);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(send.mock.calls[0][0].input.Attributes.FifoTopic).toBe("true");

      expect(
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        send.mock.calls[0][0].input.Attributes.ContentBasedDeduplication
      ).toBe("true");
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

      const snsWkflw = new SNSWorkflow(
        new SNSClient({}),
        SQSClientMock,
        PrompterMock,
        LoggerMock
      );

      await snsWkflw.createQueues();
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(SQSClientMock.send.mock.calls[0][0].input.QueueName).toBe(
        "queue-one.fifo"
      );

      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      expect(SQSClientMock.send.mock.calls[2][0].input.QueueName).toBe(
        "queue-two.fifo"
      );
    });
  });

  describe("attachQueueIamPolicies", () => {
    it("should attach a policy to each of the SQS queues", async () => {
      const PrompterMock = {
        confirm: vi.fn(() => Promise.resolve(true)),
      };

      const snsWkflw = new SNSWorkflow(
        new SNSClient({}),
        SQSClientMock,
        PrompterMock,
        LoggerMock
      );

      snsWkflw.queues = [
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

      await snsWkflw.attachQueueIamPolicies();

      expect(
        SQSClientMock.send.mock.calls[0][0] instanceof SetQueueAttributesCommand
      ).toBe(true);

      expect(
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        SQSClientMock.send.mock.calls[0][0].input.Attributes.Policy
      ).toBeTruthy();

      expect(
        SQSClientMock.send.mock.calls[1][0] instanceof SetQueueAttributesCommand
      ).toBe(true);

      expect(
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        SQSClientMock.send.mock.calls[1][0].input.Attributes.Policy
      ).toBeTruthy();
    });
  });
});

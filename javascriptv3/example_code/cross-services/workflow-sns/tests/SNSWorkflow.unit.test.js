/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";
import { SNSWorkflow } from "../SNSWorkflow.js";
import { CreateTopicCommand, SNSClient } from "@aws-sdk/client-sns";
import { SQSClient } from "@aws-sdk/client-sqs";

describe("SNSWorkflow", () => {
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
        {
          log: () => {
            /*noop*/
          },
        }
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
        {
          log: () => {
            /*noop*/
          },
        }
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
        {
          log: () => {
            /*noop*/
          },
        }
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
});

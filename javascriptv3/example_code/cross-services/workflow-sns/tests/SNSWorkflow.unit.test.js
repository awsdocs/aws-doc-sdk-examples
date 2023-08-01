/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect } from "vitest";
import { SNSWorkflow } from "../SNSWorkflow.js";
import { SNSClient } from "@aws-sdk/client-sns";
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
});

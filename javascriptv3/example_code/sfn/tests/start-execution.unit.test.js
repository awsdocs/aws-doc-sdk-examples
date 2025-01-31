// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, test, expect, vi } from "vitest";
import { SFNClient } from "@aws-sdk/client-sfn";
import { startExecution } from "../actions/start-execution.js";

const mockSendFn = vi.fn();

class MockSFNClient extends SFNClient {
  send = mockSendFn;
}

describe("sfn: start-execution", () => {
  test("should return the exact response from the client", async () => {
    const resolvedValue = {
      $metadata: {
        httpStatusCode: 200,
        requestId: "202a9309-c16a-454b-adeb-c4d19afe3bf2",
        extendedRequestId: undefined,
        cfId: undefined,
        attempts: 1,
        totalRetryDelay: 0,
      },
      executionArn:
        "arn:aws:states:us-east-1:000000000000:execution:MyStateMachine:aaaaaaaa-f787-49fb-a20c-1b61c64eafe6",
      startDate: "2024-01-04T15:54:08.362Z",
    };

    mockSendFn.mockResolvedValueOnce(resolvedValue);

    const response = await startExecution({
      sfnClient: new MockSFNClient({}),
      stateMachineArn: "ARN",
    });

    expect(response).toEqual(resolvedValue);
  });
});

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect, vi } from "vitest";
import { ListObjectVersionsCommand } from "@aws-sdk/client-s3";

import * as Scenarios from "@aws-doc-sdk-examples/lib/scenario/index.js";

import { cleanupAction } from "./clean.steps.js";

describe("clean.steps.js", () => {
  it("should call ListObjectVersionsCommand once for each bucket", async () => {
    const mockClient = {
      send: vi
        .fn()
        .mockResolvedValueOnce({ Versions: [] }) // ListObjectVersionsCommand
        .mockResolvedValueOnce({}) // DeleteBucketCommand
        .mockResolvedValueOnce({ Versions: [] }) // ListObjectVersionsCommand
        .mockResolvedValueOnce({}), // DeleteBucketCommand
    };

    const state = {
      sourceBucketName: "bucket-no-lock",
      destinationBucketName: "bucket-lock-enabled",
    };

    const action = cleanupAction(Scenarios, mockClient);

    await action.handle(state);

    expect(mockClient.send).toHaveBeenCalledTimes(4);
    expect(mockClient.send).toHaveBeenNthCalledWith(
      1,
      expect.any(ListObjectVersionsCommand),
    );
    expect(mockClient.send).toHaveBeenNthCalledWith(
      3,
      expect.any(ListObjectVersionsCommand),
    );
    expect(mockClient.send).toHaveBeenNthCalledWith(
      3,
      expect.any(ListObjectVersionsCommand),
    );
  });
});

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect, vi } from "vitest";
import { ListObjectVersionsCommand } from "@aws-sdk/client-s3";

import * as Scenarios from "@aws-doc-sdk-examples/lib/scenario/index.js";

import { cleanupAction } from "./clean.steps.js";

describe.skip("clean.steps.js", () => {
  it("should call ListObjectVersionsCommand once for each bucket", async () => {
    const mockClient = {
      send: vi
        .fn()
        .mockResolvedValueOnce({ Versions: [] }) // ListObjectVersionsCommand
        .mockResolvedValueOnce({}) // DeleteBucketCommand
        .mockResolvedValueOnce({ Versions: [] }) // ListObjectVersionsCommand
        .mockResolvedValueOnce({}) // DeleteBucketCommand
        .mockResolvedValueOnce({ Versions: [] }) // ListObjectVersionsCommand
        .mockResolvedValueOnce({}), // DeleteBucketCommand
    };

    const state = {
      noLockBucketName: "bucket-no-lock",
      lockEnabledBucketName: "bucket-lock-enabled",
      retentionBucketName: "bucket-retention",
    };

    const action = cleanupAction(Scenarios, mockClient);

    await action.handle(state);

    expect(mockClient.send).toHaveBeenCalledTimes(6);
    expect(mockClient.send).toHaveBeenNthCalledWith(
      1,
      expect.any(ListObjectVersionsCommand),
    );
    expect(mockClient.send).toHaveBeenNthCalledWith(
      3,
      expect.any(ListObjectVersionsCommand),
    );
    expect(mockClient.send).toHaveBeenNthCalledWith(
      5,
      expect.any(ListObjectVersionsCommand),
    );
  });

  it("should call the DeleteObjectCommand with BypassGovernanceRetention set to true if the Retention Mode is 'GOVERNANCE'", async () => {
    const mockClient = {
      send: vi
        .fn()
        // ListObjectVersionsCommand
        .mockResolvedValueOnce({ Versions: [] })
        // DeleteBucketCommand
        .mockResolvedValueOnce({})
        // ListObjectVersionsCommand
        .mockResolvedValueOnce({ Versions: [] })
        // DeleteBucketCommand
        .mockResolvedValueOnce({})
        // ListObjectVersionsCommand
        .mockResolvedValueOnce({ Versions: [{ Key: "key", VersionId: "id" }] })
        // GetObjectLegalHoldCommand
        .mockResolvedValueOnce({
          LegalHold: {
            Status: "OFF",
          },
        })
        // GetObjectRetentionCommand
        .mockResolvedValueOnce({
          Retention: {
            Mode: "GOVERNANCE",
          },
        })
        // DeleteObjectCommand with BypassGovernanceRetention
        .mockResolvedValueOnce({})
        // DeleteObjectCommand without BypassGovernanceRetention
        .mockResolvedValueOnce({}),
    };

    const state = {
      noLockBucketName: "bucket-no-lock",
      lockEnabledBucketName: "bucket-lock-enabled",
      retentionBucketName: "bucket-retention",
    };

    const action = cleanupAction(Scenarios, mockClient);

    await action.handle(state);

    for (const call of mockClient.send.mock.calls) {
      console.log(call);
    }

    expect(mockClient.send).toHaveBeenCalledWith(
      expect.objectContaining({
        input: {
          Bucket: state.retentionBucketName,
          Key: "key",
          VersionId: "id",
          BypassGovernanceRetention: true,
        },
      }),
    );
  });
});

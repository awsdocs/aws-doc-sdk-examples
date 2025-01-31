// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect, vi, afterEach } from "vitest";
import { ChecksumAlgorithm } from "@aws-sdk/client-s3";
import * as Scenarios from "@aws-doc-sdk-examples/lib/scenario/index.js";
import {
  createBucketsAction,
  populateBucketsAction,
  updateRetentionAction,
  updateLockPolicyAction,
} from "./setup.steps.js";

describe.skip("setup.steps.js", () => {
  const mockClient = {
    send: vi.fn(),
  };

  const state = {
    noLockBucketName: "js-object-locking-no-lock",
    lockEnabledBucketName: "js-object-locking-lock-enabled",
    retentionBucketName: "js-object-locking-retention-after-creation",
  };

  afterEach(() => {
    vi.resetAllMocks();
  });

  describe.skip("createBucketsAction", () => {
    it("should create three buckets with the correct configurations", async () => {
      const action = createBucketsAction(Scenarios, mockClient);
      await action.handle(state);

      expect(mockClient.send).toHaveBeenCalledTimes(3);
      expect(mockClient.send).toHaveBeenCalledWith(
        expect.objectContaining({
          input: {
            Bucket: state.noLockBucketName,
          },
        }),
      );
      expect(mockClient.send).toHaveBeenCalledWith(
        expect.objectContaining({
          input: {
            Bucket: state.lockEnabledBucketName,
            ObjectLockEnabledForBucket: true,
          },
        }),
      );
      expect(mockClient.send).toHaveBeenCalledWith(
        expect.objectContaining({
          input: {
            Bucket: state.retentionBucketName,
          },
        }),
      );
    });
  });

  describe.skip("populateBucketsAction", () => {
    it("should upload six files to the three buckets", async () => {
      const action = populateBucketsAction(Scenarios, mockClient);
      await action.handle(state);

      expect(mockClient.send).toHaveBeenCalledTimes(6);
      for (const stateKey in state) {
        for (const fileName of ["file0.txt", "file1.txt"]) {
          expect(mockClient.send).toHaveBeenCalledWith(
            expect.objectContaining({
              input: {
                Bucket: state[stateKey],
                Key: fileName,
                Body: "Content",
                ChecksumAlgorithm: ChecksumAlgorithm.SHA256,
              },
            }),
          );
        }
      }
    });
  });

  describe.skip("updateRetentionAction", () => {
    it("should enable versioning and set a retention period on the retention bucket", async () => {
      const action = updateRetentionAction(Scenarios, mockClient);
      await action.handle(state);

      expect(mockClient.send).toHaveBeenCalledTimes(2);
      expect(mockClient.send).toHaveBeenCalledWith(
        expect.objectContaining({
          input: {
            Bucket: state.retentionBucketName,
            VersioningConfiguration: {
              MFADelete: "Disabled",
              Status: "Enabled",
            },
          },
        }),
      );
      expect(mockClient.send).toHaveBeenCalledWith(
        expect.objectContaining({
          input: {
            Bucket: state.retentionBucketName,
            ObjectLockConfiguration: {
              ObjectLockEnabled: "Enabled",
              Rule: {
                DefaultRetention: {
                  Mode: "GOVERNANCE",
                  Years: 1,
                },
              },
            },
          },
        }),
      );
    });
  });

  describe.skip("updateLockPolicyAction", () => {
    it("should add an object lock policy to the lock-enabled bucket", async () => {
      const action = updateLockPolicyAction(Scenarios, mockClient);
      await action.handle(state);

      expect(mockClient.send).toHaveBeenCalledTimes(1);
      expect(mockClient.send).toHaveBeenCalledWith(
        expect.objectContaining({
          input: {
            Bucket: state.lockEnabledBucketName,
            ObjectLockConfiguration: {
              ObjectLockEnabled: "Enabled",
            },
          },
        }),
      );
    });
  });
});

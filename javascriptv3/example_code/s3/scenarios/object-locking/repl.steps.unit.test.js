// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import * as Scenarios from "@aws-doc-sdk-examples/lib/scenario/index.js";
import { choices, replAction, replInput } from "./repl.steps.js";
import { ChecksumAlgorithm } from "@aws-sdk/client-s3";

describe.skip("repl.steps.js", () => {
  const mockClient = {
    send: vi.fn(),
  };

  const state = {
    noLockBucketName: "bucket-no-lock",
    lockEnabledBucketName: "bucket-lock-enabled",
    retentionBucketName: "bucket-retention",
  };

  describe.skip("replInput", () => {
    it("should create a ScenarioInput with the correct choices", () => {
      const input = replInput(Scenarios);
      expect(input).toBeInstanceOf(Scenarios.ScenarioInput);
      expect(input.stepOptions.choices).toHaveLength(7);
      expect(input.stepOptions.choices.map((c) => c.value)).toEqual([
        1, 2, 3, 4, 5, 6, 0,
      ]);
    });
  });

  describe.skip("replAction", () => {
    beforeEach(() => {
      mockClient.send.mockReset();
    });

    it("should call ListObjectVersionsCommand for each bucket", async () => {
      const handleMock = vi
        .fn()
        .mockImplementationOnce(
          (/** @type { Record<string, any> } */ state) => {
            state.replChoice = choices.LIST_ALL_FILES;
            return choices.LIST_ALL_FILES;
          },
        )
        .mockImplementation((/** @type { Record<string, any> } */ state) => {
          state.replChoice = choices.EXIT;
          return choices.EXIT;
        });

      const scenarios = {
        ...Scenarios,
        ScenarioInput: () => ({
          handle: handleMock,
        }),
      };
      const action = replAction(scenarios, mockClient);
      mockClient.send.mockResolvedValue({ Versions: [] });

      await action.handle(state);

      expect(mockClient.send).toHaveBeenCalledTimes(6);
      expect(mockClient.send).toHaveBeenNthCalledWith(
        1,
        expect.objectContaining({
          input: expect.objectContaining({ Bucket: state.noLockBucketName }),
        }),
      );
      expect(mockClient.send).toHaveBeenNthCalledWith(
        2,
        expect.objectContaining({
          input: expect.objectContaining({
            Bucket: state.lockEnabledBucketName,
          }),
        }),
      );
      expect(mockClient.send).toHaveBeenNthCalledWith(
        3,
        expect.objectContaining({
          input: expect.objectContaining({ Bucket: state.retentionBucketName }),
        }),
      );
    });

    it("should call DeleteObjectCommand when replChoice is choices.DELETE_FILE", async () => {
      const handleMock = vi
        .fn()
        .mockImplementationOnce(
          (/** @type { Record<string, any> } */ state) => {
            state.replChoice = choices.DELETE_FILE;
            return choices.DELETE_FILE;
          },
        )
        .mockImplementationOnce(
          (/** @type { Record<string, any> } */ state) => {
            state.selectedFile = 0;
            return state.selectedFile;
          },
        )
        .mockImplementation((/** @type { Record<string, any> } */ state) => {
          state.replChoice = 0;
          return 0;
        });

      const scenarios = {
        ...Scenarios,
        ScenarioInput: () => ({
          handle: handleMock,
        }),
      };
      const action = replAction(scenarios, mockClient);
      mockClient.send
        .mockResolvedValueOnce({ Versions: [{ Key: "key", VersionId: "id" }] })
        .mockResolvedValueOnce({ Versions: [] })
        .mockResolvedValueOnce({ Versions: [] });

      state.replChoice = choices.DELETE_FILE;
      await action.handle(state);

      expect(mockClient.send).toHaveBeenCalledWith(
        expect.objectContaining({
          input: expect.objectContaining({
            Bucket: state.noLockBucketName,
            Key: "key",
            VersionId: "id",
          }),
        }),
      );
    });

    it("should call DeleteObjectCommand with BypassGovernanceRetention set to true when replChoice is choices.DELETE_FILE_WITH_RETENTION", async () => {
      const handleMock = vi
        .fn()
        .mockImplementationOnce(
          (/** @type { Record<string, any> } */ state) => {
            state.replChoice = choices.DELETE_FILE_WITH_RETENTION;
            return choices.DELETE_FILE_WITH_RETENTION;
          },
        )
        .mockImplementationOnce(
          (/** @type { Record<string, any> } */ state) => {
            state.selectedFile = 0;
            return state.selectedFile;
          },
        )
        .mockImplementation((/** @type { Record<string, any> } */ state) => {
          state.replChoice = choices.EXIT;
          return choices.EXIT;
        });

      const scenarios = {
        ...Scenarios,
        ScenarioInput: () => ({
          handle: handleMock,
        }),
      };

      const action = replAction(scenarios, mockClient);
      mockClient.send
        .mockResolvedValueOnce({ Versions: [{ Key: "key", VersionId: "id" }] })
        .mockResolvedValueOnce({ Versions: [{ Key: "key", VersionId: "id" }] })
        .mockResolvedValue({});

      await action.handle(state);

      expect(mockClient.send).toHaveBeenCalledWith(
        expect.objectContaining({
          input: expect.objectContaining({
            Bucket: state.noLockBucketName,
            Key: "key",
            VersionId: "id",
            BypassGovernanceRetention: true,
          }),
        }),
      );
    });

    it("should handle replChoice choices.OVERWRITE_FILE", async () => {
      const handleMock = vi
        .fn()
        .mockImplementationOnce(
          (/** @type { Record<string, any> } */ state) => {
            state.replChoice = choices.OVERWRITE_FILE;
            return choices.OVERWRITE_FILE;
          },
        )
        .mockImplementationOnce(
          (/** @type { Record<string, any> } */ state) => {
            state.selectedFile = 0;
            return state.selectedFile;
          },
        )
        .mockImplementation((/** @type { Record<string, any> } */ state) => {
          state.replChoice = choices.EXIT;
          return choices.EXIT;
        });

      const scenarios = {
        ...Scenarios,
        ScenarioInput: () => ({
          handle: handleMock,
        }),
      };

      const action = replAction(scenarios, mockClient);
      mockClient.send
        .mockResolvedValueOnce({ Versions: [{ Key: "key", VersionId: "id" }] })
        .mockResolvedValueOnce({ Versions: [] })
        .mockResolvedValueOnce({ Versions: [] });

      await action.handle(state);

      expect(mockClient.send).toHaveBeenCalledWith(
        expect.objectContaining({
          input: expect.objectContaining({
            Bucket: state.noLockBucketName,
            Key: "key",
            Body: "New content",
            ChecksumAlgorithm: ChecksumAlgorithm.SHA256,
          }),
        }),
      );
    });
    it("should handle replChoice choices.VIEW_RETENTION_SETTINGS", async () => {
      const handleMock = vi
        .fn()
        .mockImplementationOnce(
          (/** @type { Record<string, any> } */ state) => {
            state.replChoice = choices.VIEW_RETENTION_SETTINGS;
            return choices.VIEW_RETENTION_SETTINGS;
          },
        )
        .mockImplementationOnce(
          (/** @type { Record<string, any> } */ state) => {
            state.selectedFile = 0;
            return state.selectedFile;
          },
        )
        .mockImplementation((/** @type { Record<string, any> } */ state) => {
          state.replChoice = choices.EXIT;
          return choices.EXIT;
        });

      const scenarios = {
        ...Scenarios,
        ScenarioInput: () => ({
          handle: handleMock,
        }),
      };

      const action = replAction(scenarios, mockClient);
      mockClient.send
        .mockResolvedValueOnce({ Versions: [{ Key: "key", VersionId: "id" }] })
        .mockResolvedValueOnce({ Versions: [] })
        .mockResolvedValueOnce({ Versions: [] })
        .mockResolvedValueOnce({
          Retention: {
            Mode: "GOVERNANCE",
            RetainUntilDate: new Date("2024-02-28T00:00:00Z"),
          },
        })
        .mockResolvedValueOnce({
          ObjectLockConfiguration: {
            ObjectLockEnabled: "Enabled",
            Rule: {
              DefaultRetention: {
                Mode: "GOVERNANCE",
                Years: 1,
              },
            },
          },
        })
        .mockResolvedValue({ Versions: [] });

      await action.handle(state);

      expect(state.replOutput).toContain(
        "Object retention for key in bucket-no-lock: GOVERNANCE until 2024-02-28",
      );
    });
    it("should handle replChoice choices.VIEW_LEGAL_HOLD_SETTINGS", async () => {
      const handleMock = vi
        .fn()
        .mockImplementationOnce(
          (/** @type { Record<string, any> } */ state) => {
            state.replChoice = choices.VIEW_LEGAL_HOLD_SETTINGS;
            return choices.VIEW_LEGAL_HOLD_SETTINGS;
          },
        )
        .mockImplementationOnce(
          (/** @type { Record<string, any> } */ state) => {
            state.selectedFile = 0;
            return state.selectedFile;
          },
        )
        .mockImplementation((/** @type { Record<string, any> } */ state) => {
          state.replChoice = choices.EXIT;
          return choices.EXIT;
        });

      const scenarios = {
        ...Scenarios,
        ScenarioInput: () => ({
          handle: handleMock,
        }),
      };

      const action = replAction(scenarios, mockClient);
      mockClient.send
        .mockResolvedValueOnce({ Versions: [{ Key: "key", VersionId: "id" }] })
        .mockResolvedValueOnce({ Versions: [] })
        .mockResolvedValueOnce({ Versions: [] })
        .mockResolvedValueOnce({
          LegalHold: {
            Status: "ON",
          },
        })
        .mockResolvedValue({ Versions: [] });

      await action.handle(state);

      expect(state.replOutput).toContain(
        "Object legal hold for key in bucket-no-lock: Status: ON",
      );
    });
  });
});

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";

const send = vi.fn();

vi.doMock("@aws-sdk/client-s3", async () => {
  const actual = await vi.importActual("@aws-sdk/client-s3");
  return {
    ...actual,
    S3Client: class {
      send = send;
    },
  };
});

import { createBucket, uploadFilesToBucket } from "../scenarios/basic.js";

describe("S3 basic scenario", () => {
  const logSpy = vi.spyOn(console, "log");

  describe("createBucket", () => {
    it("should log a success message", async () => {
      send.mockResolvedValueOnce({});

      await createBucket("my-bucket");

      expect(logSpy).toHaveBeenCalledWith("Bucket created successfully.");
    });
  });

  describe("uploadFilesToBucket", () => {
    it("should log the files that were found and uploaded", async () => {
      send.mockResolvedValueOnce({});
    })
  });
});

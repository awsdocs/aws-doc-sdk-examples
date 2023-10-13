/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi, beforeEach } from "vitest";

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

vi.doMock("fs", async () => {
  const actual = await vi.importActual("fs");
  return {
    ...actual,
    readdirSync: () => ["file1.txt", "file2.txt"],
    readFileSync: () => "file content",
  };
});

vi.doMock("@aws-sdk-examples/libs/utils/util-io.js", async () => {
  const actual = await vi.importActual(
    "@aws-sdk-examples/libs/utils/util-io.js",
  );
  return {
    ...actual,
    promptForText: () => Promise.resolve("my-bucket"),
  };
});

const {
  createBucket,
  deleteBucket,
  emptyBucket,
  listFilesInBucket,
  uploadFilesToBucket,
} = await import("../scenarios/basic.js");

describe("S3 basic scenario", () => {
  const logSpy = vi.spyOn(console, "log");

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("createBucket", () => {
    it("should log a success message", async () => {
      send.mockResolvedValueOnce({});

      await createBucket("my-bucket");

      expect(logSpy).toHaveBeenCalledWith("Bucket created successfully.\n");
    });
  });

  describe("uploadFilesToBucket", () => {
    it("should send the files to s3", async () => {
      send.mockResolvedValueOnce({});

      await uploadFilesToBucket({ bucketName: "my-bucket", folderPath: "" });

      expect(send).toHaveBeenCalledWith(
        expect.objectContaining({
          input: expect.objectContaining({
            Bucket: "my-bucket",
            Key: "file1.txt",
            Body: "file content",
          }),
        }),
      );
    });

    it("should log the files that were found and uploaded", async () => {
      send.mockResolvedValueOnce({});

      await uploadFilesToBucket({ bucketName: "my-bucket", folderPath: "" });

      expect(logSpy).toHaveBeenCalledWith("file1.txt uploaded successfully.");
    });
  });

  describe("listFilesInBucket", () => {
    it("should list the files in the bucket", async () => {
      send.mockResolvedValueOnce({
        Contents: [{ Key: "file1" }, { Key: "file2" }],
      });

      await listFilesInBucket({ bucketName: "my-bucket", folderPath: "" });

      expect(logSpy).toHaveBeenCalledWith(` • file1\n • file2\n`);
    });
  });

  describe("emptyBucket", () => {
    it("should call 'send' with the keys returned from ListObjects", async () => {
      send.mockResolvedValueOnce({
        Contents: [{ Key: "file1" }, { Key: "file2" }],
      });

      await emptyBucket({ bucketName: "my-bucket", folderPath: "" });

      expect(send).toHaveBeenNthCalledWith(
        2,
        expect.objectContaining({
          input: expect.objectContaining({
            Bucket: "my-bucket",
            Delete: {
              Objects: [{ Key: "file1" }, { Key: "file2" }],
            },
          }),
        }),
      );
    });
  });

  describe("deleteBucket", () => {
    it("should call 'send' with the provided bucket name", async () => {
      send.mockResolvedValueOnce({});

      await deleteBucket({ bucketName: "my-bucket" });

      expect(send).toHaveBeenCalledWith(
        expect.objectContaining({
          input: expect.objectContaining({
            Bucket: "my-bucket",
          }),
        }),
      );
    });
  });
});

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { S3ServiceException } from "@aws-sdk/client-s3";
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

const { main } = await import("../actions/put-bucket-acl.js");

describe("put-bucket-acl", () => {
  const bucketName = "amzn-s3-demo-bucket";
  const granteeCanonicalUserId = "canonical-id-1";
  const ownerCanonicalUserId = "canonical-id-2";

  it("should log the successful response from the service", async () => {
    send.mockResolvedValue({ $metadata: { httpStatusCode: 200 } });

    const spy = vi.spyOn(console, "log");

    await main({
      bucketName,
      granteeCanonicalUserId,
      ownerCanonicalUserId,
    });

    expect(spy).toHaveBeenCalledWith(`Granted READ access to ${bucketName}`);
  });

  it("should log a relevant error when the bucket doesn't exist", async () => {
    const error = new S3ServiceException("The specified bucket does not exist");
    error.$fault = "server"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    error.$metadata = "metadata"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    error.name = "NoSuchBucket";
    send.mockRejectedValueOnce(error);

    const spy = vi.spyOn(console, "error");

    await main({
      bucketName,
      granteeCanonicalUserId,
      ownerCanonicalUserId,
    });

    expect(spy).toHaveBeenCalledWith(
      `Error from S3 while setting ACL for bucket ${bucketName}. The bucket doesn't exist.`,
    );
  });

  it("should indicate a failure came from S3 when the error isn't generic", async () => {
    const error = new S3ServiceException({
      message: "Some S3 service exception",
    });
    error.$fault = "server"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    error.$metadata = "metadata"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    error.name = "ServiceException";
    send.mockRejectedValueOnce(error);

    const spy = vi.spyOn(console, "error");

    await main({
      bucketName,
      granteeCanonicalUserId,
      ownerCanonicalUserId,
    });

    expect(spy).toHaveBeenCalledWith(
      `Error from S3 while setting ACL for bucket ${bucketName}. ServiceException: Some S3 service exception`,
    );
  });

  it("should throw errors that are not S3 specific", async () => {
    send.mockRejectedValueOnce(new Error());

    await expect(() =>
      main({
        bucketName,
        granteeCanonicalUserId,
        ownerCanonicalUserId,
      }),
    ).rejects.toBeTruthy();
  });
});

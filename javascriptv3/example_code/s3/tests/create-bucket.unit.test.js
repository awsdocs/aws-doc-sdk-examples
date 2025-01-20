// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {
  BucketAlreadyExists,
  BucketAlreadyOwnedByYou,
} from "@aws-sdk/client-s3";
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

const { main } = await import("../actions/create-bucket.js");

describe("create-bucket", () => {
  it("should log the response from the service", async () => {
    send.mockResolvedValue({ Location: "foo" });

    const spy = vi.spyOn(console, "log");

    await main({ bucketName: "amzn-s3-demo-bucket" });

    expect(spy).toHaveBeenCalledWith("Bucket created with location foo");
  });

  it("should log a relevant error message if a bucket already exists globally", async () => {
    const error = new BucketAlreadyExists();
    error.$fault = "server"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    error.$metadata = "metadata"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    send.mockRejectedValue(error);

    const spy = vi.spyOn(console, "error");

    await main({ bucketName: "amzn-s3-demo-bucket" });

    expect(spy).toHaveBeenCalledWith(
      `The bucket "amzn-s3-demo-bucket" already exists in another AWS account. Bucket names must be globally unique.`,
    );
  });

  it("should log a relevant error message if a bucket already exists in the users AWS account", async () => {
    const error = new BucketAlreadyOwnedByYou();
    error.$fault = "server"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    error.$metadata = "metadata"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    send.mockRejectedValue(error);

    const spy = vi.spyOn(console, "error");

    await main({ bucketName: "amzn-s3-demo-bucket" });

    expect(spy).toHaveBeenCalledWith(
      `The bucket "amzn-s3-demo-bucket" already exists in this AWS account.`,
    );
  });
});

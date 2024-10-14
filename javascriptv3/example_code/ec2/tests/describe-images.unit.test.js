// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi } from "vitest";

const paginateDescribeImages = vi.fn();

vi.doMock("@aws-sdk/client-ec2", async () => {
  const actual = await vi.importActual("@aws-sdk/client-ec2");
  return {
    ...actual,
    paginateDescribeImages,
  };
});

const { main } = await import("../actions/describe-images.js");

describe("describe-images", () => {
  it("should log the arm64 images", async () => {
    const logSpy = vi.spyOn(console, "log");
    paginateDescribeImages.mockImplementationOnce(async function* () {
      yield {
        Images: [
          {
            ImageId: "12345",
            Architecture: "arm64",
            Name: "Fake image",
          },
        ],
      };
    });

    await main({ architecture: "arm64", pageSize: 100 });

    expect(logSpy).toHaveBeenCalledWith("Found 1 images:\n\nFake image\n");
  });

  it("should log InvalidParameter errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("Retrieval failed");
    error.name = "InvalidParameterValue";

    paginateDescribeImages.mockReturnValueOnce(
      // biome-ignore lint/correctness/useYield: Mock generator function
      (async function* () {
        throw error;
      })(),
    );

    await main({ architecture: "arm64", pageSize: "100" });

    expect(logSpy).toHaveBeenCalledWith(error.message);
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Retrieval failed");

    paginateDescribeImages.mockReturnValueOnce(
      // biome-ignore lint/correctness/useYield: Mock generator function
      (async function* () {
        throw error;
      })(),
    );

    await expect(() =>
      main({ architecture: "arm64", pageSize: "100" }),
    ).rejects.toBe(error);
  });
});

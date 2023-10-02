/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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
          },
        ],
      };
    });

    await main();

    expect(logSpy).toHaveBeenCalledWith([
      {
        ImageId: "12345",
        Architecture: "arm64",
      },
    ]);
  });

  it("should log an error if retrieval fails", async () => {
    const logSpy = vi.spyOn(console, "log");
    const errorSpy = vi.spyOn(console, "error");
    paginateDescribeImages.mockReturnValueOnce(
      // eslint-disable-next-line require-yield
      (async function* () {
        throw new Error("Retrieval failed");
      })(),
    );

    await main();

    expect(logSpy).not.toHaveBeenCalled();
    expect(errorSpy).toHaveBeenCalledWith(new Error("Retrieval failed"));
  });
});

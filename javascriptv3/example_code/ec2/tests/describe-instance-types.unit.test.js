// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi } from "vitest";

const paginateDescribeInstanceTypes = vi.fn();

vi.doMock("@aws-sdk/client-ec2", async () => {
  const actual = await vi.importActual("@aws-sdk/client-ec2");
  return {
    ...actual,
    paginateDescribeInstanceTypes,
  };
});

const { main } = await import("../actions/describe-instance-types.js");

describe("describe-instance-types", () => {
  it("should log the arm64 instances", async () => {
    const logSpy = vi.spyOn(console, "log");
    paginateDescribeInstanceTypes.mockImplementationOnce(async function* () {
      yield {
        InstanceTypes: [
          {
            InstanceType: "t2.micro",
            MemoryInfo: { SizeInMiB: "1024" },
          },
        ],
      };
    });

    await main({ pageSize: "25", freeTier: true, supportedArch: ["arm64"] });

    expect(logSpy).toHaveBeenCalledWith(
      "Memory size in MiB for matching instance types:\n\nt2.micro: 1024 MiB",
    );
  });

  it("should log InvalidParameter errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("Retrieval failed");
    error.name = "InvalidParameterValue";

    paginateDescribeInstanceTypes.mockReturnValueOnce(
      // biome-ignore lint/correctness/useYield: Mock generator
      (async function* () {
        throw error;
      })(),
    );

    await main({ architecture: "arm64", pageSize: "100" });

    expect(logSpy).toHaveBeenCalledWith(error.message);
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Retrieval failed");

    paginateDescribeInstanceTypes.mockReturnValueOnce(
      // biome-ignore lint/correctness/useYield: Mock generator
      (async function* () {
        throw error;
      })(),
    );

    await expect(() =>
      main({ architecture: "arm64", pageSize: "100" }),
    ).rejects.toBe(error);
  });
});

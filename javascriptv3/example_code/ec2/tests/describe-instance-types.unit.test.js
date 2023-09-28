/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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
          },
        ],
      };
    });

    await main();

    expect(logSpy).toHaveBeenCalledWith([
      {
        InstanceType: "t2.micro",
      },
    ]);
  });

  it("should log an error if retrieval fails", async () => {
    const logSpy = vi.spyOn(console, "log");
    const errorSpy = vi.spyOn(console, "error");
    paginateDescribeInstanceTypes.mockReturnValueOnce(
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

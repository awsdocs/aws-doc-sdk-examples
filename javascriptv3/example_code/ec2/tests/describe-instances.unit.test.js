// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi } from "vitest";

const send = vi.fn();

const paginateDescribeInstances = vi.fn();

vi.doMock("@aws-sdk/client-ec2", async () => {
  const actual = await vi.importActual("@aws-sdk/client-ec2");
  return {
    ...actual,
    paginateDescribeInstances,
  };
});

const { main } = await import("../actions/describe-instances.js");

describe("describe-instances", () => {
  it("should log found instances", async () => {
    const logSpy = vi.spyOn(console, "log");
    paginateDescribeInstances.mockImplementationOnce(async function* () {
      yield {
        Reservations: [
          {
            Instances: [
              {
                InstanceId: "123",
              },
            ],
          },
        ],
      };
    });
    send.mockResolvedValueOnce({
      Reservations: [
        {
          Instances: [
            {
              InstanceId: "123",
            },
          ],
        },
      ],
    });

    await main({ architectures: ["x86_64"] });

    expect(logSpy).toHaveBeenCalledWith(
      "Running instances launched this month:\n\n123",
    );
  });

  it("should log InvalidParameterValue errors", async () => {
    const error = new Error("Failed to describe instances");
    error.name = "InvalidParameterName";

    paginateDescribeInstances.mockReturnValueOnce(
      // biome-ignore lint/correctness/useYield: Mock generator function
      (async function* () {
        throw error;
      })(),
    );

    await expect(() => main({ architectures: ["x86_64"] })).rejects.toBe(error);
  });
});

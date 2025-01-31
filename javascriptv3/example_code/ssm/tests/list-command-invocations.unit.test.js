// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";

const paginateListCommandInvocationsMock = vi.fn();

vi.doMock("@aws-sdk/client-ssm", async () => {
  const actual = await vi.importActual("@aws-sdk/client-ssm");
  return {
    ...actual,
    paginateListCommandInvocations: paginateListCommandInvocationsMock,
  };
});

const { main } = await import("../actions/list-command-invocations.js");

describe("listCommandInvocations", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should list command invocations successfully", async () => {
    const mockCommandInvocations = [
      { CommandId: "123", InstanceId: "i-12345678" },
      { CommandId: "456", InstanceId: "i-98765432" },
    ];

    paginateListCommandInvocationsMock.mockImplementationOnce(
      async function* () {
        yield { CommandInvocations: mockCommandInvocations };
      },
    );

    const result = await main({ instanceId: "i-12345678" });

    expect(result.CommandInvocations).toEqual(mockCommandInvocations);
  });

  it("should handle ValidationError", async () => {
    const mockError = new Error("ValidationError: Invalid instance ID.");
    mockError.name = "ValidationError";

    const consoleWarnSpy = vi.spyOn(console, "warn");

    paginateListCommandInvocationsMock.mockReturnValueOnce(
      // biome-ignore lint/correctness/useYield: Mock generator
      (async function* () {
        throw mockError;
      })(),
    );

    await expect(main({ instanceId: "invalid-instance-id" })).rejects.toThrow(
      mockError,
    );

    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. Did you provide a valid instance ID?`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");

    paginateListCommandInvocationsMock.mockReturnValueOnce(
      // biome-ignore lint/correctness/useYield: Mock generator
      (async function* () {
        throw mockError;
      })(),
    );

    await expect(main({})).rejects.toThrow(mockError);
  });
});

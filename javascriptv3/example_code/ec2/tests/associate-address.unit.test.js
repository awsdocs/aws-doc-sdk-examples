/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";

const send = vi.fn();

vi.doMock("@aws-sdk/client-ec2", async () => {
  const actual = await vi.importActual("@aws-sdk/client-ec2");
  return {
    ...actual,
    EC2Client: class {
      send = send;
    },
  };
});

const { main } = await import("../actions/associate-address.js");

describe("associate-address", () => {
  it("should log the details of the new association", async () => {
    const logSpy = vi.spyOn(console, "log");

    send.mockResolvedValueOnce({ AssociationId: "foo" });

    await main();

    expect(logSpy).toHaveBeenCalledWith(
      "Address with allocation ID ALLOCATION_ID is now associated with instance INSTANCE_ID.",
      "The association ID is foo.",
    );
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed to associate address"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(
      new Error("Failed to associate address"),
    );
  });
});

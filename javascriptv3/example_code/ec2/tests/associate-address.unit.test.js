// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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

    await main({ allocationId: "ALLOCATION_ID", instanceId: "INSTANCE_ID" });

    expect(logSpy).toHaveBeenCalledWith(
      "Address with allocation ID ALLOCATION_ID is now associated with instance INSTANCE_ID.",
      "The association ID is foo.",
    );
  });

  it("should log InvalidAllocationID.NotFound error", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("Invalid id");
    error.name = "InvalidAllocationID.NotFound";
    send.mockRejectedValueOnce(error);

    await main({});

    expect(logSpy).toHaveBeenCalledWith(
      "Invalid id. Did you provide the ID of a valid Elastic IP address AllocationId?",
    );
  });

  it("should throw unknown errors", async () => {
    const error = new Error();
    send.mockRejectedValueOnce(error);

    await expect(() => main({})).rejects.toBe(error);
  });
});

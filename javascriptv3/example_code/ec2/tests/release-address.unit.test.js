/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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

import { main } from "../actions/release-address.js";

describe("release-address", () => {
  it("should log a success message", async () => {
    const logSpy = vi.spyOn(console, "log");

    send.mockResolvedValueOnce({});

    await main();

    expect(logSpy).toHaveBeenCalledWith("Successfully released address.");
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed to release address"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(
      new Error("Failed to release address")
    );
  });
});

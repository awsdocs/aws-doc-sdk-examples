/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi, beforeEach } from "vitest";

const send = vi.fn();

vi.doMock("@aws-sdk/client-support", async () => {
  const actual = await vi.importActual("@aws-sdk/client-support");
  return {
    ...actual,
    SupportClient: class {
      send = send;
    },
  };
});

const { main } = await import("../hello.js");

describe("hello service", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should log a count of services if the user has support access", async () => {
    // Mock the SupportClient to return a list of services.
    send.mockResolvedValue({
      services: [
        { code: "service1", name: "Service 1" },
        { code: "service2", name: "Service 2" },
      ],
    });

    const spy = vi.spyOn(console, "log");

    await main();

    expect(spy).toHaveBeenCalledWith(
      "Hello, AWS Support! There are 2 services available.",
    );
  });

  it("should throw an error if the user does not have support access", async () => {
    // Mock the SupportClient to throw an error.
    send.mockRejectedValue(
      new Error(
        "You must be subscribed to the AWS Support plan to use this feature.",
      ),
    );

    const spy = vi.spyOn(console, "error");

    await main();

    expect(spy).toHaveBeenCalledWith(
      "Failed to get service count: ",
      "You must be subscribed to the AWS Support plan to use this feature.",
    );
  });
});

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { vi, describe, it, expect } from "vitest";
import { main as hello } from "../hello.js";

describe("hello", () => {
  it("should log something and not throw an error", async () => {
    const consoleSpy = vi.spyOn(console, "log");
    await hello();
    expect(consoleSpy).toHaveBeenCalled();
  });
});

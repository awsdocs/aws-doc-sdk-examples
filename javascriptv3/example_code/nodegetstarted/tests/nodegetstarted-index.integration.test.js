// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { test, vi } from "vitest";
import { main } from "../index.js";

vi.mock("readline/promises", () => {
  return {
    createInterface: () => {
      return {
        question: vi.fn(() => Promise.resolve("y")),
        close: vi.fn(),
      };
    },
  };
});

test("getting started example should run", async () => {
  await main();
});

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { test, expect } from "vitest";
import { splitDateRange } from "../utils/util-date.js";

test("splitDateRange should take a date range and create two new ones with a 1ms offset", () => {
  const range = [
    new Date("2023-12-25T00:00:00Z"),
    new Date("2024-01-01T00:00:00Z"),
  ];
  const newRanges = splitDateRange(range);
  expect(newRanges).toEqual([
    [
      new Date("2023-12-25T00:00:00.000Z"),
      new Date("2023-12-28T12:00:00.000Z"),
    ],
    [
      new Date("2023-12-28T12:00:00.001Z"),
      new Date("2024-01-01T00:00:00.000Z"),
    ],
  ]);
});

import { describe, test, expect } from "vitest";

import { startsWith } from "@aws-sdk-examples/libs/utils/util-array.js";

describe("startsWith", () => {
  test.each([
    [["a", "b", "c"], ["a", "b", "c", "d"], true],
    [["a"], ["a", "b", "c"], true],
    [["a", "b", "c"], ["d", "a", "b", "c", "e"], false],
    [["abc", "ba", "ca"], ["abc", "ba", "ca", "d"], true],
    [["a", "b", "c"], ["a", "b"], false],
  ])("startsWith(%s)(%s) -> %s", (prefix, list, expected) => {
    expect(startsWith(prefix)(list)).toBe(expected);
  });
});

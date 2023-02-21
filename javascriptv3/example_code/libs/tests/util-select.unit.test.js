import { describe, it, expect } from "vitest";

import { select } from '../utils/util-select.js'

describe("select", () => {
  it("should resolve with autoSelected option if it's valid", async () => {
    const [selected, option] = await select(["foo", "bar"], "", 1);
    expect(selected).toEqual(0);
    expect(option).toEqual("foo");
  });
});

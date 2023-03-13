import { describe, it, expect } from "vitest";

import { promptToSelect } from '../utils/util-io.js'

describe("promptToSelect", () => {
  it("should resolve with autoSelected option if it's valid", async () => {
    const [selected, option] = await promptToSelect(["foo", "bar"], "", 1);
    expect(selected).toEqual(0);
    expect(option).toEqual("foo");
  });
});

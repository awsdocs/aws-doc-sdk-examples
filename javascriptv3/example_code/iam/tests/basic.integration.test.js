import { describe, it } from "vitest";
import { main } from "../scenarios/basic.js";

describe("basic scenario", () => {
  it("should run without error", async () => {
    await main();
  });
});

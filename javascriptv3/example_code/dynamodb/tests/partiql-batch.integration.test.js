import { describe, it } from "vitest";

import { main } from "../scenarios/partiql-batch.js";

describe("partiql batch", () => {
  it("should run without error", async () => {
    await main();
  });
});

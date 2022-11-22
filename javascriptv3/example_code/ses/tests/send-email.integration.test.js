import { describe, it, expect } from "vitest";

import { run } from "../src/ses_sendemail";

describe("ses_sendemail", () => {
  it("should return an error when using an unverified email", async () => {
    const result = await run();
    expect(result.Error.Message).toContain("Email address is not verified.");
  });
});

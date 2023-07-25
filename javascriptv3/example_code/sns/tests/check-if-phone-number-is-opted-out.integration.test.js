import { describe, it, expect } from "vitest";

import { main as checkIfPhoneNumberIsOptedOut } from "../actions/check-if-phone-number-is-opted-out.js";

describe("check-if-phone-number-is-opted-out", () => {
  it("should return false when the phone number doesn't exist", async () => {
    const result = await checkIfPhoneNumberIsOptedOut();
    expect(result.isOptedOut).toBe(false);
  });
});

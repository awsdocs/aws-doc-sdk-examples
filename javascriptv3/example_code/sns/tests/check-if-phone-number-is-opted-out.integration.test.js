// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect } from "vitest";

import { checkIfPhoneNumberIsOptedOut } from "../actions/check-if-phone-number-is-opted-out.js";

describe("checkIfPhoneNumberIsOptedOut", () => {
  it("should return false when the phone number doesn't exist", async () => {
    const response = await checkIfPhoneNumberIsOptedOut();
    expect(response.isOptedOut).toBe(false);
  });
});

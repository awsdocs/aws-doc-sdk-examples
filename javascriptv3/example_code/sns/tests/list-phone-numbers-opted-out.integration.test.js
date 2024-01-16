// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect } from "vitest";
import { listPhoneNumbersOptedOut } from "../actions/list-phone-numbers-opted-out.js";

describe("listPhoneNumbersOptedOut", () => {
  it("should return a phoneNumbers property with an array value", async () => {
    const response = await listPhoneNumbersOptedOut();
    expect(response.phoneNumbers).toBeInstanceOf(Array);
  });
});

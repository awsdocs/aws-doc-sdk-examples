// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect } from "vitest";

import { run } from "../src/ses_sendemail";
import { MessageRejected } from "@aws-sdk/client-ses";

describe("ses_sendemail", () => {
  it("should return an error when using an unverified email", async () => {
    const result = await run();
    expect(result instanceof MessageRejected).toBe(true);
    if (result instanceof MessageRejected) {
      expect(result.Error.Message).toContain("Email address is not verified.");
    }
  });
});

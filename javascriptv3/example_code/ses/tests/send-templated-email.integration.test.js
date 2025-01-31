// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, beforeAll, afterAll, it, expect } from "vitest";

import { createTemplate, deleteTemplate } from "../src/libs/sesUtils";
import { run, TEMPLATE_NAME } from "../src/ses_sendtemplatedemail";
import { MessageRejected } from "@aws-sdk/client-ses";

describe("ses_sendbulktemplatedemail", () => {
  beforeAll(async () => {
    try {
      await createTemplate(TEMPLATE_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  afterAll(async () => {
    try {
      await deleteTemplate(TEMPLATE_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  it("should fail when the email addresses are not verified", async () => {
    const result = await run();
    expect(result instanceof MessageRejected).toBe(true);
    if (result instanceof MessageRejected) {
      expect(result.Error.Message).toContain("Email address is not verified.");
    }
  });
});

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, beforeAll, afterAll, it, expect } from "vitest";

import {
  createIdentity,
  createTemplate,
  deleteIdentity,
  deleteTemplate,
} from "../src/libs/sesUtils";
import { run, TEMPLATE_NAME, USERS } from "../src/ses_sendbulktemplatedemail";
import { MessageRejected } from "@aws-sdk/client-ses";

describe("ses_sendbulktemplatedemail", () => {
  beforeAll(async () => {
    try {
      await Promise.all(USERS.map((user) => createIdentity(user.emailAddress)));
      await createTemplate(TEMPLATE_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  afterAll(async () => {
    try {
      await Promise.all(USERS.map((user) => deleteIdentity(user.emailAddress)));
      await deleteTemplate(TEMPLATE_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  it("should fail when the email addresses are not verified", async () => {
    const result = await run();
    expect(result instanceof MessageRejected).toBe(true);
    if (result instanceof MessageRejected) {
      // TODO: MessageRejected isn't typed properly. Remove eslint-disable if type is fixed.
      // eslint-disable-next-line
      expect(result.Error.Message).toContain("Email address is not verified.");
    }
  });
});

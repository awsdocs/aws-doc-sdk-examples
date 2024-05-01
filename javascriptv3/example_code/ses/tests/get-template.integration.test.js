// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, beforeAll, afterAll, it, expect } from "vitest";

import { run, TEMPLATE_NAME } from "../src/ses_gettemplate";
import { createTemplate, deleteTemplate } from "../src/libs/sesUtils";

describe("ses_gettemplate", () => {
  beforeAll(async () => {
    await createTemplate(TEMPLATE_NAME);
  });

  afterAll(async () => {
    await deleteTemplate(TEMPLATE_NAME);
  });

  /**
   * @typedef {import('@aws-sdk/client-ses').GetTemplateCommandOutput} GetTemplateCommandOutput
   */

  it("should successfully get an existing email template", async () => {
    /** @type { GetTemplateCommandOutput } */
    const result = await run();
    expect(result.Template.TemplateName).toBe(TEMPLATE_NAME);
  });
});

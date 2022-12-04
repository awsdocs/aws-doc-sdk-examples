import { describe, afterAll, it, expect } from "vitest";

import { run, TEMPLATE_NAME } from "../src/ses_createtemplate";
import { deleteTemplate, getTemplate } from "../src/libs/sesUtils.js";

describe("ses_createtemplate", () => {
  afterAll(async () => {
    try {
      await deleteTemplate(TEMPLATE_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  it("should successfully create an email template", async () => {
    await run();
    const result = await getTemplate(TEMPLATE_NAME);
    expect(result.Template.TemplateName).toBe(TEMPLATE_NAME);
  });
});

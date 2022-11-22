import { describe, beforeAll, it, expect } from "vitest";

import { run, TEMPLATE_NAME } from "../src/ses_deletetemplate.js";
import { createTemplate, getTemplate } from "../src/libs/sesUtils";

describe("ses_deletetemplate", () => {
  beforeAll(async () => {
    try {
      await createTemplate(TEMPLATE_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  it("should successfully delete an email template", async () => {
    let template = await getTemplate(TEMPLATE_NAME);
    expect(template).toBeTruthy();
    await run();
    template = await getTemplate(TEMPLATE_NAME);
    expect(template).toBeFalsy();
  });
});

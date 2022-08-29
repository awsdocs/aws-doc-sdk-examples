import { run, TEMPLATE_NAME } from "../../ses/src/ses_gettemplate";
import { createTemplate, deleteTemplate } from "../../ses/src/libs/sesUtils";

describe("ses_gettemplate", () => {
  beforeAll(async () => {
    await createTemplate(TEMPLATE_NAME);
  });

  afterAll(async () => {
    await deleteTemplate(TEMPLATE_NAME);
  });

  it("should successfully get an existing email template", async () => {
    const result = await run();
    expect(result.Template.TemplateName).toBe(TEMPLATE_NAME);
  });
});

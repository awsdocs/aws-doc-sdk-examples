import { getUniqueName } from "../../libs/utils/util-string.js";
import { createTemplate, deleteTemplate } from "../../ses/src/libs/sesUtils";
import { run } from "../../ses/src/ses_listtemplates";

describe("ses_listemplates", () => {
  const TEMPLATE_NAME = getUniqueName("TemplateName");

  beforeAll(async () => {
    await createTemplate(TEMPLATE_NAME);
  });

  afterAll(async () => {
    await deleteTemplate(TEMPLATE_NAME);
  });

  it("should successfully list templates", async () => {
    const result = await run();
    expect(result.TemplatesMetadata).toContainEqual(
      expect.objectContaining({ Name: TEMPLATE_NAME })
    );
  });
});

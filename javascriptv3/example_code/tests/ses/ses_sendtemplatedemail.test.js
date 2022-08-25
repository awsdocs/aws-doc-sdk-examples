import {
  createIdentity,
  createTemplate,
  deleteIdentity,
  deleteTemplate,
} from "../../ses/src/libs/sesUtils";
import {
  run,
  TEMPLATE_NAME,
  USER,
} from "../../ses/src/ses_sendtemplatedemail";

describe("ses_sendbulktemplatedemail", () => {
  beforeAll(async () => {
    try {
      await createIdentity(USER.emailAddress);
      await createTemplate(TEMPLATE_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  afterAll(async () => {
    try {
      await deleteIdentity(USER.emailAddress);
      await deleteTemplate(TEMPLATE_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  it("should fail when the email addresses are not verified", async () => {
    const result = await run();
    expect(result.Error.Message).toContain("Email address is not verified.");
  });
});

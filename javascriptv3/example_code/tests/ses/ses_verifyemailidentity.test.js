import { run, EMAIL_ADDRESS } from "../../ses/src/ses_verifyemailidentity";
import { deleteIdentity, findIdentity } from "../../ses/src/libs/sesUtils";

describe("ses_verifyemailidentity", () => {
  afterAll(async () => {
    try {
      await deleteIdentity(EMAIL_ADDRESS);
    } catch (e) {
      console.error(e);
    }
  });

  it("should successfully create an email identity", async () => {
    await run();
    const result = await findIdentity(EMAIL_ADDRESS);
    expect(result).toBeTruthy();
  });
});

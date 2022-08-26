import { findIdentity, createIdentity } from "../../ses/src/libs/sesUtils";
import { run, IDENTITY_EMAIL } from "../../ses/src/ses_deleteidentity";

describe("ses_deleteidentity", () => {
  beforeAll(async () => {
    await createIdentity(IDENTITY_EMAIL);
  });

  it("should successfully delete an email identity", async () => {
    let identity = await findIdentity(IDENTITY_EMAIL);
    expect(identity).toBeTruthy();
    await run();
    identity = await findIdentity(IDENTITY_EMAIL);
    expect(identity).toBeFalsy();
  });
});

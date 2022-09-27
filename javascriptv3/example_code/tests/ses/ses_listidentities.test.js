import { getUniqueName, postfix } from "../../libs/utils/util-string.js";
import { createIdentity, deleteIdentity } from "../../ses/src/libs/sesUtils.js";
import { run } from "../../ses/src/ses_listidentities";

describe("ses_listidentities", () => {
  const IDENTITY_NAME = postfix(getUniqueName("IdentityName"), "@example.com");

  beforeAll(async () => {
    await createIdentity(IDENTITY_NAME);
  });

  afterAll(async () => {
    await deleteIdentity(IDENTITY_NAME);
  });

  it("should successfully list identities", async () => {
    const result = await run();
    expect(result.Identities).toContain(IDENTITY_NAME);
  });
});

import { describe, beforeAll, afterAll, it, expect } from "vitest";

import { getUniqueName, postfix } from "../../libs/utils/util-string.js";
import { createIdentity, deleteIdentity } from "../src/libs/sesUtils.js";
import { run } from "../src/ses_listidentities";

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

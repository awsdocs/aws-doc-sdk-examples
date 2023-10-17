import { describe, it, expect } from "vitest";

import { createAccountAlias } from "../actions/create-account-alias.js";
import { deleteAccountAlias } from "../actions/delete-account-alias.js";
import { listAccountAliases } from "../actions/list-account-aliases.js";
import { getUniqueName } from "@aws-sdk-examples/libs/utils/util-string.js";

describe("Account alias test", () => {
  it("should create, read, and delete an account alias", async () => {
    // Create an account alias.
    const accountAlias = getUniqueName("test-account-alias");
    await createAccountAlias(accountAlias);

    // List account aliases.
    let foundAccountAlias = await findAccountAlias(accountAlias);
    expect(foundAccountAlias).toEqual(accountAlias);

    // Delete the account alias.
    await deleteAccountAlias(accountAlias);

    foundAccountAlias = await findAccountAlias(accountAlias);
    expect(foundAccountAlias).toBeUndefined();
  });
});

/**
 *
 * @param {string} accountAlias
 */
const findAccountAlias = async (accountAlias) => {
  for await (const alias of listAccountAliases()) {
    if (alias === accountAlias) {
      return alias;
    }
  }
};

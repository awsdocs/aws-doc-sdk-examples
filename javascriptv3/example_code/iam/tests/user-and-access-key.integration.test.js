import { describe, it, expect } from "vitest";
import { StatusType } from "@aws-sdk/client-iam";

import { getUniqueName } from "@aws-sdk-examples/libs/utils/util-string.js";

import { createUser } from "../actions/create-user.js";
import { listUsers } from "../actions/list-users.js";
import { deleteUser } from "../actions/delete-user.js";
import { updateUser } from "../actions/update-user.js";
import { createAccessKey } from "../actions/create-access-key.js";
import { listAccessKeys } from "../actions/list-access-keys.js";
import { getAccessKeyLastUsed } from "../actions/get-access-key-last-used.js";
import { deleteAccessKey } from "../actions/delete-access-key.js";
import { updateAccessKey } from "../actions/update-access-key.js";

describe("User and access key test", () => {
  it("should create, read, update, and delete a user and access key", async () => {
    const userName = getUniqueName("test-user");
    const newUserName = getUniqueName("new-test-user");
    // Create the user.
    await createUser(userName);

    // List the users.
    let listUsersResponse = await listUsers();
    let user = userInListUsersResponse(userName, listUsersResponse);
    expect(user?.UserName).toEqual(userName);

    // Update the user.
    await updateUser(userName, newUserName);
    listUsersResponse = await listUsers();
    user = userInListUsersResponse(newUserName, listUsersResponse);
    expect(user?.UserName).toEqual(newUserName);

    // Create an access key.
    const createAccessKeyResponse = await createAccessKey(newUserName);
    if (!createAccessKeyResponse.AccessKey?.AccessKeyId) {
      throw new Error("Missing AccessKey");
    }

    // List the access keys.
    let accessKeyMetadata = await findAccessKeyMetadata(
      newUserName,
      createAccessKeyResponse.AccessKey.AccessKeyId,
    );
    expect(accessKeyMetadata).toBeDefined();

    // Get the data on when the access key was last used.
    const getAccessKeyLastUsedResponse = await getAccessKeyLastUsed(
      createAccessKeyResponse.AccessKey.AccessKeyId,
    );
    expect(getAccessKeyLastUsedResponse.AccessKeyLastUsed).toBeDefined();

    // Update the access key.
    await updateAccessKey(
      newUserName,
      createAccessKeyResponse.AccessKey.AccessKeyId,
    );
    accessKeyMetadata = await findAccessKeyMetadata(
      newUserName,
      createAccessKeyResponse.AccessKey.AccessKeyId,
    );
    expect(accessKeyMetadata?.Status).toEqual(StatusType.Inactive);

    // Delete the access key.
    await deleteAccessKey(
      newUserName,
      createAccessKeyResponse.AccessKey.AccessKeyId,
    );

    // List the access keys.
    accessKeyMetadata = await findAccessKeyMetadata(
      newUserName,
      createAccessKeyResponse.AccessKey.AccessKeyId,
    );
    expect(accessKeyMetadata).toBeUndefined();

    // Delete the user.
    await deleteUser(newUserName);
    listUsersResponse = await listUsers();
    user = userInListUsersResponse(newUserName, listUsersResponse);
    expect(user).toBeUndefined();
  });
});

/**
 *
 * @param {string} userName
 * @param {import("@aws-sdk/client-iam").ListUsersResponse} listUsersResponse
 * @returns {import("@aws-sdk/client-iam").User | undefined}
 */
const userInListUsersResponse = (userName, listUsersResponse) =>
  listUsersResponse.Users?.find((u) => u.UserName === userName);

/**
 *
 * @param {string} userName
 * @param {string} accessKeyId
 */
const findAccessKeyMetadata = async (userName, accessKeyId) => {
  // List the access keys.
  for await (const accessKey of listAccessKeys(userName)) {
    if (accessKey.AccessKeyId === accessKeyId) {
      return accessKey;
    }
  }
};

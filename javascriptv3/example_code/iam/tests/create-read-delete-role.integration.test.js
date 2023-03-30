import { describe, it, expect } from "vitest";

import { getUniqueName } from "libs/utils/util-string.js";

import { createRole } from "../actions/create-role.js";
import { getRole } from "../actions/get-role.js";
import { deleteRole } from "../actions/delete-role.js";
import { createUser } from "../actions/create-user.js";
import { listUsers } from "../actions/list-users.js";
import { deleteUser } from "../actions/delete-user.js";

describe("Role test", () => {
  it("should create a role, get a role, and delete the role", async () => {
    const roleName = getUniqueName("create-role-test");
    await createRole(roleName);

    const response = await getRole(roleName);
    expect(response.Role?.RoleName).toEqual(roleName);

    await deleteRole(roleName);

    await expect(() => getRole(roleName)).rejects.toThrow(
      `The role with name ${roleName} cannot be found.`
    );
  });
});

describe("User and access key test", () => {
  it("should create, read, update, and delete a user and access key", async () => {
    const userName = getUniqueName("test-user");
    const newUserName = getUniqueName("new-test-user");
    // Create the user.
    await createUser(userName);

    // List the users.
    let listUsersResponse = await listUsers();
    let user = userInListUsersResponse(userName, listUsersResponse);
    expect(user).toBeDefined();
    // Update the user.
    // Create an access key.
    // List the access keys.
    // List when the access key was last used.
    // Update the access key.
    // Delete the access key.

    // Delete the user.
    await deleteUser(userName);
    listUsersResponse = await listUsers();
    user = userInListUsersResponse(userName, listUsersResponse);
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

import { describe, it, expect } from "vitest";

import { getUniqueName } from "libs/utils/util-string.js";

import { createRole } from "../actions/create-role.js";
import { getRole } from "../actions/get-role.js";
import { deleteRole } from "../actions/delete-role.js";
import { listRoles } from "../actions/list-roles.js";
import { retry } from "libs/utils/util-timers.js";

describe("Role test", () => {
  it("should create a role, list roles, get a role, and delete the role", async () => {
    const roleName = getUniqueName("create-role-test");
    await createRole(roleName);

    const role = retry({ intervalInMs: 500, maxRetries: 10 }, async () => {
      const retryListRolesResponse = await listRoles();
      return retryListRolesResponse.Roles?.find((r) => r.RoleName === roleName);
    });
    expect(role).toBeDefined();

    const response = await getRole(roleName);
    expect(response.Role?.RoleName).toEqual(roleName);

    await deleteRole(roleName);

    await expect(() => getRole(roleName)).rejects.toThrow(
      `The role with name ${roleName} cannot be found.`
    );
  });
});

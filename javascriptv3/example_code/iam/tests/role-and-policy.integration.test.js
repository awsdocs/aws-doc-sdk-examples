import { describe, it, expect } from "vitest";

import { getUniqueName } from "libs/utils/util-string.js";

import { createRole } from "../actions/create-role.js";
import { getRole } from "../actions/get-role.js";
import { deleteRole } from "../actions/delete-role.js";
import { listRoles } from "../actions/list-roles.js";
import { createPolicy } from "../actions/create-policy.js";
import { listPolicies } from "../actions/list-policies.js";
import { getPolicy } from "../actions/get-policy.js";
import { deletePolicy } from "../actions/delete-policy.js";
import { attachRolePolicy } from "../actions/attach-role-policy.js";
import { listAttachedRolePolicies } from "../actions/list-attached-role-policies.js";
import { detachRolePolicy } from "../actions/detach-role-policy.js";

describe("Role and policy test", () => {
  it("should create a role and a policy, list them, get role, attach them, and delete them", async () => {
    // Create role.
    const roleName = getUniqueName("create-role-test");
    await createRole(roleName);

    // List roles.
    const role = await findRole(roleName);
    expect(role).toBeDefined();

    // Get role.
    const getRoleResponse = await getRole(roleName);
    expect(getRoleResponse.Role?.RoleName).toEqual(roleName);

    // Create policy.
    const policyName = getUniqueName("create-policy-test");
    await createPolicy(policyName);

    let policy = await findPolicy(policyName);

    if (!policy?.Arn) {
      throw new Error("Policy not found");
    }

    const policyArn = policy?.Arn;

    // Attach policy to role.
    await attachRolePolicy(policyArn, roleName);
    let attachedPolicy = await findAttachedPolicy(roleName, policyArn);
    expect(attachedPolicy).toBeDefined();

    // Detach policy from role.
    await detachRolePolicy(policyArn, roleName);
    attachedPolicy = await findAttachedPolicy(roleName, policyArn);
    expect(attachedPolicy).toBeUndefined();

    // Get policy
    const getPolicyResponse = await getPolicy(policy.Arn);
    expect(getPolicyResponse.Policy?.Arn).toEqual(policy?.Arn);

    // Delete policy.
    await deletePolicy(policy.Arn);
    await expect(() => getPolicy(policyName)).rejects.toThrow("");

    // Delete role.
    await deleteRole(roleName);
    await expect(() => getRole(roleName)).rejects.toThrow(
      `The role with name ${roleName} cannot be found.`
    );
  });
});

/**
 *
 * @param {string} policyName
 */
const findPolicy = async (policyName) => {
  const listPoliciesResponse = await listPolicies();
  return listPoliciesResponse.Policies?.find(
    (p) => p.PolicyName === policyName
  );
};

/**
 *
 * @param {string} roleName
 * @returns
 */
const findRole = async (roleName) => {
  const listRolesResponse = await listRoles();
  return listRolesResponse.Roles?.find((r) => r.RoleName === roleName);
};

/**
 *
 * @param {string} roleName
 * @param {string} policyArn
 */
const findAttachedPolicy = async (roleName, policyArn) => {
  const listAttachedPoliciesResponse = await listAttachedRolePolicies(roleName);
  return listAttachedPoliciesResponse.AttachedPolicies?.find(
    (p) => p.PolicyArn === policyArn
  );
};

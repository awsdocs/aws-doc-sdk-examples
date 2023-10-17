import { describe, it, expect } from "vitest";

import { getUniqueName } from "@aws-sdk-examples/libs/utils/util-string.js";

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
import { IAMClient, waitUntilRoleExists } from "@aws-sdk/client-iam";
import { putRolePolicy } from "../actions/put-role-policy.js";
import { listRolePolicies } from "../actions/list-role-policies.js";
import { deleteRolePolicy } from "../actions/delete-role-policy.js";

const examplePolicy = JSON.stringify({
  Version: "2012-10-17",
  Statement: [
    {
      Sid: "VisualEditor0",
      Effect: "Allow",
      Action: [
        "s3:ListBucketMultipartUploads",
        "s3:ListBucketVersions",
        "s3:ListBucket",
        "s3:ListMultipartUploadParts",
      ],
      Resource: "arn:aws:s3:::test-bucket",
    },
    {
      Sid: "VisualEditor1",
      Effect: "Allow",
      Action: [
        "s3:ListStorageLensConfigurations",
        "s3:ListAccessPointsForObjectLambda",
        "s3:ListAllMyBuckets",
        "s3:ListAccessPoints",
        "s3:ListJobs",
        "s3:ListMultiRegionAccessPoints",
      ],
      Resource: "*",
    },
  ],
});

describe("Role and policy test", () => {
  it("should create a role and a policy, list them, get role, attach them, and delete them", async () => {
    // Create role.
    const roleName = getUniqueName("create-role-test");
    await createRole(roleName);

    await waitUntilRoleExists(
      { client: new IAMClient({}), maxWaitTime: 300 },
      { RoleName: roleName },
    );

    // List roles.
    const foundRole = await findRole(roleName);
    expect(foundRole).toBeDefined();
    expect(foundRole?.RoleName).toEqual(roleName);

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

    // Attach inline policy to role.
    const inlinePolicyName = getUniqueName("inline-policy-test");
    await putRolePolicy(roleName, inlinePolicyName, examplePolicy);
    let foundInlinePolicyName = await findInlineRolePolicy(
      roleName,
      inlinePolicyName,
    );
    expect(foundInlinePolicyName).toEqual(inlinePolicyName);

    // Delete inline policy.
    await deleteRolePolicy(roleName, inlinePolicyName);
    foundInlinePolicyName = await findInlineRolePolicy(
      roleName,
      inlinePolicyName,
    );
    expect(foundInlinePolicyName).toBeUndefined();

    // Attach policy to role.
    await attachRolePolicy(policyArn, roleName);
    let attachedPolicy = await findAttachedRolePolicy(roleName, policyArn);
    expect(attachedPolicy).toBeDefined();

    // Detach policy from role.
    await detachRolePolicy(policyArn, roleName);
    attachedPolicy = await findAttachedRolePolicy(roleName, policyArn);
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
      `The role with name ${roleName} cannot be found.`,
    );
  });
});

/**
 *
 * @param {string} policyName
 */
const findPolicy = async (policyName) => {
  for await (const policy of listPolicies()) {
    if (policy.PolicyName === policyName) {
      return policy;
    }
  }
};

/**
 *
 * @param {string} roleName
 * @returns
 */
const findRole = async (roleName) => {
  for await (const role of listRoles()) {
    if (role.RoleName === roleName) {
      return role;
    }
  }
};

/**
 *
 * @param {string} roleName
 * @param {string} policyArn
 */
const findAttachedRolePolicy = async (roleName, policyArn) => {
  for await (const policy of listAttachedRolePolicies(roleName)) {
    if (policy.PolicyArn === policyArn) {
      return policy;
    }
  }
};

/**
 *
 * @param {string} roleName
 * @param {string} policyName
 */
const findInlineRolePolicy = async (roleName, policyName) => {
  for await (const policy of listRolePolicies(roleName)) {
    if (policy === policyName) {
      return policy;
    }
  }
};

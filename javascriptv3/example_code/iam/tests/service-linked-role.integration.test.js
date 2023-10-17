import { describe, it, expect } from "vitest";

import { createServiceLinkedRole } from "../actions/create-service-linked-role.js";
import { getRole } from "../actions/get-role.js";
import { deleteServiceLinkedRole } from "../actions/delete-service-linked-role.js";
import { retry } from "@aws-sdk-examples/libs/utils/util-timers.js";
import { getServiceLinkedRoleDeletionStatus } from "../actions/get-service-linked-role-deletion-status.js";
import { DeletionTaskStatusType } from "@aws-sdk/client-iam";

describe("Service linked role", () => {
  it("create, read, delete role", async () => {
    // Create the role.
    const { Role } = await createServiceLinkedRole(
      "elasticbeanstalk.amazonaws.com",
    );
    if (!Role?.RoleName) {
      throw new Error("Role name not found.");
    }
    const roleName = Role.RoleName;

    // Get the role.
    let getRoleResponse = await getRole(roleName);
    expect(getRoleResponse.Role?.RoleName).toEqual(roleName);

    // Delete the role.
    const { DeletionTaskId } = await deleteServiceLinkedRole(roleName);
    if (!DeletionTaskId) {
      throw new Error("DeletionTaskId not found.");
    }

    // Wait for deletion task to complete.
    retry({ intervalInMs: 1000, maxRetries: 60 }, async () => {
      const { Status } =
        await getServiceLinkedRoleDeletionStatus(DeletionTaskId);
      if (Status !== DeletionTaskStatusType.SUCCEEDED) {
        throw new Error("Deletion task not completed.");
      }
    });
  });
});

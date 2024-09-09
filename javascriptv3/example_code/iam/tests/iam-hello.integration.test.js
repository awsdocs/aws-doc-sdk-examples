// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect, vitest } from "vitest";
import { getUniqueName } from "@aws-doc-sdk-examples/lib/utils/util-string.js";
import { createPolicy } from "../actions/create-policy.js";
import { listLocalPolicies } from "../hello.js";
import { deletePolicy } from "../actions/delete-policy.js";

describe("hello", () => {
  it("should list my local policies", async () => {
    const policyName = getUniqueName("test-policy");
    const { Policy } = await createPolicy(policyName);

    if (!Policy?.Arn) {
      throw new Error("Policy not created.");
    }

    const logSpy = vitest.spyOn(console, "log");
    await listLocalPolicies();

    expect(logSpy).toHaveBeenCalledWith(policyName);

    await deletePolicy(Policy.Arn);
  });
});

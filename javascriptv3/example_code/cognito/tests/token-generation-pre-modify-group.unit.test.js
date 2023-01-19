/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { it, describe, expect } from "vitest";

import { handler } from "../scenarios/lambda-triggers/functions/token-generation-pre-modify-group.mjs";

describe("token-generation-pre-modify-group", () => {
  it("should override some claims", async () => {
    const result = await handler({
      request: {},
      response: {},
    });
    expect(result).toEqual({
      request: {},
      response: {
        claimsOverrideDetails: {
          groupOverrideDetails: {
            groupsToOverride: ["group-A", "group-B", "group-C"],
            iamRolesToOverride: [
              "arn:aws:iam::XXXXXXXXXXXX:role/sns_callerA",
              "arn:aws:iam::XXXXXXXXX:role/sns_callerB",
              "arn:aws:iam::XXXXXXXXXX:role/sns_callerC",
            ],
            preferredRole: "arn:aws:iam::XXXXXXXXXXX:role/sns_caller",
          },
        },
      },
    });
  });
});

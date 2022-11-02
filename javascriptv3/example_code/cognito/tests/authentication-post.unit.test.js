/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { testEqual } from "../../libs/utils/util-test.js";
import { handler } from "../scenarios/lambda-triggers/functions/authentication-post.mjs";

describe("authentication-post", () => {
  it(
    "should return the same event it was passed",
    testEqual(
      {
        triggerSource: "test",
        userPoolId: "testUserPoolId",
        callerContext: { clientId: "clientId" },
        userName: "Peccy",
      },
      handler({
        triggerSource: "test",
        userPoolId: "testUserPoolId",
        callerContext: { clientId: "clientId" },
        userName: "Peccy",
      })
    )
  );
});

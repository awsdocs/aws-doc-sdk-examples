/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { testEqual } from "../../libs/utils/util-test.js";
import { handler } from "../scenarios/lambda-triggers/functions/sign-up-pre-auto-confirm-domain.mjs";

describe("sign-up-pre-auto-confirm-domain", () => {
  it(
    "should auto confirm if the domain matches the target",
    testEqual(
      {
        request: { userAttributes: { email: "test@example.com" } },
        response: { autoConfirmUser: true },
      },
      handler({
        request: { userAttributes: { email: "test@example.com" } },
        response: {},
      })
    )
  );

  it(
    "should not auto confirm if the domain does not matches the target",
    testEqual(
      {
        request: { userAttributes: { email: "test@example.ca" } },
        response: { autoConfirmUser: false },
      },
      handler({
        request: { userAttributes: { email: "test@example.ca" } },
        response: {},
      })
    )
  );
});

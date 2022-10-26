/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { testEqual } from "../../libs/utils/util-test.js";
import { handler } from "../scenarios/lambda-triggers/functions/auth-challenge-create.mjs";

describe("auth-challenge-create function", () => {
  it(
    'should return the same request/response objects if the challenge name is not "CUSTOM_CHALLENGE"',
    testEqual(
      expect.objectContaining({ request: {}, response: {} }),
      handler({ request: {}, response: {} })
    )
  );

  it(
    "should return the same request/response objects if there are no sessions",
    testEqual(
      expect.objectContaining({
        request: { challengeName: "CUSTOM_CHALLENGE", session: [] },
        response: {},
      }),
      handler({
        request: { challengeName: "CUSTOM_CHALLENGE", session: [] },
        response: {},
      })
    )
  );

  it(
    "should return a captcha challenge if the session length is 2",
    testEqual(
      expect.objectContaining({
        response: {
          publicChallengeParameters: { captchaUrl: "url/123.jpg" },
          privateChallengeParameters: { answer: "5" },
        },
      }),
      handler({
        request: { challengeName: "CUSTOM_CHALLENGE", session: [{}, {}] },
        response: {},
      })
    )
  );

  it(
    "should return a mascot challenge if the session length is 3",
    testEqual(
      expect.objectContaining({
        response: {
          publicChallengeParameters: {
            securityQuestion: "Who is your favorite team mascot?",
          },
          privateChallengeParameters: { answer: "Peccy" },
        },
      }),
      handler({
        request: { challengeName: "CUSTOM_CHALLENGE", session: [{}, {}, {}] },
        response: {},
      })
    )
  );
});

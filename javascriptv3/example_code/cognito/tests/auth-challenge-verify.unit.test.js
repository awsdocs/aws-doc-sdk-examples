/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { testEqual } from "../../libs/utils/util-test.js";
import {
  handler,
} from "../scenarios/lambda-triggers/functions/auth-challenge-verify.mjs";

describe("auth-challenge-verify", () => {
  it(
    "should indicate a correct answer in the response if the answer is correct",
    testEqual(
      expect.objectContaining({ response: { answerCorrect: true } }),
      handler({
        request: {
          privateChallengeParameters: { answer: "5" },
          challengeAnswer: "5",
        },
        response: {},
      })
    )
  );

  it(
    "should indicate an incorrect answer in the response if the answer is incorrect",
    testEqual(
      expect.objectContaining({ response: { answerCorrect: false } }),
      handler({
        request: {
          privateChallengeParameters: { answer: "1" },
          challengeAnswer: "5",
        },
        response: {},
      })
    )
  );
});

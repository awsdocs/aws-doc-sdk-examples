const { testEqual } = require("../../libs/utils/util-test");
const {
  handler,
} = require("../scenarios/lambda-triggers/functions/auth-challenge-verify.js");

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

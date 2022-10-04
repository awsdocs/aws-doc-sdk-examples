const { testEqual } = require("../../libs/utils/util-test");
const {
  handler,
} = require("../scenarios/lambda-triggers/functions/authentication-post.js");

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

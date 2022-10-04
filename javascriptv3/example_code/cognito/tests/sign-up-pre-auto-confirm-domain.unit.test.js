const { testEqual } = require("../../libs/utils/util-test.js");
const {
  handler,
} = require("../scenarios/lambda-triggers/functions/sign-up-pre-auto-confirm-domain.js");

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

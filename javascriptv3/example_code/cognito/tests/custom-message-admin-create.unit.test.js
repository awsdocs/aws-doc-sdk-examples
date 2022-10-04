const { testEqual } = require("../../libs/utils/util-test.js");
const {
  handler,
} = require("../scenarios/lambda-triggers/functions/custom-message-admin-create.js");

describe("custom-message-admin-create", () => {
  it(
    "should return an unmodified event when the triggerSource is not AdminCreateUser",
    testEqual(
      { request: { triggerSource: "" }, response: {} },
      handler({ request: { triggerSource: "" }, response: {} })
    )
  );

  it(
    "should return a custom message when the triggerSource is AdminCreateUser",
    testEqual(
      expect.objectContaining({
        response: expect.objectContaining({
          emailMessage: `Welcome to the service. Your user name is Peccy. Your temporary password is 123`,
        }),
      }),
      handler({
        triggerSource: "CustomMessage_AdminCreateUser",
        request: { usernameParameter: "Peccy", codeParameter: "123" },
        response: {},
      })
    )
  );
});

const { testEqual } = require("../../libs/utils/util-test.js");
const {
  handler,
} = require("../scenarios/lambda-triggers/functions/sign-up-pre-auto-confirm-verify.js");

describe("sign-up-pre-auto-confirm-verify", () => {
  it(
    "should auto verify the user, their phone, and their email",
    testEqual(
      {
        request: {
          userAttributes: { email: "test@example.com", phone_number: "555-555-5555" },
        },
        response: {
          autoConfirmUser: true,
          autoVerifyEmail: true,
          autoVerifyPhone: true,
        },
      },
      handler({
        request: {
          userAttributes: { email: "test@example.com", phone_number: "555-555-5555" },
        },
        response: {},
      })
    )
  );

  it(
    "should not auto verify the phone and email if they are missing",
    testEqual(
      {
        request: {
          userAttributes: {},
        },
        response: {
          autoConfirmUser: true,
        },
      },
      handler({
        request: {
          userAttributes: {},
        },
        response: {},
      })
    )
  );
});

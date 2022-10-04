const { testEqual } = require("../../libs/utils/util-test.js");
const {
  handler,
} = require("../scenarios/lambda-triggers/functions/migrate-user.js");

describe("migrate-user", () => {
  it(
    "should return the unmodified event if the trigger source has no match",
    testEqual({ triggerSource: "Random" }, handler({ triggerSource: "Random" }))
  );

  it(
    "should migrate a user on authentication",
    testEqual(
      {
        userName: "belladonna",
        request: { password: "Test123" },
        triggerSource: "UserMigration_Authentication",
        response: {
          finalUserStatus: "CONFIRMED",
          messageAction: "SUPPRESS",
          userAttributes: { email_verified: "true", email: "bella@example.com" },
        },
      },
      handler({
        userName: "belladonna",
        request: { password: "Test123" },
        triggerSource: "UserMigration_Authentication",
        response: {},
      })
    )
  );

  it(
    "should migrate a user on forgot password",
    testEqual(
      {
        userName: "belladonna",
        triggerSource: "UserMigration_ForgotPassword",
        response: {
          messageAction: "SUPPRESS",
          userAttributes: { email_verified: "true", email: "bella@example.com" },
        },
      },
      handler({
        userName: "belladonna",
        triggerSource: "UserMigration_ForgotPassword",
        response: {},
      })
    )
  );
});

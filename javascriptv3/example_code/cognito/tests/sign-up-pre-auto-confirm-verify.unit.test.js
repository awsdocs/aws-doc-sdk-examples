import { testEqual } from "../../libs/utils/util-test.js";
import { handler } from "../scenarios/lambda-triggers/functions/sign-up-pre-auto-confirm-verify.mjs";

describe("sign-up-pre-auto-confirm-verify", () => {
  it(
    "should auto verify the user, their phone, and their email",
    testEqual(
      {
        request: {
          userAttributes: {
            email: "test@example.com",
            phone_number: "555-555-5555",
          },
        },
        response: {
          autoConfirmUser: true,
          autoVerifyEmail: true,
          autoVerifyPhone: true,
        },
      },
      handler({
        request: {
          userAttributes: {
            email: "test@example.com",
            phone_number: "555-555-5555",
          },
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

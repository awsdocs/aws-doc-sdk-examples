import { testEqual } from "../../libs/utils/util-test.js";
import { handler } from "../scenarios/lambda-triggers/functions/custom-message-sign-up.mjs";

describe("custom-message-sign-up", () => {
  it(
    "should return an unmodified event when the triggerSource is not CustomMessage_SignUp",
    testEqual(
      { request: { triggerSource: "" }, response: {} },
      handler({ request: { triggerSource: "" }, response: {} })
    )
  );

  it(
    "should return a custom message when the triggerSource is CustomMessage_SignUp",
    testEqual(
      expect.objectContaining({
        response: expect.objectContaining({
          emailMessage: `Thank you for signing up. Your confirmation code is 123.`,
        }),
      }),
      handler({
        triggerSource: "CustomMessage_SignUp",
        request: { usernameParameter: "Peccy", codeParameter: "123" },
        response: {},
      })
    )
  );
});

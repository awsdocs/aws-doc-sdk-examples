import { testEqual } from "../../libs/utils/util-test.js";
import { handler } from "../scenarios/lambda-triggers/functions/token-generation-pre-add-suppress-claim.mjs";

describe("token-generation-pre-add-suppress-claim", () => {
  it(
    "should override some claims",
    testEqual(
      {
        request: {},
        response: {
          claimsOverrideDetails: {
            claimsToAddOrOverride: {
              my_first_attribute: "first_value",
              my_second_attribute: "second_value",
            },
            claimsToSuppress: ["email"],
          },
        },
      },
      handler({
        request: {},
        response: {},
      })
    )
  );
});

const { testEqual } = require("../../libs/utils/util-test.js");
const {
  handler,
} = require("../scenarios/lambda-triggers/functions/token-generation-pre-add-suppress-claim.js");

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

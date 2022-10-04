import { testEqual } from "../../libs/utils/util-test.js";
import { handler } from "../scenarios/lambda-triggers/functions/token-generation-pre-modify-group.mjs";

describe("token-generation-pre-modify-group", () => {
  it(
    "should override some claims",
    testEqual(
      {
        request: {},
        response: {
          claimsOverrideDetails: {
            groupOverrideDetails: {
              groupsToOverride: ["group-A", "group-B", "group-C"],
              iamRolesToOverride: [
                "arn:aws:iam::XXXXXXXXXXXX:role/sns_callerA",
                "arn:aws:iam::XXXXXXXXX:role/sns_callerB",
                "arn:aws:iam::XXXXXXXXXX:role/sns_callerC",
              ],
              preferredRole: "arn:aws:iam::XXXXXXXXXXX:role/sns_caller",
            },
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

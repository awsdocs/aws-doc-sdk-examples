jest.mock("../src/libs/iamClient");
jest.mock("@aws-sdk/client-iam");

// Get service clients module and commands.
import 'regenerator-runtime/runtime'
import { run, params } from "../src/iam_listaccountaliases";
import { iamClient } from "../src/libs/iamClient";

describe("@aws-sdk/client-iam mock", () => {
  it("should successfully mock IAM client", async () => {
    iamClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

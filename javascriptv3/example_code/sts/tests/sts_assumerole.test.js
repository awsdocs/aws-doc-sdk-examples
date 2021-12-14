jest.mock("../src/libs/iamClient");
jest.mock("../src/libs/stsClient");
jest.mock("@aws-sdk/client-sts");
jest.mock("@aws-sdk/client-iam");

// Get service clients module and commands.
import 'regenerator-runtime/runtime'
import { run, params } from "../src/iam_updateuser";
import { iamClient } from "../src/libs/iamClient";
import { stsClient } from "../src/libs/stsClient";

describe("@aws-sdk/client-iam mock", () => {
  it("should successfully mock IAM client", async () => {
    iamClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

describe("@aws-sdk/client-sts mock", () => {
  it("should successfully mock STS client", async () => {
    stsClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

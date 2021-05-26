import { run, params } from "../../iam/src/sts_assumerole";
import { stsClient } from "../../iam/src/libs/stsClient.js";

jest.mock("../../iam/src/libs/stsClient.js");

describe("@aws-sdk/client-sts mock", () => {
  it("should successfully mock STS client", async () => {
    stsClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

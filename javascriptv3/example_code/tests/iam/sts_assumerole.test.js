const { run, params } = require("../../iam/src/sts_assumerole");
const { stsClient } = require("../../iam/src/libs/stsClient.js");

jest.mock("../../iam/src/libs/stsClient.js");

describe("@aws-sdk/client-sts mock", () => {
  it("should successfully mock STS client", async () => {
    stsClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

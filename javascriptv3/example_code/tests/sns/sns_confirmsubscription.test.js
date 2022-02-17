const { run, params } = require("../../sns/src/sns_confirmsubscription");
const { snsClient } = require("../../sns/src/libs/snsClient");

jest.mock("../../sns/src/libs/snsClient.js");

describe("@aws-sdk/client-emc mock", () => {
  it("should successfully mock SNS client", async () => {
    snsClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

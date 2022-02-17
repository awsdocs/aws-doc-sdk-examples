const { run, params } = require("../../sns/src/sns_checkphoneoptout");
const { snsClient } = require("../../sns/src/libs/snsClient.js");

jest.mock("../../sns/src/libs/snsClient.js");

describe("@aws-sdk/client-sns mock", () => {
    it("should successfully mock SNS client", async () => {
        snsClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

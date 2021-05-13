// Get service clients module and commands using CommonJS syntax.
const { run, params } = require("../../cloudwatch/src/cw_disablealarmactions");
const { cwClient } = require("../../cloudwatch/src/libs/cwClient");

jest.mock("../../cloudwatch/src/libs/cwClient.js");

describe("@aws-sdk/client-cloudwatch mock", () => {
    it("should successfully mock CloudWatch client", async () => {
        cwClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

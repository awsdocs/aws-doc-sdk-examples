// Get service clients module and commands using CommonJS syntax.
const { run, params } = require("../../cloudwatch/src/cwl_deletesubscriptionfilter");
const { cwlClient } = require("../../cloudwatch/src/libs/cwlClient");

jest.mock("../../cloudwatch/src/libs/cwlClient.js");

describe("@aws-sdk/client-cloudwatch mock", () => {
    it("should successfully mock CloudWatch Logs client", async () => {
        cwlClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

// Get service clients module and commands using CommonJS syntax.
const { run, params } = require("../../cloudwatch/src/cwe_puttargets");
const { cweClient } = require("../../cloudwatch/src/libs/cloudwatch-events-cloudWatchEventsClient");

jest.mock("../../cloudwatch/src/libs/cweClient.js");

describe("@aws-sdk/client-cloudwatch-events mock", () => {
    it("should successfully mock CloudWatch Events client", async () => {
        cweClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

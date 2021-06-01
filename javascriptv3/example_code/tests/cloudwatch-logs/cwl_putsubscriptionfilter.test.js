// Get service clients module and commands using CommonJS syntax.
const { run, params } = require("../../cloudwatch-logs/src/putSubscriptionFilter");
const { cwlClient } = require("../../cloudwatch-logs/src/libs/cloudwatchLogsClient");

jest.mock("../../cloudwatch-logs/src/libs/cloudwatchLogsClient.js");

describe("@aws-sdk/client-cloudwatch-logs mock", () => {
    it("should successfully mock CloudWatch Logs client", async () => {
        cwlClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

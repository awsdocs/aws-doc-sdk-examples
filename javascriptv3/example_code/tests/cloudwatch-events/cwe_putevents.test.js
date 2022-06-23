// Get service clients module and commands using CommonJS syntax.
const { run, params } = require("../../cloudwatch-events/src/putEvents");
const { cweClient } = require("../../cloudwatch-events/src/libs/cloudWatchEventsClient");

jest.mock("../../cloudwatch-events/src/libs/cloudWatchEventsClient.js");

describe("@aws-sdk/client-cloudwatch-events mock", () => {
    it("should successfully mock CloudWatch Events client", async () => {
        cweClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

// Get service clients module and commands using CommonJS syntax.
const { run, params } = require("../../cloudwatch/src/enableAlarmActions");
const { cwClient } = require("../../cloudwatch/src/libs/cloudWatchClient");


jest.mock("../../cloudwatch/src/libs/cloudWatchClient.js");

describe("@aws-sdk/client-cloudwatch mock", () => {
  it("should successfully mock CloudWatch client", async () => {
    cwClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

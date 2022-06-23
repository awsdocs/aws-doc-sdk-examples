jest.mock("../src/libs/cloudWatchLogsClient.js");
jest.mock("@aws-sdk/client-cloudwatch-logs");

// Get service clients module and commands.
import "regenerator-runtime/runtime";
import { run, params } from "../src/describeSubscriptionFilters.js";
import { cwlClient } from "../src/libs/cloudWatchLogsClient";

describe("@aws-sdk/client-cloudwatch-logs mock", () => {
  it("should successfully mock CloudWatch Logs client", async () => {
    cwlClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

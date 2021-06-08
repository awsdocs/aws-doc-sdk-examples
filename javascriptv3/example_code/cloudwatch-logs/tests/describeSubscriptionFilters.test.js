jest.mock("../src/libs/cloudWatchLogsClient.js");
jest.mock("@aws-sdk/client-cloudwatch-logs");

// Get service clients module and commands.
import "regenerator-runtime/runtime";
import { run, params } from "../src/describeSubscriptionFilters.js";
import { cwlClient } from "../src/libs/cloudWatchLogsClient";

describe("@aws-sdk/client-cloudwatch-logs mock", () => {
  it("should run async equal", async (done) => {
    expect(1).toBe(1);
    done();
  });

  it("should be equal", () => {
    expect(1).toBe(1);
  });

  it("should successfully mock CloudWatch Logs client", async () => {
    cwlClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});


jest.mock("../src/libs/cloudWatchClient.js");
jest.mock("@aws-sdk/client-cloudwatch");

// Get service clients module and commands.
import 'regenerator-runtime/runtime'
import { run, params } from "../src/listMetrics.js";
import { cwClient } from "../src/libs/cloudWatchClient";

describe("@aws-sdk/client-cloudwatch mock", () => {
  it("should successfully mock CloudWatch client", async () => {
    cwClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

// Get service clients module and commands using CommonJS syntax.
import { run, params } from "../../cloudwatch/src/cw_listmetrics";
import { cwClient } from "../../cloudwatch/src/libs/cwClient";

jest.mock("../../cloudwatch/src/libs/cwClient.js");

describe("@aws-sdk/client-cloudwatch mock", () => {
  it("should successfully mock CloudWatch client", async () => {
    cwClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

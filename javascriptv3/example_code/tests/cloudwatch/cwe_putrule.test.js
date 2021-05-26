// Get service clients module and commands using CommonJS syntax.
import { run, params } from "../../cloudwatch/src/cwe_putrule";
import { cweClient } from "../../cloudwatch/src/libs/cweClient";

jest.mock("../../cloudwatch/src/libs/cweClient.js");

describe("@aws-sdk/client-cloudwatch mock", () => {
  it("should successfully mock CloudWatch Events client", async () => {
    cweClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

import { run, params } from "../../sns/src/sns_deletetopic";
import { snsClient } from "../../sns/src/libs/snsClient.js";

jest.mock("../../sns/src/libs/snsClient.js");

describe("@aws-sdk/client-emc mock", () => {
  it("should successfully mock SNS client", async () => {
    snsClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

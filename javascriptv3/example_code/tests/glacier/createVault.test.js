import { run, params } from "../../glacier/src/createVault";
import { glacierClient } from "../../glacier/src/libs/glacierClient.js";

jest.mock("../../glacier/src/libs/glacierClient.js");

describe("@aws-sdk/client-glacier mock", () => {
  it("should successfully mock Glacier client", async () => {
    glacierClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

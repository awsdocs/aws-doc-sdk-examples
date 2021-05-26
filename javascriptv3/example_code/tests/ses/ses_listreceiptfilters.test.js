import { run } from "../../ses/src/ses_listreceiptfilters";
import { sesClient } from "../../ses/src/libs/sesClient.js";

jest.mock("../../ses/src/libs/sesClient.js");

describe("@aws-sdk/client-ses mock", () => {
  it("should successfully mock SES client", async () => {
    sesClient.send.mockResolvedValue({ isMock: true });
    const response = await run();
    expect(response.isMock).toEqual(true);
  });
});

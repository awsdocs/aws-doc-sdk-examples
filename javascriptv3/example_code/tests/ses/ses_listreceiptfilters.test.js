const { run } = require("../../ses/src/ses_listreceiptfilters");
const { sesClient } = require("../../ses/src/libs/sesClient.js");

jest.mock("../../ses/src/libs/sesClient.js");

describe("@aws-sdk/client-ses mock", () => {
  it("should successfully mock SES client", async () => {
    sesClient.send.mockResolvedValue({ isMock: true });
    const response = await run();
    expect(response.isMock).toEqual(true);
  });
});

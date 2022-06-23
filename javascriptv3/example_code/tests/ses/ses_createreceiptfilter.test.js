const { run, params } = require("../../ses/src/ses_createreceiptfilter");
const { sesClient } = require("../../ses/src/libs/sesClient");

jest.mock("../../ses/src/libs/sesClient.js");

describe("@aws-sdk/client-ses mock", () => {
  it("should successfully mock SES client", async () => {
    sesClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

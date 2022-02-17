const { run, params } = require("../../pinpoint/src/pinpoint_send_sms_message");
const { pinClient } = require("../../pinpoint/src/libs/pinClient.js");

jest.mock("../../pinpoint/src/libs/pinClient.js");

describe("@aws-sdk/client-pinpoint mock", () => {
  it("should successfully mock PinPoint client", async () => {
    pinClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

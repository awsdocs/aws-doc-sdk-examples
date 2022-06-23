const { run, params } = require("../../sqs/src/sqs_deadletterqueue");
const { sqsClient } = require("../../sqs/src/libs/sqsClient.js");

jest.mock("../../sqs/src/libs/sqsClient.js");

describe("@aws-sdk/client-ses mock", () => {
  it("should successfully mock SES client", async () => {
    sqsClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

import { run, params } from "../../sqs/src/sqs_longpolling_createqueue";
import { sqsClient } from "../../sqs/src/libs/sqsClient.js";

jest.mock("../../sqs/src/libs/sqsClient.js");

describe("@aws-sdk/client-ses mock", () => {
  it("should successfully mock SES client", async () => {
    sqsClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

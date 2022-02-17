const {
  run,
  params,
} = require("../../transcribe/src/transcribe_create_medical_job");
const {
  transcribeClient,
} = require("../../transcribe/src/libs/transcribeClient.js");

jest.mock("../../transcribe/src/libs/transcribeClient.js");

describe("@aws-sdk/client-transcribe mock", () => {
  it("should successfully mock Transcribe client", async () => {
    transcribeClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

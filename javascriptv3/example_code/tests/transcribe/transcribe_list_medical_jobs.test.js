import {
  run,
  params,
} from "../../transcribe/src/transcribe_list_medical_jobs";
import {
  transcribeClient,
} from "../../transcribe/src/libs/transcribeClient.js";

jest.mock("../../transcribe/src/libs/transcribeClient.js");

describe("@aws-sdk/client-transcribe mock", () => {
  it("should successfully mock Transcribe client", async () => {
    transcribeClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

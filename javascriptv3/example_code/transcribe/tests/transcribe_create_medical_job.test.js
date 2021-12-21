jest.mock("../src/libs/transcribeClient");
jest.mock("@aws-sdk/client-transcribe");

// Get service clients module and commands.
import 'regenerator-runtime/runtime'
import { run, params } from "../src/transcribe_create_medical_job";
import { transcribeClient } from "../src/libs/transcribeClient";

describe("@aws-sdk/client-transcribe mock", () => {
  it("should successfully mock Transcribe client", async () => {
    transcribeClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

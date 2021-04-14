const mockCreateTranscriptionJob = jest.fn();
jest.mock("@aws-sdk/client-transcribe/commands/StartTranscriptionJobCommand", () => ({
  Transcribe: function Transcribe() {
    this.StartTranscriptionJobCommand = mockCreateTranscriptionJob;
  },
}));
const { run } = require("../../transcribe/src/transcribe_create_job");

test("has to mock Transcribe#transcribe_create_job", async (done) => {
  await run();
  expect(mockCreateTranscriptionJob).toHaveBeenCalled;
  done();
});

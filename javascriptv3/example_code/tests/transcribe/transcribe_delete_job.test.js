const mockDeleteTranscriptionJob = jest.fn();
jest.mock("@aws-sdk/client-transcribe/commands/DeleteTranscriptionJobCommand", () => ({
  Transcribe: function Transcribe() {
    this.DeleteTranscriptionJobCommand = mockDeleteTranscriptionJob;
  },
}));
const { run } = require("../../transcribe/src/transcribe_delete_job");

test("has to mock Transcribe#transcribe_delete_job", async (done) => {
  await run();
  expect(mockDeleteTranscriptionJob).toHaveBeenCalled;
  done();
});

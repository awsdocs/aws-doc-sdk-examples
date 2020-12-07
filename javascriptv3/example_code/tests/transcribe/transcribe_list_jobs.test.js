const mockListTranscriptionJobs = jest.fn();
jest.mock("@aws-sdk/client-transcribe/commands/ListTranscriptionJobCommand", () => ({
  Transcribe: function Transcribe() {
    this.ListTranscriptionJobCommand = mockListTranscriptionJobs;
  },
}));
const { run } = require("../../transcribe/src/transcribe_list_jobs");

test("has to mock Transcribe#transcribe_list_jobs", async (done) => {
  await run();
  expect(mockListTranscriptionJobs).toHaveBeenCalled;
  done();
});

const mockListMedicalTranscriptionJobs = jest.fn();
jest.mock("@aws-sdk/client-transcribe/commands/ListMedicalTranscriptionJobCommand", () => ({
    Transcribe: function Transcribe() {
        this.ListMedicalTranscriptionJobCommand = mockListMedicalTranscriptionJobs;
    },
}));
const { run } = require("../../transcribe/src/transcribe_list_medical_jobs");

test("has to mock Transcribe#transcribe_list_medical_jobs", async (done) => {
    await run();
    expect(mockListMedicalTranscriptionJobs).toHaveBeenCalled;
    done();
});

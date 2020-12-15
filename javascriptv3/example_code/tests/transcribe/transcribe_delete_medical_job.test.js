const mockDeleteMedicalTranscriptionJob = jest.fn();
jest.mock("@aws-sdk/client-transcribe/commands/DeleteMedicalTranscriptionJobCommand", () => ({
    Transcribe: function Transcribe() {
        this.DeleteMedicalTranscriptionJobCommand = mockDeleteMedicalTranscriptionJob;
    },
}));
const { run } = require("../../transcribe/src/transcribe_delete_medical_job");

test("has to mock Transcribe#transcribe_delete_medical_job", async (done) => {
    await run();
    expect(mockDeleteMedicalTranscriptionJob).toHaveBeenCalled;
    done();
});

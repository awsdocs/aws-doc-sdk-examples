const mockCreateMedicalTranscriptionJob = jest.fn();
jest.mock("@aws-sdk/client-transcribe/commands/StartMedicalTranscriptionJobCommand", () => ({
    Transcribe: function Transcribe() {
        this.StartMedicalTranscriptionJobCommand = mockCreateMedicalTranscriptionJob;
    },
}));
const { run } = require("../../transcribe/src/transcribe_create_medical_job");

test("has to mock Transcribe#transcribe_create_medical_job", async (done) => {
    await run();
    expect(mockCreateMedicalTranscriptionJob).toHaveBeenCalled;
    done();
});

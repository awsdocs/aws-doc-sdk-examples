const mockSynthesizeToS3 = jest.fn();
jest.mock("@aws-sdk/client-polly/commands/StartSpeechSynthesisTaskCommand", () => ({
    Pinpoint: function Pinpoint() {
        this.StartSpeechSynthesisTaskCommand = mockSynthesizeToS3;
    },
}));
import { run } from "../../polly/general-examples/src/polly_synthesize_to_s3";

test("has to mock polly#mockSynthesizeToS3", async (done) => {
    await run();
    expect(mockSynthesizeToS3).toHaveBeenCalled;
    done();
});

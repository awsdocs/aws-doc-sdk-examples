const mockSendEmailCommand = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/SendEmailCommand", () => ({
    DynamoDB: function DynamoDB() {
        this.SendEmailCommand = mockSendEmailCommand;
    },
}));
import { run } from "../../../cross-services/lambda-step-functions/src/lambda3/sendemail";

test("has to mock db#sendemail", async (done) => {
    await run();
    expect(mockSendEmailCommand).toHaveBeenCalled;
    done();
});

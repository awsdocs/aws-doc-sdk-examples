const mockScanCommand = jest.fn();
jest.mock("@aws-sdk/client-dynamodb/commands/ScanCommand", () => ({
    DynamoDB: function DynamoDB() {
        this.ScanCommand = mockScanCommand;
    },
}));
const { run } = require("../../../cross-services/lambda-scheduled-events/src/mylamdbafunction.ts");

test("has to mock db#scan", async (done) => {
    await run();
    expect(mockScanCommand).toHaveBeenCalled;
    done();
});

const mockPublishCommand = jest.fn();
jest.mock("@aws-sdk/client-sns/commands/PublishCommand", () => ({
    SNS: function SNS() {
        this.PublishCommand = mockPublishCommand;
    },
}));
const { run } = require("../../../cross-services/lambda-scheduled-events/src/mylamdbafunction.ts");

test("has to mock sns#sendmessage", async (done) => {
    await run();
    expect(mockPublishCommand).toHaveBeenCalled;
    done();
});

const mockPutItemCommand = jest.fn();
jest.mock("@aws-sdk/client-dynamodb/commands/PutItemCommand", () => ({
    DynamoDB: function DynamoDB() {
        this.PutItemCommand = mockPutItemCommand;
    },
}));
const { run } = require("../../../cross-services/lambda-step-functions/src/lambda2/additem");

test("has to mock db#putitem", async (done) => {
    await run();
    expect(mockPutItemCommand).toHaveBeenCalled;
    done();
});

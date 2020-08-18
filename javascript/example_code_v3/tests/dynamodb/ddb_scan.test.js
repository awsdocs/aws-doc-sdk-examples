const mockScan = jest.fn();
jest.mock("@aws-sdk/client-dynamodb/commands/ScanCommand", () => ({
    DynamoDB: function DynamoDB() {
        this.ScanCommand = mockScan;
    },
}));
const {run} = require("../../dynamodb/ddb_scan");

//test function
test("has to mock db#Scan", async (done) => {
    await run();
    expect(mockScan).toHaveBeenCalled;
    done();
});

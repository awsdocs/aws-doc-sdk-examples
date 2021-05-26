const mockBatchWriterItemCommand = jest.fn();
jest.mock("@aws-sdk/client-dynamodb/commands/ScanCommand", () => ({
    DynamoDB: function DynamoDB() {
        this.mockBatchWriterItemCommand = mockBatchWriterItemCommand;
    },
}));
import { run } from "../../../cross-services/lambda-scheduled-events/src/helper-functions/populate-table";

test("has to mock db#batchwrite", async (done) => {
    await run();
    expect(mockBatchWriterItemCommand).toHaveBeenCalled;
    done();
});

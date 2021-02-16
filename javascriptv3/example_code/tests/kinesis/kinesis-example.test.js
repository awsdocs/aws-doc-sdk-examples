const mockPutRecordsCommand = jest.fn();
jest.mock("@aws-sdk/client-kinesis/commands/PutRecordsCommand", () => ({
    Kinesis: function Kinesis() {
        this.PutRecordsCommand = mockPutRecordsCommand;
    },
}));
const { run } = require("../../kinesis/src/kinesis-example.js");
test("has to mock kinesis#getputrecordcommand", async (done) => {
    await run();
    expect(mockPutRecordsCommand).toHaveBeenCalled;
    done();
});

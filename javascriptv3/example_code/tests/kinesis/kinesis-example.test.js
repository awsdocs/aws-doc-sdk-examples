var recordData = [{
    Data: JSON.stringify({
        blog: window.location.href,
        scrollTopPercentage: 10,
        scrollBottomPercentage: 10,
        time: new Date()
    }),
    PartitionKey: 'PARTITION_KEY' // Must be a string.
}];

const params = {
    Records: recordData,
    StreamName: 'STREAM_NAME'}


const { uploadData } = require("../../kinesis/src/kinesis-example");
const { kinesisClient } = require("../../kinesis/src/libs/kinesisClient.js");

jest.mock("../../kinesis/src/libs/kinesisClient.js");

describe("@aws-sdk/client-kinesis mock", () => {
    it("should successfully mock Kinesis client", async () => {
        kinesisClient.send.mockResolvedValue({ isMock: true });
        const response = await uploadData(params);
        expect(response.isMock).toEqual(true);
    });
});

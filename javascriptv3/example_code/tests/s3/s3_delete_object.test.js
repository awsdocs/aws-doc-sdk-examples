const mockDeleteObject = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
        this.DeleteObjectsCommand = mockDeleteObject;
    },
}));
const { bucketParams, run } = require("../../s3/s3_createBucket");

test("has to mock S3#deleteallobjects", async (done) => {
    await run();
    expect(mockDeleteObject).toHaveBeenCalled;
    done();
});

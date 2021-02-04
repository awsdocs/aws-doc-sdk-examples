const mockCreateAndUploadObject = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/PutObjectCommand", () => ({
    S3: function S3() {
        this.PutObjectCommand = mockCreateAndUploadObject;
    },
}));
const { bucketParams, run } = require("../../s3/src/s3_create_and_upload_objects");

test("has to mock S3#createanduploadObject", async (done) => {
    await run();
    expect(mockCreateAndUploadObject).toHaveBeenCalled;
    done();
});

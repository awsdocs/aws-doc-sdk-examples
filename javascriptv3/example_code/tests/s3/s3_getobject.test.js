const mockGetObject = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/GetObjectCommand", () => ({
    S3: function S3() {
        this.GetObjectCommand = mockGetObject;
    },
}));
const {  run } = require("../../s3/src/s3_getobject");

test("has to mock S3#getObjectfromBucket", async (done) => {
    await run();
    expect(mockGetObject).toHaveBeenCalled;
    done();
});

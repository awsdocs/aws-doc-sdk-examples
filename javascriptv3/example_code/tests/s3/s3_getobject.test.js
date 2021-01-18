const mockPutObject = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
        this.GetObjectCommand = mockPutObject;
    },
}));
const { uploadParams, file, path, run } = require("../../s3/s3_getobject");

test("has to mock S3#uploadtoBucket", async (done) => {
    await run();
    expect(mockPutObject).toHaveBeenCalled;
    done();
});

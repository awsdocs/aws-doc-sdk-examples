const mockPutObject = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/PutObjectCommand", () => ({
  S3: function S3() {
    this.PutObjectCommand = mockPutObject;
  },
}));
const { uploadParams, file, path, run } = require("../../s3/s3_upload");

test("has to mock S3#uploadtoBucket", async (done) => {
  await run();
  expect(mockPutObject).toHaveBeenCalled;
  done();
});

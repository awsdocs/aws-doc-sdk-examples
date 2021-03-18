const mockCreateMultipartUpload = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
        this.CreateMultipartUploadCommand = mockCreateMultipartUpload;
    },
}));
const { run } = require("../../s3/s3_multipartupload");

test("has to mock S3#CreateMultiPartUpload", async (done) => {
    await run();
    expect(mockCreateMultipartUpload).toHaveBeenCalled;
    done();
});

const mockUploadPartCommand = jest.fn();
jest.mock("@aws-sdk/client-s3/Commands/UploadPartCommand", () => ({
    S3: function S3() {
        this.UploadPartCommand = mockUploadPartCommand;
    },
}));
const { run } = require("../../s3/s3_multipartupload");

test("has to mock S3#mUploadPart", async (done) => {
    await run();
    expect(mockUploadPartCommand).toHaveBeenCalled;
    done();
});

const mockCompleteMultipartUploadCommand = jest.fn();
jest.mock("@aws-sdk/client-s3/Commands/CompleteMultipartUploadCommand", () => ({
    S3: function S3() {
        this.CompleteMultipartUploadCommand = mockCompleteMultipartUploadCommand;
    },
}));
const { run } = require("../../s3/s3_multipartupload");

test("has to mock S3#mCompleteUploadPart", async (done) => {
    await run();
    expect(mockCompleteMultipartUploadCommand).toHaveBeenCalled;
    done();
});

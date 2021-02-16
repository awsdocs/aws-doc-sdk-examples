const mockCreateBucket = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
        this.CreateBucketCommand = mockCreateBucket;
    },
}));
const { run } = require("../../s3/s3_get_presignedURL");

test("has to mock S3#createBucket", async (done) => {
    await run();
    expect(mockCreateBucket).toHaveBeenCalled();
    done();
});

const mockPutObject = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
        this.PutObjectCommand = mockPutObject;
    },
}));
const { run } = require("../../s3/s3_get_presignedURL");

test("has to mock S3#putobject", async (done) => {
    await run();
    expect(mockPutObject).toHaveBeenCalled();
    done();
});

const mockGetObject = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
        this.GetObjectCommand = mockGetObject;
    },
}));
const { run } = require("../../s3/s3_get_presignedURL");

test("has to mock S3#getobject", async (done) => {
    await run();
    expect(mockGetObject).toHaveBeenCalled();
    done();
});


const mockDeleteObject = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
        this.DeleteObjectCommand = mockDeleteObject;
    },
}));
const { run } = require("../../s3/s3_get_presignedURL");

test("has to mock S3#deleteobject", async (done) => {
    await run();
    expect(mockDeleteObject).toHaveBeenCalled();
    done();
});

const mockDeleteBucket = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
        this.DeleteObjectCommand = mockDeleteBucket;
    },
}));
const { run } = require("../../s3/s3_get_presignedURL");

test("has to mock S3#deletebucket", async (done) => {
    await run();
    expect(mockDeleteBucket).toHaveBeenCalled();
    done();
});

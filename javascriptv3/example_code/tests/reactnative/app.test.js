const mockCreateBucket = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
        this.createBucket = mockCreateBucket;
    },
}));
import { bucketParams, run } from "../../s3/s3_createBucket";

test("has to mock S3#createBucket", async (done) => {
    await run();
    expect(mockCreateBucket).toHaveBeenCalledWith(bucketParams);
    done();
});

const mockDeleteBucket = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
        this.deleteBucket = mockDeleteBucket;
    },
}));
import { bucketParams, run } from "../../s3/s3_deleteBucket";

test("has to mock S3#deleteBucket", async (done) => {
    await run();
    expect(mockDeleteBucket).toHaveBeenCalledWith(bucketParams);
    done();
});

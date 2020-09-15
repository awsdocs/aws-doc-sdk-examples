
describe("S3 Bucket interactions", () => {

  let mockS3Response = {
    data: "EXAMPLE_RESPONSE"
  };
  let mockCreateBucket = jest.fn(() => new Object({}));

  let mockDeleteBucket = jest.fn(() => new Object({}));
  let mockDeleteBucketWebsite = jest.fn(() => new Object({}));
  let mockDeleteBucketPolicy = jest.fn(() => new Object({}));

  let mockGetBucketAcl = jest.fn(() => new Object({}));
  let mockGetBucketPolicy = jest.fn(() => new Object({}));
  let mockGetBucketWebsite = jest.fn(() => new Object({}));

  let mockSend = jest.fn(() => mockS3Response);

  jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
      this.send = mockSend;
    },
    CreateBucketCommand: mockCreateBucket,

    DeleteBucketCommand: mockDeleteBucket,
    DeleteBucketWebsiteCommand: mockDeleteBucketWebsite,
    DeleteBucketPolicyCommand: mockDeleteBucketPolicy,

    GetBucketAclCommand: mockGetBucketAcl,
    GetBucketPolicyCommand: mockGetBucketPolicy,
    GetBucketWebsiteCommand: mockGetBucketWebsite

  }));
  
  describe("Default run invokes correctly", () => {
    // dont have to call run() as it's called on require by default.

    test("S3#createBucket", async (done) => { 
      const { bucketParams } = require("../src/s3_createBucket"); 
      expect(mockCreateBucket).toHaveBeenCalledWith(bucketParams);
      expect(mockCreateBucket).toBeCalledTimes(1);
      done();
    });

    test("S3#deleteBucket", async (done) => {  
      const { bucketParams } = require("../src/s3_deleteBucket"); 
      expect(mockDeleteBucket).toHaveBeenCalledWith(bucketParams);
      expect(mockDeleteBucket).toBeCalledTimes(1);
      done();
    });

    test("S3#deleteBucketWebsite", async (done) => {  
      const { bucketParams } = require("../src/s3_deleteBucketWebsite"); 
      expect(mockDeleteBucketWebsite).toHaveBeenCalledWith(bucketParams);
      expect(mockDeleteBucketWebsite).toBeCalledTimes(1);
      done();
    });

    test("S3#deleteBucketPolicy", async (done) => {  
      const { bucketParams } = require("../src/s3_deleteBucketPolicy"); 
      expect(mockDeleteBucketPolicy).toHaveBeenCalledWith(bucketParams);
      expect(mockDeleteBucketPolicy).toBeCalledTimes(1);
      done();
    });

    test("S3#getBucketAcl", async (done) => {  
      const { bucketParams } = require("../src/s3_getBucketAcl"); 
      expect(mockGetBucketAcl).toHaveBeenCalledWith(bucketParams);
      expect(mockGetBucketAcl).toBeCalledTimes(1);
      done();
    });

    test("S3#getBucketPolicy", async (done) => {  
      const { bucketParams } = require("../src/s3_getBucketPolicy"); 
      expect(mockGetBucketPolicy).toHaveBeenCalledWith(bucketParams);
      expect(mockGetBucketPolicy).toBeCalledTimes(1);
      done();
    });

    test("S3#getBucketWebsite", async (done) => {  
      const { bucketParams } = require("../src/s3_getBucketWebsite"); 
      expect(mockGetBucketWebsite).toHaveBeenCalledWith(bucketParams);
      expect(mockGetBucketWebsite).toBeCalledTimes(1);
      done();
    });
  });
});

describe("S3 Bucket interactions", () => {

  let mockS3Response = {
    data: "EXAMPLE_RESPONSE"
  };
  let mockCreateBucket = jest.fn(() => new Object({}));
  let mockDeleteBucket = jest.fn(() => new Object({}));
  let mockSend = jest.fn(() => mockS3Response);

  jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
      this.send = mockSend;
    },
    CreateBucketCommand: mockCreateBucket,
    DeleteBucketCommand: mockDeleteBucket
  }));
  
  describe("exploit default runs", () => {
    afterEach(() => {
        mockCreateBucket.mockReset();
        mockDeleteBucket.mockReset();
        mockSend.mockReset();
    });
      
    test("S3#createBucket", async (done) => { 
      const { bucketParams } = require("../src/s3_createBucket"); 
      // dont have to call run() as it's called on require by default.
      expect(mockCreateBucket).toHaveBeenCalledWith(bucketParams);
      expect(mockCreateBucket).toBeCalledTimes(1);
      done();
    });
    
    test("S3#deleteBucket", async (done) => {  
      const { bucketParams } = require("../src/s3_deleteBucket"); 
      // dont have to call run() as it's called on require by default.
      expect(mockDeleteBucket).toHaveBeenCalledWith(bucketParams);
      expect(mockDeleteBucket).toBeCalledTimes(1);
      done();
    });
  });
});
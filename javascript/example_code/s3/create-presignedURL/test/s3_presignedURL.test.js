const {urlParams} = require("./s3_presignedURL");
const mockedCreateBucket = jest.fn();
jest.mock("aws-sdk/clients/s3", () => {
  return class S3 {
    getSignedUrl(putObject, urlParams) {
      mockedCreateBucket(urlParams);
    }
  };
});

it("has to mock S3#createBucket",  () => {
  const generatePresignedURL = require("./s3_presignedURL").generatePresignedURL;
   generatePresignedURL();
  expect(mockedCreateBucket).toHaveBeenCalledWith(urlParams);
});




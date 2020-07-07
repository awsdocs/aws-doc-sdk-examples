
const mocksetUpBucketNonMod = jest.fn();
jest.mock('@aws-sdk/client-s3', () => ({
  Lambda: function lambda() {
    this.createBucket = mocksetUpBucketNonMod
  }
}));
const {staticHostParams, bucketParams, run} = require("../../lambda/tutorial/slotassets/s3-bucket-setup-non-modular");

//test function
test("has to mock s3#s3bucketsetupnonmod",  async (done) => {
  await run();
  expect(mocksetUpBucketNonMod).toHaveBeenCalled;
  done();
});

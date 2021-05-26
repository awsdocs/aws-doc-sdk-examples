const mocksetUpBucketNonMod = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
  Lambda: function lambda() {
    this.createBucket = mocksetUpBucketNonMod;
  },
}));
import {
  staticHostParams,
  bucketParams,
  run,
} from "../../lambda/tutorial/slotassets/s3-bucket-setup-non-modular";

test("has to mock s3#s3bucketsetupnonmod", async (done) => {
  await run();
  expect(mocksetUpBucketNonMod).toHaveBeenCalled;
  done();
});

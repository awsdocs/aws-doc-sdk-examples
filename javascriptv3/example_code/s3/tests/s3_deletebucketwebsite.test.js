jest.mock("../src/libs/s3Client.js");
jest.mock("@aws-sdk/client-s3");

// Get service clients module and commands.
import 'regenerator-runtime/runtime'
import { run, bucketParams } from "../src/s3_deletebucketwebsite.js";
import { s3Client } from "../src/libs/s3Client";

describe("@aws-sdk/client-s3 mock", () => {
  it("should successfully mock S3 client", async () => {
    s3Client.send.mockResolvedValue({ isMock: true });
    const response = await run(bucketParams);
    expect(response.isMock).toEqual(true);
  });
});

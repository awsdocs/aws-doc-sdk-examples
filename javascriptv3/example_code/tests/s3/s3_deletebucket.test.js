import { run, bucketParams } from "../../s3/src/s3_deletebucket";
import { s3Client } from "../../s3/src/libs/s3Client.js";

jest.mock("../../s3/src/libs/s3Client.js");

describe("@aws-sdk/client-s3 mock", () => {
  it("should successfully mock s3 client", async () => {
    s3Client.send.mockResolvedValue({ isMock: true });
    const response = await run(bucketParams);
    expect(response.isMock).toEqual(true);
  });
});

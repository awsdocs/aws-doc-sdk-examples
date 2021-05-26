import { run, params } from "../../nodegetstarted/src/sample.js";
import { s3Client } from "../../nodegetstarted/src/libs/s3Client.js";

jest.mock("../../nodegetstarted/src/libs/s3Client.js");

describe("@aws-sdk/client-s3 mock", () => {
  it("should successfully mock Amazon S3 client", async () => {
    s3Client.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

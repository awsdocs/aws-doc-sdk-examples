const { run, params } = require("../../nodegetstarted/src/sample.js");
const { s3Client } = require("../../nodegetstarted/src/libs/s3Client.js");

jest.mock("../../nodegetstarted/src/libs/s3Client.js");

describe("@aws-sdk/client-s3 mock", () => {
  it("should successfully mock Amazon S3 client", async () => {
    s3Client.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

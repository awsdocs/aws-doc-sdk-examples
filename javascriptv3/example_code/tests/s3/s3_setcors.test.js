const { run, corsParams } = require("../../s3/src/s3_setcors");
const { s3Client } = require("../../s3/src/libs/s3Client.js");

jest.mock("../../s3/src/libs/s3Client.js");

describe("@aws-sdk/client-s3 mock", () => {
  it("should successfully mock s3 client", async () => {
    s3Client.send.mockResolvedValue({ isMock: true });
    const response = await run(corsParams);
    expect(response.isMock).toEqual(true);
  });
});

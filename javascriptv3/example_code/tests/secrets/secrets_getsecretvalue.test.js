const { run, params } = require("../../secrets/src/secrets_getsecretvalue");
const { secretsClient } = require("../../secrets/src/libs/secretsClient.js");

jest.mock("../../secrets/src/libs/secretsClient.js");

describe("@aws-sdk/client-secrets mock", () => {
  it("should successfully mock Secrets Manager client", async () => {
    secretsClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

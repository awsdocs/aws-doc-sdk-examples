// Get service clients module and commands using CommonJS syntax.
const { run, params } = require("../../dynamodb/src/ddb_batchwriteitem");
const { ddbClient } = require("../../dynamodb/src/libs/ddbClient");

jest.mock("../../dynamodb/src/libs/ddbClient.js");

describe("@aws-sdk/client-dynamodb mock", () => {
  it("should successfully mock DynamoDB client", async () => {
    ddbClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

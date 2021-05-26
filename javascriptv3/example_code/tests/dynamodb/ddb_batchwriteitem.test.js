// Get service clients module and commands using CommonJS syntax.
import { run, params } from "../../dynamodb/src/ddb_batchwriteitem";
import { ddbClient } from "../../dynamodb/src/libs/ddbClient";

jest.mock("../../dynamodb/src/libs/ddbClient.js");

describe("@aws-sdk/client-dynamodb mock", () => {
  it("should successfully mock DynamoDB client", async () => {
    ddbClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

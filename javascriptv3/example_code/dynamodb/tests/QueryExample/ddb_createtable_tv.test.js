jest.mock("../../src/libs/ddbClient.js");
jest.mock("@aws-sdk/client-dynamodb");

// Get service clients module and commands.
import 'regenerator-runtime/runtime'
import { run, params } from "../../src/QueryExample/ddb_createtable_tv";
import { ddbClient } from "../src/libs/ddbClient";

describe("@aws-sdk/client-dynamodb mock", () => {
  it("should successfully mock dynamodb client", async () => {
    ddbClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

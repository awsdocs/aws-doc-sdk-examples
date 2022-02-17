// Get service clients module and commands using CommonJS syntax.
const { run, params } = require("../../dynamodb/src/ddbdoc_put_item");
const { ddbDocClient } = require("../../dynamodb/src/libs/ddbDocClient");

jest.mock("../../dynamodb/src/libs/ddbDocClient.js");

describe("@aws-sdk/client-ddb mock", () => {
    it("should successfully mock DynamoDB client", async () => {
        ddbDocClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

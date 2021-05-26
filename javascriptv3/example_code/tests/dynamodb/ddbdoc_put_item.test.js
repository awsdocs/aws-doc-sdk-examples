// Get service clients module and commands using CommonJS syntax.
import { run, params } from "../../dynamodb/src/ddbdoc_put_item";
import { ddbDocClient } from "../../dynamodb/src/libs/ddbDocClient";

jest.mock("../../dynamodb/src/libs/ddbDocClient.js");

describe("@aws-sdk/client-ddb mock", () => {
    it("should successfully mock DynamoDB client", async () => {
        ddbDocClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

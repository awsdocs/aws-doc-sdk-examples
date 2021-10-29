jest.mock("../src/libs/eventBridgeClient.js");
jest.mock("@aws-sdk/client-eventbridge");

// Get service clients module and commands.
import 'regenerator-runtime/runtime'
import { run, params } from "../src/putRule.js";
import { ebClient } from "../src/libs/eventBridgeClient.js";

describe("@aws-sdk/client-eventbridge mock", () => {
    it("should successfully mock Amazon EventBridge client", async () => {
        ebClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

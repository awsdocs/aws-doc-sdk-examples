jest.mock("../src/libs/codeBuildClient.js");
jest.mock("@aws-sdk/client-codebuild");

// Get service clients module and commands.
import 'regenerator-runtime/runtime'
import { run, params } from "../src/createProject";
import { codeBuildClient } from "../src/libs/codeBuildClient";

describe("@aws-sdk/client-codebuild mock", () => {
    it("should successfully mock CodeBuild client", async () => {
        codeBuildClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

jest.mock("../src/libs/codeCommitClient.js");
jest.mock("@aws-sdk/client-codecommit");

// Get service clients module and commands.
import 'regenerator-runtime/runtime'
import { run, params } from "../src/getMergeOptions";
import { codeCommitClient } from "../src/libs/codeCommitClient";

describe("@aws-sdk/client-codecommit mock", () => {
    it("should successfully mock CodeCommit client", async () => {
        codeCommitClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

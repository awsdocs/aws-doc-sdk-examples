jest.mock("../src/libs/personalizeClients.js");
jest.mock("@aws-sdk/client-personalize");

// Get service clients module and commands.
import 'regenerator-runtime/runtime';
import { run, createBatchSegmentJobParam }  from "../src/personalize_createBatchSegmentJob.js";
import { personalizeClient } from "../src/libs/personalizeClients";

describe("@aws-sdk/client-personalize mock", () => {
    it("should successfully mock personalize client", async () => {
        personalizeClient.send.mockResolvedValue({ isMock: true });
        const response = await run(createBatchSegmentJobParam);
        expect(response.isMock).toEqual(true);
    });
});


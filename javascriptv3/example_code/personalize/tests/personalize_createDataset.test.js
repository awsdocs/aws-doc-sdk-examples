jest.mock("../src/libs/personalizeClients.js");
jest.mock("@aws-sdk/client-personalize");

// Get service clients module and commands.
import 'regenerator-runtime/runtime';
import { run, createDatasetParam } from "../src/personalize_createDataset.js";
import { personalizeClient } from "../src/libs/personalizeClients";

describe("@aws-sdk/client-personalize mock", () => {
    it("should successfully mock personalize client", async () => {
        personalizeClient.send.mockResolvedValue({ isMock: true });
        console.log(createDatasetParam)
        const response = await run(createDatasetParam);
        expect(response.isMock).toEqual(true);
    });
});


jest.mock("../src/libs/personalizeClients.js");
jest.mock("@aws-sdk/client-personalize");

// Get service clients module and commands.
import 'regenerator-runtime/runtime';
import { run, createCampaignParam } from "../src/personalize_createCampaign.js";
import { personalizeClient } from "../src/libs/personalizeClients";

describe("@aws-sdk/client-personalize mock", () => {
    it("should successfully mock personalize client", async () => {
        personalizeClient.send.mockResolvedValue({ isMock: true });
        const response = await run(createCampaignParam);
        expect(response.isMock).toEqual(true);
    });
});


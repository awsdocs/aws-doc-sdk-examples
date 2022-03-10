jest.mock("../src/libs/personalizeClients.js");
jest.mock("@aws-sdk/client-personalize-runtime");

// Get service clients module and commands.
import 'regenerator-runtime/runtime';

import { run, getRecommendationsParam } from "../src/personalize_getRecommendationsWithFilter.js";
import { personalizeRuntimeClient } from "../src/libs/personalizeClients";

describe("@aws-sdk/client-personalize mock", () => {
    it("should successfully mock personalize client", async () => {
        personalizeRuntimeClient.send.mockResolvedValue({ isMock: true });
        const response = await run(getRecommendationsParam);
        expect(response.isMock).toEqual(true);
    });
});


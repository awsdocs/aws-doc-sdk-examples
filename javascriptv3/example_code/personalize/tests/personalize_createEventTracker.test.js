jest.mock("../src/libs/personalizeClients.js");
jest.mock("@aws-sdk/client-personalize");

// Get service clients module and commands.
import 'regenerator-runtime/runtime';

import { run, createEventTrackerParam } from "../src/personalize_createEventTracker.js";
import { personalizeClient } from "../src/libs/personalizeClients";

describe("@aws-sdk/client-personalize mock", () => {
    it("should successfully mock personalize client", async () => {
        personalizeClient.send.mockResolvedValue({ isMock: true });
        const response = await run(createEventTrackerParam);
        expect(response.isMock).toEqual(true);
    });
});


jest.mock("../src/libs/personalizeClients.js");
jest.mock("@aws-sdk/client-personalize-events");

// Get service clients module and commands.
import 'regenerator-runtime/runtime';

import { run, putItemsParam } from "../src/personalize_putItems.js";
import { personalizeEventsClient } from "../src/libs/personalizeClients";

describe("@aws-sdk/client-personalize mock", () => {
    it("should successfully mock personalize client", async () => {
        personalizeEventsClient.send.mockResolvedValue({ isMock: true });
        const response = await run(putItemsParam);
        expect(response.isMock).toEqual(true);
    });
});


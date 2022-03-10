jest.mock("../src/libs/personalizeClients.js");
jest.mock("@aws-sdk/client-personalize");

// Get service clients module and commands.
import 'regenerator-runtime/runtime';

import { run, createDomainSchemaParam } from "../src/personalize_createDomainSchema.js";
import { personalizeClient } from "../src/libs/personalizeClients";

describe("@aws-sdk/client-personalize mock", () => {
    it("should successfully mock personalize client", async () => {
        personalizeClient.send.mockResolvedValue({ isMock: true });
        const response = await run(createDomainSchemaParam);
        expect(response.isMock).toEqual(true);
    });
});


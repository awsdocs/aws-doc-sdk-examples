jest.mock("../src/libs/cloudWatchEventsClient.js");
jest.mock("@aws-sdk/client-cloudwatch-events");

// Get service clients module and commands.
import 'regenerator-runtime/runtime'
import { run, params } from "../src/putEvents.js";
import { cweClient } from "../src/libs/cloudWatchEventsClient.js";

describe("@aws-sdk/client-cloudwatch-events mock", () => {
    it("should run async equal", async (done) => {
        expect(1).toBe(1);
        done();
    });

    it("should be equal", () => {
        expect(1).toBe(1);
    });

    it("should successfully mock CloudWatch Events client", async () => {
        cweClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

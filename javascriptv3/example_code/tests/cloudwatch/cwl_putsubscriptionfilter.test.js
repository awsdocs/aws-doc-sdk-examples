// Get service clients module and commands using CommonJS syntax.
import { run, params } from "../../cloudwatch/src/cwl_putsubscriptionfilter";
import { cwlClient } from "../../cloudwatch/src/libs/cwlClient";

jest.mock("../../cloudwatch/src/libs/cwlClient.js");

describe("@aws-sdk/client-cloudwatch mock", () => {
    it("should successfully mock CloudWatch Logs client", async () => {
        cwlClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

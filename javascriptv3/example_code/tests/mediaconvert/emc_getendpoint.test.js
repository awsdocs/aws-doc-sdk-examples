import { run, params } from "../../mediaconvert/src/emc_getendpoint";
import { emcClientGet } from "../../mediaconvert/src/libs/emcClientGet";

jest.mock("../../mediaconvert/src/libs/emcClientGet.js");

describe("@aws-sdk/client-emc mock", () => {
    it("should successfully mock EMC client", async () => {
        emcClientGet.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

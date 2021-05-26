import { run, params } from "../../sns/src/sns_checkphoneoptout";
import { snsClient } from "../../sns/src/libs/snsClient.js";

jest.mock("../../sns/src/libs/snsClient.js");

describe("@aws-sdk/client-sns mock", () => {
    it("should successfully mock SNS client", async () => {
        snsClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

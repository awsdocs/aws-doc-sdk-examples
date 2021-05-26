const { run, params } = require("../../iam/src/iam_updateaccesskey");
const { iamClient } = require("../../iam/src/libs/iamClient.js");

jest.mock("../../iam/src/libs/iamClient.js");

describe("@aws-sdk/client-iam mock", () => {
    it("should successfully mock IAM client", async () => {
        iamClient.send.mockResolvedValue({ isMock: true });
        const response = await run(params);
        expect(response.isMock).toEqual(true);
    });
});

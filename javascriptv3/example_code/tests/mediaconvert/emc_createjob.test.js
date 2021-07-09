const { run, params } = require("../../mediaconvert/src/emc_create_jobtemplate");
const { emcClient } = require("../../mediaconvert/src/libs/emcClient");

jest.mock("../../mediaconvert/src/libs/emcClient.js");

describe("@aws-sdk/client-emc mock", () => {
  it("should successfully mock EMC client", async () => {
    emcClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

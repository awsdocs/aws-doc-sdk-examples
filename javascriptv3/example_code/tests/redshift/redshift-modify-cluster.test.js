const { run, params } = require("../../redshift/src/redshift-modify-cluster");
const { redshiftClient } = require("../../redshift/src/libs/redshiftClient.js");

jest.mock("../../redshift/src/libs/redshiftClient.js");

describe("@aws-sdk/client-redshift mock", () => {
  it("should successfully mock redshift client", async () => {
    redshiftClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
  });
});

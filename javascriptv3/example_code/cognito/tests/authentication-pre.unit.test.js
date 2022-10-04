import { handler } from "../scenarios/lambda-triggers/functions/authentication-pre.mjs";

describe("authentication-pre", () => {
  it("should throw an error if the declared client id matches", () => {
    expect.assertions(1);
    return expect(
      handler({
        callerContext: { clientId: "user-pool-app-client-id-to-be-blocked" },
      })
    ).rejects.toBeTruthy();
  });

  it("should not throw an error if the declared client id does not match", () => {
    expect.assertions(1);
    return expect(
      handler({
        callerContext: { clientId: "any-other-client-id" },
      })
    ).resolves.toBeTruthy();
  });
});

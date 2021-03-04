const mockGetSecrets = jest.fn();
jest.mock("@aws-sdk/client-secrets-manager", () => ({
    Secrets: function Secrets() {
        this.GetSecretsCommand = mockGetSecrets;
    },
}));
const { run } = require("../../secrets/src/secrets_getsecretvalue");

test("has to mock Secrets#getSecrets", async (done) => {
    await run();
    expect(mockGetSecrets).toHaveBeenCalled;
    done();
});

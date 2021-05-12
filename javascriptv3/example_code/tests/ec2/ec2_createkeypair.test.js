const mockCreateKeyPair = jest.fn();
jest.mock("@aws-sdk/client-ec2/commands/CreateKeyPairCommand", () => ({
  EC2: function EC2() {
    this.CreateKeyPairCommand = mockCreateKeyPair;
  },
}));
const { params, run } = require("../../ec2/src/ec2_createkeypair");

test("has to mock ec2#createKeyPair", async (done) => {
  await run();
  expect(mockCreateKeyPair).toHaveBeenCalled;
  done();
});

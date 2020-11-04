const mockPutTargets = jest.fn();
jest.mock(
  "@aws-sdk/client-cloudwatch-events/commands/PutTargetsCommand",
  () => ({
    CloudWatch: function CloudWatch() {
      this.PutTargetsCommand = mockPutTargets;
    },
  })
);
const { params, run } = require("../../cloudwatch/src/cwe_puttargets");

test("has to mock cloudwatch-events#puttargets", async (done) => {
  await run();
  expect(mockPutTargets).toHaveBeenCalled;
  done();
});

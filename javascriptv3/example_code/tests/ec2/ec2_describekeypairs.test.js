const mockDescribeKeyPairs = jest.fn();
jest.mock("@aws-sdk/client-ec2/commands/DescribeKeyPairsCommand", () => ({
  EC2: function EC2() {
    this.DescribeKeyPairsCommand = mockDescribeKeyPairs;
  },
}));
const { params, run } = require("../../ec2/src/ec2_describekeypairs");

test("has to mock ec2#describeKeyPairs", async (done) => {
  await run();
  expect(mockDescribeKeyPairs).toHaveBeenCalled;
  done();
});

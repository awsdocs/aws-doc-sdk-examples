const mockDescribeRegions = jest.fn();
jest.mock("@aws-sdk/client-ec2/commands/DescribeRegionsCommand", () => ({
  EC2: function EC2() {
    this.DescribeRegionsCommand = mockDescribeRegions;
  },
}));
const { params, run } = require("../../ec2/src/ec2_describeregionsandzones");

//test function
test("has to mock ec2#describeRegionsandZones", async (done) => {
  await run();
  expect(mockDescribeRegions).toHaveBeenCalled;
  done();
});

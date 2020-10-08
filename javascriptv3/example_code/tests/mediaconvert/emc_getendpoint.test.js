const mockDescribeEndpoints = jest.fn();
jest.mock(
  "@aws-sdk/client-mediaconvert/commands/DescribeEndpointsCommand",
  () => ({
    MediaConvert: function MediaConvert() {
      this.DescribeEndpointsCommand = mockDescribeEndpoints;
    },
  })
);
const { params, run } = require("../../mediaconvert/src/emc_getendpoint");

//test function
test("has to mock mediaconvert#describeendpoint", async (done) => {
  await run();
  expect(mockDescribeEndpoints).toHaveBeenCalled;
  done();
});

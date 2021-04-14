const mockList1000PlusObjects = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
  S3: function S3() {
    this.listObjects = mockList1000PlusObjects;
  },
}));
const { bucketParams } = require("../../s3/s3_list1000plusobjects");

test("has to mock S3#list1000PlusObjects", async (done) => {
  await run();
  expect(mockList1000PlusObjects).toHaveBeenCalled;
  done();
});

const mockGetBucketAcl = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/PutBucketAclCommand", () => ({
    S3: function S3() {
        this.PutBucketAclCommand = mockGetBucketAcl;
    },
}));
const { run } = require("../../s3/s3_putbucketacl");

test("has to mock S3#putBucketAcl", async (done) => {
    await run();
    expect(mockGetBucketAcl).toHaveBeenCalled;
    done();
});

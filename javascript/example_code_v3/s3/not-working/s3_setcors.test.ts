describe("S3#getCors", () => {

    const mockgetCors = jest.fn();
    jest.mock("@aws-sdk/client-s3", () => ({
        S3: function S3() {
            this.putBucketCors = mockgetCors;
        }
    }));
    const { run } = require("../src/s3_getcors");

    //test function
    test("has to mock S3#getCors", async (done) => {
        await run();
        expect(mockgetCors).toHaveBeenCalled;
        done();
    });

});
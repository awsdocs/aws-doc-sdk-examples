describe("S3 Object interactions", () => {

    let mockS3Response = {
        data: "EXAMPLE_RESPONSE"
    };


    let mockSend = jest.fn(() => mockS3Response);

    jest.mock("@aws-sdk/client-s3", () => ({
        S3: function S3() {
            this.send = mockSend;
        }

    }));

    describe("Default run invokes correctly", () => {
        // dont have to call run() as it's called on require by default.

        //TODO: build test cases for S3.Object operations

    });
});

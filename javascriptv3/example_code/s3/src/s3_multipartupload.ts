// Load the required clients and commands.
const {
    S3,
    CreateMultipartUploadCommand,
    UploadPartCommand,
    CompleteMultipartUploadCommand,
} = require("@aws-sdk/client-s3");

// Set the AWS Region.
const REGION = "REGION";

// Set the parameters.
const createParams = {
    Bucket: "brians-stack-mybucket160f8132-1phf3t7w4t5xn",
    Key: "mylambdafunction.js.zip"
};

// Specify how many parts in the upload. Between 1 and 10000.
const parts = 3; // For example, 3.

// Create an Amazon S3 service client object.
const s3Client = new S3({ region: REGION });

const run = async () => {
    try {
        // Create the mutlipart upload.
        const data = await s3Client.send(
            new CreateMultipartUploadCommand(createParams)
        );
        console.log("Upload started. Upload ID: ", data.UploadId);
        // Use loop to run UploadPartCommand for each part.
        var i;
        for (i = 0; i < parts; i++) {
            var uploadParams = {
                Bucket: createParams.Bucket,
                Key: createParams.Key,
                PartNumber: i,
                UploadId: data.UploadId,
            };
            try {
                const data = await s3Client.send(new UploadPartCommand(uploadParams));
                console.log("Part uploaded. ETag: ", data.ETag);
                var completeParams = {
                    Bucket: createParams.Bucket,
                    Key: createParams.Key,
                    MultipartUpload: {
                        Parts: [
                            {
                                ETag: data.ETag,
                                PartNumber: i,
                            },
                        ],
                    },
                    UploadId: uploadParams.UploadId,
                }
                console.log(completeParams)
            } catch (err) {
                console.log("Error uploading part", err);
            }
        }
    } catch (err) {
        console.log("Error creating upload ", err);
    }
    try {
        // Complete the mutlipart upload.
        const data = await s3Client.send(
            new CompleteMultipartUploadCommand(completeParams)
        );
        console.log("Upload completed. File location: ", data.Location);
    } catch (err) {
        console.log("Error ", err);
    }
};
run();

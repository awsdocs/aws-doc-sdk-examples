// Import an S3 client
import { S3Client, PutBucketPolicyCommand } from '@aws-sdk/client-s3';

// Set the AWS Region
const REGION = "eu-west-1"; //e.g. "us-east-1"

// Create params JSON for S3.createBucket
const BUCKET_NAME = "BUCKET_NAME";
const bucketParams = {
  Bucket : BUCKET_NAME
};

const readOnlyAnonUserPolicy = {
    Version: "2012-10-17",
    Statement: [
        {
            Sid: "AddPerm",
            Effect: "Allow",
            Principal: "*",
            Action: [
                "s3:GetObject"
            ],
            Resource: [
                ""
            ]
        }
    ]
};

// create selected bucket resource string for bucket policy
const bucketResource = "arn:aws:s3:::" + BUCKET_NAME + "/*"; //BUCKET_NAME
readOnlyAnonUserPolicy.Statement[0].Resource[0] = bucketResource;

// // convert policy JSON into string and assign into params
const bucketPolicyParams = {Bucket: BUCKET_NAME, Policy: JSON.stringify(readOnlyAnonUserPolicy)};

// // Instantiate an S3 client
const s3 = new S3Client({});

(async () => {
    try{
        s3.send

        // const response = await s3.putBucketPolicy(bucketPolicyParams);
        const response = await s3.send(new PutBucketPolicyCommand(bucketPolicyParams));
        console.log('Success, permissions added to bucket', response)
    }
    catch(err){
        console.log('Error', err);
    }
})();

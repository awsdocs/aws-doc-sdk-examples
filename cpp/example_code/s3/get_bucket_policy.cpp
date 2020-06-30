// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

// snippet-start:[s3.cpp.get_bucket_policy.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/GetBucketPolicyRequest.h>
#include <awsdoc/s3/s3_examples.h>
// snippet-end:[s3.cpp.get_bucket_policy.inc]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Gets information about a bucket policy for a bucket
 * in Amazon S3.
 *
 * Prerequisites: The bucket to get bucket policy information about.
 *
 * Inputs:
 * - bucketName: The name of the bucket to get bucket policy information about.
 * - region: The AWS Region for the bucket.
 *
 * Outputs: true if information about the bucket policy was retrieved; 
 * otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[s3.cpp.get_bucket_policy.code]
bool AwsDoc::S3::GetBucketPolicy(const Aws::String& bucketName, 
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::GetBucketPolicyRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::GetBucketPolicyOutcome outcome =
        s3_client.GetBucketPolicy(request);

    if (outcome.IsSuccess())
    {
        Aws::StringStream policy_stream;
        Aws::String line;

        outcome.GetResult().GetPolicy() >> line;
        policy_stream << line;

        std::cout << "Policy:" << std::endl << std::endl << 
            policy_stream.str() << std::endl;

        return true;
    }
    else
    {
        auto err = outcome.GetError();
        std::cout << "Error: GetBucketPolicy: "
            << err.GetExceptionName() << ": " << err.GetMessage() << std::endl;

        return false;
    }
}

int main()
{
    Aws::String bucket_name = "my-bucket";
    Aws::String region = "us-east-1";

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (!AwsDoc::S3::GetBucketPolicy(bucket_name, region))
        {
            return 1;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3.cpp.get_bucket_policy.code]
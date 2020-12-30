// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

// snippet-start:[s3.cpp.delete_bucket_policy.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketPolicyRequest.h>
#include <awsdoc/s3/s3_examples.h>
// snippet-end:[s3.cpp.delete_bucket_policy.inc]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Deletes the bucket policy from a bucket in Amazon S3.
 *
 * Prerequisites: The bucket containing the bucket policy to be deleted.
 *
 * Inputs:
 * - bucketName: The name of the bucket containing the bucket policy 
     to be deleted.
 * - region: The AWS Region of the bucket.
 *
 * Outputs: true if the bucket policy was deleted; otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

 // snippet-start:[s3.cpp.delete_bucket_policy.code]
bool AwsDoc::S3::DeleteBucketPolicy(const Aws::String& bucketName,const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;

    if (!region.empty())
    {
        config.region = region;
    }

    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::DeleteBucketPolicyRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketPolicyOutcome outcome =
        s3_client.DeleteBucketPolicy(request);

    if (!outcome.IsSuccess())
    {
        auto err = outcome.GetError();
        std::cout << "Error: DeleteBucketPolicy: " <<
            err.GetExceptionName() << ": " << err.GetMessage() << std::endl;

        return false;
    }

    return true;
}

int main()
{
    Aws::String bucket_name = "my-bucket";

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (AwsDoc::S3::DeleteBucketPolicy(bucket_name))
        {
            std::cout << "Deleted bucket policy from '" << bucket_name <<
                "'." << std::endl;
        }
        else
        {
            return 1;
        }
    }
    ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3.cpp.delete_bucket_policy.code]

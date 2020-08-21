// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

// snippet-start:[s3.cpp.delete_bucket.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <awsdoc/s3/s3_examples.h>
// snippet-end:[s3.cpp.delete_bucket.inc]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Deletes a bucket from Amazon S3.
 *
 * Prerequisites: The bucket to be deleted.
 *
 * Inputs:
 * - bucketName: The name of the bucket to delete.
 * - region: The AWS Region of the bucket to delete.
 *
 * Outputs: true if the bucket was deleted; otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[s3.cpp.delete_bucket.code]
bool AwsDoc::S3::DeleteBucket(const Aws::String& bucketName, 
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::DeleteBucketRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketOutcome outcome = 
        s3_client.DeleteBucket(request);

    if (!outcome.IsSuccess()) 
    {
        auto err = outcome.GetError();
        std::cout << "Error: DeleteBucket: " <<
            err.GetExceptionName() << ": " << err.GetMessage() << std::endl;

        return false;
    }

    return true;
}

int main()
{
    Aws::String bucket_name = "my-bucket";
    Aws::String region = "us-east-1";
    
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (AwsDoc::S3::DeleteBucket(bucket_name, region))
        {
            std::cout << "Deleted bucket '" << bucket_name <<
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
// snippet-end:[s3.cpp.delete_bucket.code]

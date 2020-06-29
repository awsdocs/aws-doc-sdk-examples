// Copyright Amazon.com, Inc. or its affiliates.All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

// snippet-start:[s3.cpp.delete_website_config.inc]
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketWebsiteRequest.h>
#include <awsdoc/s3/s3_examples.h>
// snippet-end:[s3.cpp.delete_website_config.inc]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Removes the website configuration for a bucket in Amazon S3.
 *
 * Prerequisites: The bucket containing the website configuration to 
 * be removed.
 *
 * Inputs:
 * - bucketName: The name of the bucket containing the website configuration to 
 *   be removed.
 * - region: The AWS Region of the bucket.
 *
 * Outputs: true if the website configuration was removed; otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[s3.cpp.delete_website_config.code]
bool AwsDoc::S3::DeleteBucketWebsite(const Aws::String& bucketName, 
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::DeleteBucketWebsiteRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketWebsiteOutcome outcome =
        s3_client.DeleteBucketWebsite(request);

    if (!outcome.IsSuccess())
    {
        auto err = outcome.GetError();
        std::cout << "Error: DeleteBucketWebsite: " <<
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
        if (AwsDoc::S3::DeleteBucketWebsite(bucket_name, region))
        {
            std::cout << "Removed website configuration from '" << 
                bucket_name << "'." << std::endl;
        }
    }
    ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3.cpp.delete_website_config.code]
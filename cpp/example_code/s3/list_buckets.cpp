// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

// snippet-start:[s3.cpp.list_buckets.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/Bucket.h>
#include <awsdoc/s3/s3_examples.h>
// snippet-end:[s3.cpp.list_buckets.inc]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Lists all available Amazon S3 bucket names in the caller's account.
 *
 * Outputs: true if the list of available buckets was retrieved; 
 * otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[s3.cpp.list_buckets.code]
bool AwsDoc::S3::ListBuckets()
{
    Aws::S3::S3Client s3_client;
    Aws::S3::Model::ListBucketsOutcome outcome = s3_client.ListBuckets();

    if (outcome.IsSuccess())
    {
        std::cout << "Bucket names:" << std::endl << std::endl;

        Aws::Vector<Aws::S3::Model::Bucket> buckets =
            outcome.GetResult().GetBuckets();

        for (Aws::S3::Model::Bucket& bucket : buckets)
        {
            std::cout << bucket.GetName() << std::endl;
        }

        return true;
    }
    else
    {
        std::cout << "Error: ListBuckets: "
            << outcome.GetError().GetMessage() << std::endl;

        return false;
    }
}

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (!AwsDoc::S3::ListBuckets())
        {
            return 1;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3.cpp.list_buckets.code]

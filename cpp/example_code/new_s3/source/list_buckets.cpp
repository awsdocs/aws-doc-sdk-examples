/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
/*
 * snippet-sourcedescription:[list_buckets.cpp demonstrates how to list all the buckets in an AWS account.]
 * snippet-keyword:[C++]
 * snippet-sourcesyntax:[cpp]
 * snippet-keyword:[Code Sample]
 * snippet-keyword:[Amazon S3]
 * snippet-service:[s3]
 * snippet-sourcetype:[full-example]
 * snippet-sourcedate:[]
 * snippet-sourceauthor:[AWS]
 */

#include <awsdoc/s3/s3_examples.h>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>

bool AwsDoc::S3::ListBuckets()
{
    Aws::S3::S3Client s3_client;
    auto outcome = s3_client.ListBuckets();

    if (outcome.IsSuccess())
    {
        std::cout << "Your Amazon S3 buckets:" << std::endl;

        Aws::Vector<Aws::S3::Model::Bucket> bucket_list =
            outcome.GetResult().GetBuckets();

        for (auto const &bucket : bucket_list)
        {
            std::cout << "  * " << bucket.GetName() << std::endl;
        }
        return true;
    }
    else
    {
        std::cout << "ListBuckets error: "
            << outcome.GetError().GetExceptionName() << " - "
            << outcome.GetError().GetMessage() << std::endl;
        return true;
    }
}

int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        AwsDoc::S3::ListBuckets();
    }
    Aws::ShutdownAPI(options);
    return 0;
}

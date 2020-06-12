//snippet-sourcedescription:[Upgrade AWS SDK for C++ to version 1.8 to build list_objects_with_aws_global_region.cpp. This example demonstrates how to use aws-global region in client configuration to make cross-region requests.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

#include <aws/core/Aws.h>
#include <aws/core/utils/logging/LogLevel.h>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/HeadBucketRequest.h>
#include <aws/s3/model/ListObjectsRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <iostream>

using namespace Aws;
using namespace Aws::S3;
using namespace Aws::S3::Model;

static const char BUCKET_NAME[] = "aws-sdk-cpp-list-objects-with-aws-global-region";

/**
 * With AWS SDK for C++ version 1.8, you are able to make cross region requests by specifying aws-global as region in client configuration.
 * In this example, an S3 client in aws-global region is able to list objects in the S3 bucket in us-west-2.
 */
int main(int argc, char *argv[])
{
    SDKOptions options;
    options.loggingOptions.logLevel = Utils::Logging::LogLevel::Trace;
    InitAPI(options);
    {
        Aws::Client::ClientConfiguration config;
        config.region = Aws::Region::AWS_GLOBAL;
        S3Client s3Client(config);

        // Create a bucket in us-west-2.
        CreateBucketRequest createBucketRequest;
        createBucketRequest.SetBucket(BUCKET_NAME);
        CreateBucketConfiguration createBucketConfiguration;
        createBucketConfiguration.SetLocationConstraint(BucketLocationConstraint::us_west_2);
        createBucketRequest.SetCreateBucketConfiguration(createBucketConfiguration);
        auto createBucketOutcome = s3Client.CreateBucket(createBucketRequest);

        if (createBucketOutcome.IsSuccess()) {
            std::cout << "Succeeded to create bucket: " << BUCKET_NAME << std::endl;
        } else {
            std::cout << "Failed to create bucket. Details of the error:" << std::endl;
            std::cout << createBucketOutcome.GetError() << std::endl;
        }

        // Wait for bucket to propagate.
        unsigned timeoutCount = 0;
        while (timeoutCount++ < 20)
        {
            HeadBucketRequest headBucketRequest;
            headBucketRequest.SetBucket(BUCKET_NAME);
            HeadBucketOutcome headBucketOutcome = s3Client.HeadBucket(headBucketRequest);
            if (headBucketOutcome.IsSuccess())
            {
                break;
            }

            std::this_thread::sleep_for(std::chrono::seconds(10));
        }

        // S3 client with aws-global region should be able to get access to bucket in any region.
        ListObjectsRequest listObjectsRequest;
        listObjectsRequest.SetBucket(BUCKET_NAME);
        auto listObjectOutcome = s3Client.ListObjects(listObjectsRequest);

        if (listObjectOutcome.IsSuccess()) {
            std::cout << "Found objects in bucket: " << BUCKET_NAME << std::endl;
            std::cout << "Number of objects in this bucket: " << listObjectOutcome.GetResult().GetContents().size() << std::endl;
        }
        else
        {
            std::cout << "Failed to list objects. Details of the error:" << std::endl;
            std::cout << listObjectOutcome.GetError() << std::endl;
        }

        DeleteBucketRequest deleteBucketRequest;
        deleteBucketRequest.SetBucket(BUCKET_NAME);
        auto deleteBucketOutcome = s3Client.DeleteBucket(deleteBucketRequest);
        if (deleteBucketOutcome.IsSuccess())
        {
            std::cout << "Succeeded to delete bucket" << std::endl;
        }
        else
        {
            std::cout << "Failed to delete bucket. Details of the error:" << std::endl;
            std::cout << deleteBucketOutcome.GetError() << std::endl;
        }
    }

    ShutdownAPI(options);
    return 0;
}
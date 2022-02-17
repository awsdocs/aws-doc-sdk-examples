//snippet-sourcedescription:[This example demonstrates how to use the AWS SDK for C++, starting with SDK version 1.8, to specify aws-global as the AWS Region during Amazon S3 API client configuration to make requests to S3 across AWS Regions.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2020-08-10]
//snippet-sourceauthor:[AWS]

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/core/utils/logging/LogLevel.h>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/HeadBucketRequest.h>
#include <aws/s3/model/ListObjectsRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <iostream>
#include <awsdoc/s3/s3_list_objects_with_aws_global_region.h>

using namespace Aws;
using namespace Aws::S3;
using namespace Aws::S3::Model;

static const char BUCKET_NAME[] = "aws-sdk-cpp-list-objects-with-aws-global-region";

/**
 * Starting with the AWS SDK for C++ version 1.8, you can make requests to 
 * Amazon S3 across AWS Regions by specifying aws-global as the AWS Region 
 * during S3 API client configuration. In this example, an S3 API client 
 * set to the aws-global AWS Region is able to list objects in an S3 bucket 
 * that is located in the us-west-2 AWS Region.
 */

/** 
 * Following are helper methods for creating the bucket, listing the objects 
 * in the new bucket, and then deleting the bucket. These methods are later 
 * called from this example's main method.
 */

bool CreateABucket(const S3Client& s3Client)
{
    // Create an S3 bucket within the us-west-2 AWS Region.
    CreateBucketRequest createBucketRequest;
    createBucketRequest.SetBucket(BUCKET_NAME);
    CreateBucketConfiguration createBucketConfiguration;
    createBucketConfiguration.SetLocationConstraint(BucketLocationConstraint::us_west_2);
    createBucketRequest.SetCreateBucketConfiguration(createBucketConfiguration);
    auto createBucketOutcome = s3Client.CreateBucket(createBucketRequest);

    if (createBucketOutcome.IsSuccess()) {
        std::cout << "Success. Created the bucket named '" << BUCKET_NAME << 
            "'." << std::endl;
    }
    else {
        std::cout << "Error. Could not create the bucket: " << 
            createBucketOutcome.GetError() << std::endl;

        return false;
    }

    // Wait for the bucket to propagate before continuing.
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

    return true;
}

bool ListTheObjects(const S3Client& s3Client)
{
    // An S3 API client set to the aws-global AWS Region should be able to get 
    // access to a bucket in any AWS Region.
    ListObjectsRequest listObjectsRequest;
    listObjectsRequest.SetBucket(BUCKET_NAME);
    auto listObjectOutcome = s3Client.ListObjects(listObjectsRequest);
    
    if (listObjectOutcome.IsSuccess()) {
        std::cout << "Success. Number of objects in the bucket named '" << 
            BUCKET_NAME << "' is " << 
            listObjectOutcome.GetResult().GetContents().size() << "." << 
            std::endl;

        return true;
    }
    else
    {
        std::cout << "Error. Could not count the objects in the bucket: " << 
            listObjectOutcome.GetError() << std::endl;

        return false;
    }
}

bool DeleteABucket(const S3Client& s3Client)
{
    DeleteBucketRequest deleteBucketRequest;
    deleteBucketRequest.SetBucket(BUCKET_NAME);
    auto deleteBucketOutcome = s3Client.DeleteBucket(deleteBucketRequest);

    if (deleteBucketOutcome.IsSuccess())
    {
        std::cout << "Success. Deleted the bucket named '" << BUCKET_NAME << 
            "'." << std::endl;

        return true;
    }
    else
    {
        std::cout << "Error. Could not delete the bucket: " << 
            deleteBucketOutcome.GetError() << std::endl;
        std::cout << "To clean up, you must delete the bucket named '" <<
            BUCKET_NAME << "' yourself." << std::endl;

        return false;
    }
}

int main()
{
    SDKOptions options;
    options.loggingOptions.logLevel = Utils::Logging::LogLevel::Trace;

    InitAPI(options);
    {
        Aws::Client::ClientConfiguration config;
        config.region = Aws::Region::AWS_GLOBAL;

        S3Client s3Client(config);

        if (!CreateABucket(s3Client))
        {
            return 1;
        }
        
        if (!ListTheObjects(s3Client))
        {
            return 1;
        }

        if (!DeleteABucket(s3Client))
        {
            return 1;
        }
    }
    ShutdownAPI(options);
    
    return 0;
}
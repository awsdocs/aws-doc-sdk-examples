//snippet-sourcedescription:[delete_bucket.cpp demonstrates how to delete an Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for C++]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[12/15/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[s3.cpp.delete_bucket.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include "awsdoc/s3/s3_examples.h"
// snippet-end:[s3.cpp.delete_bucket.inc]

/* 
 * 
 * Prerequisites: The bucket to be deleted.
 *
 * Inputs:
 * - bucketName: The name of the bucket to delete.
 * - region: The AWS Region of the bucket to delete.
 * 
 *  To run this C++ code example, ensure that you have setup your development environment, including your credentials.
 *  For information, see this documentation topic:
 *  https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 * 
 */

 // snippet-start:[s3.cpp.delete_bucket.code]
using namespace Aws;

bool AwsDoc::S3::DeleteBucket(const Aws::String &bucketName, const Aws::String &region)
{
    Aws::Client::ClientConfiguration clientConfig;
    if (!region.empty()) {
        clientConfig.region = region;
    }

    Aws::S3::S3Client client(clientConfig);

    Aws::S3::Model::DeleteBucketRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketOutcome outcome =
            client.DeleteBucket(request);

    if (!outcome.IsSuccess())
    {
        auto err = outcome.GetError();
        std::cout << "Error: DeleteBucket: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        return false;
    }
    else
    {
        std::cout << "The bucket was deleted" << std::endl;
        return true;

    }
}

int main()
{
    //TODO: Change bucket_name to the name of a bucket in your account.
    //If the bucket is not in your account, you will get one of two errors:
    Aws::String bucketName = "<Bucket Name>";
    //TODO:  Set to the AWS Region of the bucket bucket_name.
    Aws::String region = "us-east-1";

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    AwsDoc::S3::DeleteBucket(bucketName, region);

    ShutdownAPI(options);
 }
// snippet-end:[s3.cpp.delete_bucket.code]

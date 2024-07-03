// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include "s3_examples.h"

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to delete an S3 bucket.
 *
 */

//! Routine which demonstrates deleting an S3 bucket.
/*!
  \param bucketName: Name of the bucket to delete.
  \param clientConfig: Aws client configuration.
  \return bool: Function succeeded.
*/

// snippet-start:[s3.cpp.delete_bucket.code]
bool AwsDoc::S3::deleteBucket(const Aws::String &bucketName,
                              const Aws::S3::S3ClientConfiguration &clientConfig) {

    Aws::S3::S3Client client(clientConfig);

    Aws::S3::Model::DeleteBucketRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketOutcome outcome =
            client.DeleteBucket(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "Error: deleteBucket: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    } else {
        std::cout << "The bucket was deleted" << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.delete_bucket.code]

/*
 *
 * main function
 *
 * Prerequisites: The bucket to be deleted.
 *
 * usage: run_delete_bucket <bucket_name>
 *
*/

#ifndef TESTING_BUILD

int main(int argc, char* argv[]) {
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    if (argc != 2) {
        std::cout << R"(
Usage:
    run_delete_bucket <bucket_name>
Where:
    bucket_name - The name of the bucket to delete.
)" << std::endl;
        return 1;
    }

    Aws::String bucketName = argv[1];

    {
        Aws::S3::S3ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::S3::deleteBucket(bucketName, clientConfig);
    }

    ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD


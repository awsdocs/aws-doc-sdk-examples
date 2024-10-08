// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <iostream>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/BucketLocationConstraint.h>
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
 * Demonstrates using the AWS SDK for C++ to create an S3 bucket.
 *
 */

//! Routine which demonstrates creating an S3 bucket.
/*!
  \param bucketName: Name of bucket to create.
  \param clientConfig: Aws client configuration.
  \return bool: Function succeeded.
*/

// snippet-start:[s3.cpp.create_bucket.code]
bool AwsDoc::S3::createBucket(const Aws::String &bucketName,
                              const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::CreateBucketRequest request;
    request.SetBucket(bucketName);

    if (clientConfig.region != "us-east-1") {
        Aws::S3::Model::CreateBucketConfiguration createBucketConfig;
        createBucketConfig.SetLocationConstraint(
                Aws::S3::Model::BucketLocationConstraintMapper::GetBucketLocationConstraintForName(
                        clientConfig.region));
        request.SetCreateBucketConfiguration(createBucketConfig);
    }

    Aws::S3::Model::CreateBucketOutcome outcome = client.CreateBucket(request);
    if (!outcome.IsSuccess()) {
        auto err = outcome.GetError();
        std::cerr << "Error: createBucket: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    } else {
        std::cout << "Created bucket " << bucketName <<
                  " in the specified AWS Region." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.create_bucket.code]

/*
 *
 *  main function
 *
 *  Usage: 'run_create_bucket <bucket_name_prefix>
 *
 */

#ifndef EXCLUDE_MAIN_FUNCTION

int main(int argc, char* argv[]) {
    Aws::SDKOptions options;
    InitAPI(options);

    if (argc != 2) {
        std::cout << R"(
Usage:
    run_create_bucket <bucket_name_prefix>
Where:
    bucket_name - A bucket name prefix which will be made unique by appending a UUID.
)" << std::endl;
        return 1;
    }

    Aws::String bucketNamePrefix = argv[1];

    {
        Aws::S3::S3ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        // Create a unique bucket name to increase the chance of success
        // when trying to create the bucket.
        // Format: "<bucketNamePrefix> + "-" + lowercase UUID.
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String bucketName = bucketNamePrefix + "-" +
                               Aws::Utils::StringUtils::ToLower(uuid.c_str());

        AwsDoc::S3::createBucket(bucketName, clientConfig);
    }

    ShutdownAPI(options);
}

#endif // EXCLUDE_MAIN_FUNCTION


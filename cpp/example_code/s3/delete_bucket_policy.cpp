// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketPolicyRequest.h>
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
 * Demonstrates using the AWS SDK for C++ to delete the policy in an S3 bucket.
 *
 */

//! Routine which demonstrates deleting the policy in an S3 bucket.
/*!
  \sa deleteBucketPolicy()
  \param toBucket Name of a bucket with the policy to delete.
  \param clientConfig Aws client configuration.
*/

// snippet-start:[s3.cpp.delete_bucket_policy.code]
bool AwsDoc::S3::deleteBucketPolicy(const Aws::String &bucketName,
                                    const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);

    Aws::S3::Model::DeleteBucketPolicyRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketPolicyOutcome outcome = client.DeleteBucketPolicy(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "Error: deleteBucketPolicy: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    } else {
        std::cout << "Policy was deleted from the bucket." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.delete_bucket_policy.code]


/*
 *
 * main function
 *
 * usage 'run_delete_bucket_policy <bucket_name>'
 *
 * Prerequisites: The bucket with a policy to be deleted.
 *
*/

#ifndef TESTING_BUILD

int main(int argc, char* argv[]) {
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    if (argc != 2) {
        std::cout << R"(
Usage:
    run_delete_bucket_policy <bucket_name>
Where:
    bucket_name - The name of the bucket to delete the policy from.
)" << std::endl;
        return 1;
    }

    Aws::String bucketName = argv[1];

    {
        Aws::S3::S3ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::S3::deleteBucketPolicy(bucketName, clientConfig);
    }

    ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD
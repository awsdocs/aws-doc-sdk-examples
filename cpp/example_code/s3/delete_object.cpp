// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteObjectRequest.h>
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
 * Demonstrates using the AWS SDK for C++ to delete an object in an S3 bucket.
 *
 */

//! Routine which demonstrates deleting an object in an S3 bucket.
/*!
  \param objectKey: Name of an object.
  \param fromBucket: Name of a bucket with an object to delete.
  \param clientConfig: Aws client configuration.
  \return bool: Function succeeded.
*/

// snippet-start:[s3.cpp.delete_object.code]
bool AwsDoc::S3::deleteObject(const Aws::String &objectKey,
                              const Aws::String &fromBucket,
                              const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::DeleteObjectRequest request;

    request.WithKey(objectKey)
            .WithBucket(fromBucket);

    Aws::S3::Model::DeleteObjectOutcome outcome =
            client.DeleteObject(request);

    if (!outcome.IsSuccess()) {
        auto err = outcome.GetError();
        std::cerr << "Error: deleteObject: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    } else {
        std::cout << "Successfully deleted the object." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.delete_object.code]

/*
 *
 * main function
 *
 * usage: 'run_delete_object <object_key> <from_bucket>'
 *
 * Prerequisites: The bucket containing the object to delete.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char* argv[]) {
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    if (argc != 3) {
        std::cout << R"(
Usage:
    run_delete_object <object_key> <from_bucket>
Where:
    object_key - The unique identifier for the object in the bucket.
    from_bucket - The name of the bucket containing the object.
)" << std::endl;
        return 1;
    }

    Aws::String objectKey = argv[1];
    Aws::String fromBucket = argv[2];

    {
        Aws::S3::S3ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::S3::deleteObject(objectKey, fromBucket, clientConfig);
    }

    ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD

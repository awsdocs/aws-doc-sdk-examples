// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CopyObjectRequest.h>
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
 * Demonstrates using the AWS SDK for C++ to copy an object between two S3 buckets.
 *
 */

//! Routine which demonstrates copying an object between two S3 buckets.
/*!
  \param objectKey Key of object in from bucket.
  \param fromBucket Name of from bucket.
  \param toBucket Name of to bucket.
  \param clientConfig Aws client configuration.
  \return bool: Function succeeded.
*/

// snippet-start:[s3.cpp.copy_objects.code]
bool AwsDoc::S3::copyObject(const Aws::String &objectKey, const Aws::String &fromBucket, const Aws::String &toBucket,
                            const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::CopyObjectRequest request;

    request.WithCopySource(fromBucket + "/" + objectKey)
            .WithKey(objectKey)
            .WithBucket(toBucket);

    Aws::S3::Model::CopyObjectOutcome outcome = client.CopyObject(request);
    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "Error: copyObject: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;

    } else {
        std::cout << "Successfully copied " << objectKey << " from " << fromBucket <<
                  " to " << toBucket << "." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.copy_objects.code]

/*
 *
 *  main function
 *
 *
 *  Usage: 'run_copy_object <object_key> <from_bucket> <to_bucket>'
 *
 * Prerequisites: Two buckets. One of the buckets must contain the object to
 * be copied to the other bucket.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char* argv[]) {
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    if (argc != 4) {
        std::cout << R"(
Usage:
    run_copy_object <object_key> <from_bucket> <to_bucket>
Where:
    object_key - The name of the object to copy.
    from_bucket - The name of the bucket containing the object.
    to_bucket - The name of the bucket to copy the object to.
)" << std::endl;
        return 1;
    }

    Aws::String objectKey = argv[1];
    Aws::String fromBucket = argv[2];
    Aws::String toBucket = argv[3];

    {
        Aws::S3::S3ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::copyObject(objectKey, fromBucket, toBucket, clientConfig);
    }

    ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD


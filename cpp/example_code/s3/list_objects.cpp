// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/ListObjectsV2Request.h>
#include <aws/s3/model/Object.h>
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
 * Demonstrates using the AWS SDK for C++ to list the objects in an S3 bucket.
 *
 */

//! Routine which demonstrates listing the objects in an S3 bucket.
/*!
  \fn listObjects()
  \param bucketName Name of the S3 bucket.
  \param clientConfig Aws client configuration.
 */

// snippet-start:[s3.cpp.list_objects.code]
bool AwsDoc::S3::listObjects(const Aws::String &bucketName,
                             const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3Client(clientConfig);

    Aws::S3::Model::ListObjectsV2Request request;
    request.WithBucket(bucketName);

    Aws::String continuationToken; // Used for pagination.
    Aws::Vector<Aws::S3::Model::Object> allObjects;

    do {
        if (!continuationToken.empty()) {
            request.SetContinuationToken(continuationToken);
        }

        auto outcome = s3Client.ListObjectsV2(request);

        if (!outcome.IsSuccess()) {
            std::cerr << "Error: listObjects: " <<
                      outcome.GetError().GetMessage() << std::endl;
            return false;
        } else {
            Aws::Vector<Aws::S3::Model::Object> objects =
                    outcome.GetResult().GetContents();

            allObjects.insert(allObjects.end(), objects.begin(), objects.end());
            continuationToken = outcome.GetResult().GetNextContinuationToken();
        }
    } while (!continuationToken.empty());

    std::cout << allObjects.size() << " object(s) found:" << std::endl;

    for (const auto &object: allObjects) {
        std::cout << "  " << object.GetKey() << std::endl;
    }

    return true;
}
// snippet-end:[s3.cpp.list_objects.code]

/*
 *
 *   main function
 *
 * Prerequisites: Create a bucket containing at least one object.
 *
 * usage: run_list_objects <bucket_name>
 *
 * Where:
 *   bucket_name - The name of the bucket that contains the objects.
 *.
 */

#ifndef TESTING_BUILD

int main(int argc, char* argv[])
{
    if (argc != 2)
    {
        std::cout << R"(
Usage:
    run_list_objects <bucket_name>
Where:
    bucket_name - The name of the bucket that contains the objects.
)" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String bucketName = argv[1];

        Aws::S3::S3ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::S3::listObjects(bucketName, clientConfig);
    }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif  // TESTING_BUILD


// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/GetObjectRequest.h>
#include <fstream>
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
 * Demonstrates using the AWS SDK for C++ to getting an object in an S3 bucket.
 *
 */

//! Routine which demonstrates getting an object in an S3 bucket.
/*!
  \sa getObject()
  \param objectKey Name of an object in a bucket.
  \param toBucket: Name of a bucket.
  \param clientConfig: Aws client configuration.
*/

// snippet-start:[s3.cpp.get_object.code]
bool AwsDoc::S3::getObject(const Aws::String &objectKey,
                           const Aws::String &fromBucket,
                           const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);

    Aws::S3::Model::GetObjectRequest request;
    request.SetBucket(fromBucket);
    request.SetKey(objectKey);

    Aws::S3::Model::GetObjectOutcome outcome =
            client.GetObject(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "Error: getObject: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    } else {
        std::cout << "Successfully retrieved '" << objectKey << "' from '"
                  << fromBucket << "'." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.get_object.code]

/*
 *
 * main function
 *
 * usage: run_get_object <object_name> <bucket_name>
 *
 * Where:
 *  object_name - the name of an object in the bucket.
 *  bucket_name - the name of a bucket.
 *
 * Prerequisites: The bucket with an object to be retrieved.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char* argv[]) {
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    if (argc != 3) {
        std::cout << R"(
Usage:
    run_get_object <object_name> <bucket_name>
Where:
    object_name - The name of the object to retrieve.
    bucket_name - The name of the bucket containing the object.
)" << std::endl;
        return 1;
    }

    Aws::String objectName = argv[1];
    Aws::String bucketName = argv[2];

    {
        Aws::S3::S3ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::getObject(objectName, bucketName, clientConfig);
    }

    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD
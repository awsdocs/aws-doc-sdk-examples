// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <iostream>
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
 * Demonstrates using the AWS SDK for C++ to put a string as an object in an S3 bucket.
  *
 */

//! Routine which demonstrates putting a string as an object in an S3 bucket.
/*!
  \param bucketName Name of the bucket.
  \param objectName Name for the object in the bucket.
  \param objectContent String as content for object.
  \param clientConfig Aws client configuration.
*/
// snippet-start:[s3.cpp.objects.put_string_into_object_bucket]
bool AwsDoc::S3::putObjectBuffer(const Aws::String &bucketName,
                                 const Aws::String &objectName,
                                 const std::string &objectContent,
                                 const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3Client(clientConfig);

    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(objectName);

    const std::shared_ptr<Aws::IOStream> inputData =
            Aws::MakeShared<Aws::StringStream>("");
    *inputData << objectContent.c_str();

    request.SetBody(inputData);

    Aws::S3::Model::PutObjectOutcome outcome = s3Client.PutObject(request);

    if (!outcome.IsSuccess()) {
        std::cerr << "Error: putObjectBuffer: " <<
                  outcome.GetError().GetMessage() << std::endl;
    } else {
        std::cout << "Success: Object '" << objectName << "' with content '"
                  << objectContent << "' uploaded to bucket '" << bucketName << "'.";
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.objects.put_string_into_object_bucket]

/**
 *
 * main function
 *
 *  Prerequisites: S3 bucket for the object.
 *
 * usage: run_put_object_buffer <object_name> <bucket_name>
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char* argv[])
{
    if (argc != 3)
    {
        std::cout << R"(
Usage:
    run_put_object_buffer <object_name> <bucket_name>
Where:
    object_name - The name for the uploaded buffer.
    bucket_name - The name of the bucket to upload the object to.
)" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String objectName = argv[1];
        const Aws::String bucketName = argv[2];
        const std::string objectContent = "This is my sample text content.";

        Aws::S3::S3ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::putObjectBuffer(bucketName, objectName, objectContent, clientConfig);
    }

    Aws::ShutdownAPI(options);

    return 0;
}

#endif  // TESTING_BUILD

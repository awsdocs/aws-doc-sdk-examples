/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <iostream>
#include <fstream>
#include <awsdoc/s3/s3_examples.h>

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to put a string as an object in an S3 bucket.
  *
 */

//! Routine which demonstrates putting a string as an object in an S3 bucket.
/*!
  \fn PutObject()
  \param bucketName Name of the bucket.
  \param objectName Name for the object in the bucket.
  \param objectContent String as content for object.
  \param clientConfig Aws client configuration.
*/

// snippet-start:[s3.cpp.objects.put_string_into_object_bucket]
bool AwsDoc::S3::PutObjectBuffer(const Aws::String &bucketName,
                                 const Aws::String &objectName,
                                 const std::string &objectContent,
                                 const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3_client(clientConfig);

    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(objectName);

    const std::shared_ptr<Aws::IOStream> inputData =
            Aws::MakeShared<Aws::StringStream>("");
    *inputData << objectContent.c_str();

    request.SetBody(inputData);

    Aws::S3::Model::PutObjectOutcome outcome = s3_client.PutObject(request);

    if (!outcome.IsSuccess()) {
        std::cerr << "Error: PutObjectBuffer: " <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    else {
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
* TODO(User) items: Set the following variables
* - bucketName: The name of the bucket.
*
*/

#ifndef TESTING_BUILD

int main() {
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // TODO(User): Change bucketName to the name of a bucket in your account.
        const Aws::String bucketName = "<Enter bucket Name>";
        const Aws::String objectName = "sample_text.txt";
        const std::string objectContent = "This is my sample text content.";

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::PutObjectBuffer(bucketName, objectName, objectContent, clientConfig);
    }

    Aws::ShutdownAPI(options);

    return 0;
}

#endif  // TESTING_BUILD

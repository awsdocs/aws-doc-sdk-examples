// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketWebsiteRequest.h>
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
* Demonstrates using the AWS SDK for C++ to delete the website configuration for an S3 bucket.
*
*/

//! Routine which demonstrates deleting the website configuration for an S3 bucket.
/*!
  \param bucketName Name of the bucket containing a website configuration.
  \param clientConfig Aws client configuration.
  \return bool: Function succeeded.
*/

// snippet-start:[s3.cpp.delete_website_config.code]
bool AwsDoc::S3::deleteBucketWebsite(const Aws::String &bucketName,
                                     const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::DeleteBucketWebsiteRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketWebsiteOutcome outcome =
            client.DeleteBucketWebsite(request);

    if (!outcome.IsSuccess()) {
        auto err = outcome.GetError();
        std::cerr << "Error: deleteBucketWebsite: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    } else {
        std::cout << "Website configuration was removed." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.delete_website_config.code]

/*
 *
 * main function
 *
 * usage: 'run_delete_bucket_website <bucket_name>'
 *
 * Prerequisites: The bucket containing the website configuration to
 * be removed.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char* argv[]) {
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    if (argc != 2) {
        std::cout << R"(
Usage:
    run_delete_bucket_website <bucket_name>
Where:
    bucket_name - The name of the bucket containing the website configuration to be removed.
)" << std::endl;
        return 1;
    }

    Aws::String bucketName = argv[1];

    {
        Aws::S3::S3ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::deleteBucketWebsite(bucketName, clientConfig);
    }

    ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD
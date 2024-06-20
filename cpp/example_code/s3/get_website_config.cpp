// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/GetBucketWebsiteRequest.h>
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
* Demonstrates using the AWS SDK for C++ to get the website configuration for an S3 bucket.
*
*/

//! Routine which demonstrates getting the website configuration for an S3 bucket.
/*!
  \param bucketName Name of to bucket containing a website configuration.
  \param clientConfig Aws client configuration.
  \return bool: Function succeeded.
*/
// snippet-start:[s3.cpp.get_website_config.code]
bool AwsDoc::S3::getWebsiteConfig(const Aws::String &bucketName,
                                  const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3Client(clientConfig);

    Aws::S3::Model::GetBucketWebsiteRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::GetBucketWebsiteOutcome outcome =
            s3Client.GetBucketWebsite(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();

        std::cerr << "Error: GetBucketWebsite: "
                  << err.GetMessage() << std::endl;
    } else {
        Aws::S3::Model::GetBucketWebsiteResult websiteResult = outcome.GetResult();

        std::cout << "Success: GetBucketWebsite: "
                  << std::endl << std::endl
                  << "For bucket '" << bucketName << "':"
                  << std::endl
                  << "Index page : "
                  << websiteResult.GetIndexDocument().GetSuffix()
                  << std::endl
                  << "Error page: "
                  << websiteResult.GetErrorDocument().GetKey()
                  << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.get_website_config.code]

/*
 *
 * main function
 *
 * Prerequisites: The bucket containing the website configuration.
 *
 * usage: run_get_website_config <bucket_name>
 *
 * Where:
 * - bucket_name: The name of the bucket containing the website configuration.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char* argv[])
{
    if (argc != 2)
    {
        std::cout << R"(
Usage:
    run_get_website_config <bucket_name>
Where:
    bucket_name - The name of the bucket that contains the website configuration.
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
        AwsDoc::S3::getWebsiteConfig(bucketName, clientConfig);
    }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD


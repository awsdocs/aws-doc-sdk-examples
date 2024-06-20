// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/IndexDocument.h>
#include <aws/s3/model/ErrorDocument.h>
#include <aws/s3/model/WebsiteConfiguration.h>
#include <aws/s3/model/PutBucketWebsiteRequest.h>
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
 * Demonstrates using the AWS SDK for C++ to configure a website for an S3 bucket.
 *
 */

//! Routine which demonstrates configuring a website for an S3 bucket.
/*!
  \param bucketName Name of S3 bucket.
  \param indexPage Name of index page.
  \param errorPage Name of error page.
  \param clientConfig Aws client configuration.
  \return bool: Function succeeded.
*/
// snippet-start:[s3.cpp.put_website_config.code]
bool AwsDoc::S3::putWebsiteConfig(const Aws::String &bucketName,
                                  const Aws::String &indexPage, const Aws::String &errorPage,
                                  const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);

    Aws::S3::Model::IndexDocument indexDocument;
    indexDocument.SetSuffix(indexPage);

    Aws::S3::Model::ErrorDocument errorDocument;
    errorDocument.SetKey(errorPage);

    Aws::S3::Model::WebsiteConfiguration websiteConfiguration;
    websiteConfiguration.SetIndexDocument(indexDocument);
    websiteConfiguration.SetErrorDocument(errorDocument);

    Aws::S3::Model::PutBucketWebsiteRequest request;
    request.SetBucket(bucketName);
    request.SetWebsiteConfiguration(websiteConfiguration);

    Aws::S3::Model::PutBucketWebsiteOutcome outcome =
            client.PutBucketWebsite(request);

    if (!outcome.IsSuccess()) {
        std::cerr << "Error: PutBucketWebsite: "
                  << outcome.GetError().GetMessage() << std::endl;
    } else {
        std::cout << "Success: Set website configuration for bucket '"
                  << bucketName << "'." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.put_website_config.code]

/*
 *
 *  main function
 *
 * Prerequisites: Create one S3 bucket that contains an index page and an error page.
 *
 * Usage:
 *   run_put_website_config <bucket_name> <index_page> <error_page>
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char* argv[])
{
    if (argc != 4)
    {
        std::cout << R"(
Usage:
    run_put_website_config <bucket_name> <index_page> <error_page>
Where:
    bucket_name - The name of the bucket to configure as a website.
    index_page - Upload file to bucket for the index page.
    error_page - Upload file to bucket for the error page.
)" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String bucket_name = argv[1];
        const Aws::String index_page = argv[2];
        const Aws::String error_page = argv[3];

        Aws::S3::S3ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::putWebsiteConfig(bucket_name, index_page, error_page, clientConfig);
    }

    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD

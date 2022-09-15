/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/IndexDocument.h>
#include <aws/s3/model/ErrorDocument.h>
#include <aws/s3/model/WebsiteConfiguration.h>
#include <aws/s3/model/PutBucketWebsiteRequest.h>
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
 * Demonstrates using the AWS SDK for C++ to configure a website for an S3 bucket.
 *
 */

//! Routine which demonstrates configuring a website for an S3 bucket.
/*!
  \sa PutWebsiteConfig()
  \param bucketName name of S3 bucket.
  \param indexPage name of index page.
  \param errorPage name of error page.
  \param clientConfig Aws client configuration.
*/

// snippet-start:[s3.cpp.put_website_config.code]
bool AwsDoc::S3::PutWebsiteConfig(const Aws::String &bucketName,
                                  const Aws::String &indexPage, const Aws::String &errorPage,
                                  const Aws::Client::ClientConfiguration &clientConfig) {
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
    }
    else {
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
 * Prerequisites: One S3 bucket containing an index page and an error page.
 *
 * TODO(User) items: Set the following variables
 * - bucketName: Change bucketName to the name of a bucket in your account.
 * - indexPage: Upload file to bucket for the index page.
 * - errorPage: Upload file to bucket for the error page.
 *
 */

#ifndef TESTING_BUILD

int main() {
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    //TODO(User): Change bucketName to the name of a bucket in your account.
    const Aws::String bucketName = "<Enter bucket name>";

    //TODO(User): Create these two files to serve as your website
    const Aws::String indexPage = "index.html";
    const Aws::String errorPage = "404.html";

    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::PutWebsiteConfig(bucketName, indexPage, errorPage, clientConfig);
    }

    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD

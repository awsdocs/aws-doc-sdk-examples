/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/GetBucketWebsiteRequest.h>
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
* Demonstrates using the AWS SDK for C++ to get the website configuration for an S3 bucket.
*
*/

//! Routine which demonstrates getting the website configuration for an S3 bucket.
/*!
  \sa GetWebsiteConfig()
  \param bucketName Name of to bucket containing a website configuration.
  \param clientConfig Aws client configuration.
*/

// snippet-start:[s3.cpp.get_website_config.code]
bool AwsDoc::S3::GetWebsiteConfig(const Aws::String &bucketName,
                                  const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3_client(clientConfig);

    Aws::S3::Model::GetBucketWebsiteRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::GetBucketWebsiteOutcome outcome =
            s3_client.GetBucketWebsite(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();

        std::cerr << "Error: GetBucketWebsite: "
                  << err.GetMessage() << std::endl;
    }
    else {
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
 * TODO(User) items: Set the following variables
 * - bucketName: The name of the bucket containing the website configuration.
 *
 */

#ifndef TESTING_BUILD

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        //TODO: Change bucket_name to the name of a bucket in your account.
        const Aws::String bucket_name = "<Enter bucket name>";

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::S3::GetWebsiteConfig(bucket_name, clientConfig);
    }
    Aws::ShutdownAPI(options);

    return 0;
}
#endif // TESTING_BUILD


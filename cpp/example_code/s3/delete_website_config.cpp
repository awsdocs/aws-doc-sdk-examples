/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketWebsiteRequest.h>
#include "awsdoc/s3/s3_examples.h"

/**
* Before running this C++ code example, set up your development environment, including your credentials.
*
* For more information, see the following documentation topic:
*
* https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html
*
* Purpose
*
* Demonstrates using the AWS SDK for C++ to delete the website configuration for an S3 bucket.
*
*/

//! Routine which demonstrates deleting the website configuration for an S3 bucket.
/*!
  \sa DeleteBucketWebsite()
  \param bucketName Name of the bucket containing a website configuration.
  \param clientConfig Aws client configuration.
*/

// snippet-start:[s3.cpp.delete_website_config.code]
bool AwsDoc::S3::DeleteBucketWebsite(const Aws::String &bucketName,
                                     const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::DeleteBucketWebsiteRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketWebsiteOutcome outcome =
            client.DeleteBucketWebsite(request);

    if (!outcome.IsSuccess()) {
        auto err = outcome.GetError();
        std::cerr << "Error: DeleteBucketWebsite: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }
    else {
        std::cout << "Website configuration was removed." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.delete_website_config.code]

/*
 *
 * main function
 *
 * Prerequisites: The bucket containing the website configuration to
 * be removed.
 *
 * TODO(user): items: Set the following variables
 * - bucketName: The name of the bucket containing the website configuration to
 *   be removed.
 *
 */

#ifndef TESTING_BUILD

int main() {
    //TODO(user): Change bucketName to the name of a bucket in your account.
    const Aws::String bucketName = "<Enter bucket name>";

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::DeleteBucketWebsite(bucketName, clientConfig);
    }

    ShutdownAPI(options);
}

#endif // TESTING_BUILD
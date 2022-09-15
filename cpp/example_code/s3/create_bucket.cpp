/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
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
 * Demonstrates using the AWS SDK for C++ to create an S3 bucket.
 *
 */

//! Routine which demonstrates creating an S3 bucket.
/*!
  \sa CreateBucket()
  \param bucketName Name of bucket to create.
  \param clientConfig Aws client configuration.
*/

// snippet-start:[s3.cpp.create_bucket.code]
bool AwsDoc::S3::CreateBucket(const Aws::String &bucketName,
                              const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::CreateBucketRequest request;
    request.SetBucket(bucketName);


    //TODO(user): Change the bucket location constraint enum to your target Region.
    Aws::S3::Model::CreateBucketConfiguration createBucketConfig;
    createBucketConfig.SetLocationConstraint(
            Aws::S3::Model::BucketLocationConstraintMapper::GetBucketLocationConstraintForName(
                    clientConfig.region));
    request.SetCreateBucketConfiguration(createBucketConfig);

    Aws::S3::Model::CreateBucketOutcome outcome = client.CreateBucket(request);
    if (!outcome.IsSuccess()) {
        auto err = outcome.GetError();
        std::cout << "Error: CreateBucket: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }
    else {
        std::cout << "Created bucket " << bucketName <<
                  " in the specified AWS Region." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.create_bucket.code]

/*
 *
 *  main function
 *
 */

#ifndef TESTING_BUILD

int main() {
    Aws::SDKOptions options;
    InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        // Create a unique bucket name to increase the chance of success
        // when trying to create the bucket.
        // Format: "doc-example-bucket-" + lowercase UUID.
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String bucketName = "doc-example-bucket-" +
                                 Aws::Utils::StringUtils::ToLower(uuid.c_str());

        AwsDoc::S3::CreateBucket(bucketName, clientConfig);
    }

    ShutdownAPI(options);
}

#endif // TESTING_BUILD


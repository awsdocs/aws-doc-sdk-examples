/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketRequest.h>
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
 * Demonstrates using the AWS SDK for C++ to delete an S3 bucket.
 *
 */

//! Routine which demonstrates deleting an S3 bucket.
/*!
  \sa DeleteBucket()
  \param bucketName Name of the bucket to delete.
  \param clientConfig Aws client configuration.
*/

// snippet-start:[s3.cpp.delete_bucket.code]
bool AwsDoc::S3::DeleteBucket(const Aws::String &bucketName,
                              const Aws::Client::ClientConfiguration &clientConfig) {

    Aws::S3::S3Client client(clientConfig);

    Aws::S3::Model::DeleteBucketRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketOutcome outcome =
            client.DeleteBucket(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "Error: DeleteBucket: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }
    else {
        std::cout << "The bucket was deleted" << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.delete_bucket.code]

/*
 *
 * main function
 *
 * Prerequisites: The bucket to be deleted.
 *
 * TODO(user): items: Set the following variable
 * - bucketName: The name of the bucket to delete.
 *
*/

#ifndef TESTING_BUILD
int main()
{
    //TODO(user): Change bucket_name to the name of a bucket in your account.
    //If the bucket is not in your account, you will get one of two errors:
    Aws::String bucketName = "<Enter Bucket Name>";

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::S3::DeleteBucket(bucketName, clientConfig);
    }

    ShutdownAPI(options);
 }
#endif // TESTING_BUILD


/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CopyObjectRequest.h>
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
 * Demonstrates using the AWS SDK for C++ to copy an object between 2 S3 buckets.
 *
 */

//! Routine which demonstrates copying an object between 2 S3 buckets.
/*!
  \sa CopyObject()
  \param objectKey Key of object in from bucket.
  \param fromBucket Name of from bucket.
  \param toBucket Name of to bucket.
  \param clientConfig Aws client configuration.
*/

// snippet-start:[s3.cpp.copy_objects.code]
bool AwsDoc::S3::CopyObject(const Aws::String &objectKey, const Aws::String &fromBucket, const Aws::String &toBucket,
                            const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::CopyObjectRequest request;

    request.WithCopySource(fromBucket + "/" + objectKey)
            .WithKey(objectKey)
            .WithBucket(toBucket);

    Aws::S3::Model::CopyObjectOutcome outcome = client.CopyObject(request);
    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "Error: CopyObject: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;

    }
    else {
        std::cout << "Successfully copied " << objectKey << " from " << fromBucket <<
                  " to " << toBucket << "." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.copy_objects.code]

/*
 *
 *  main function
 *
 * Prerequisites: Two buckets. One of the buckets must contain the object to
 * be copied to the other bucket.
 *
 * TODO(User) items: Set the following variables
 * - objectKey: The name of the object to copy.
 * - fromBucket: The name of the bucket to copy the object from.
 * - toBucket: The name of the bucket to copy the object to.
 *
 */

#ifndef TESTING_BUILD

int main() {
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    //TODO(user): Name of object already in bucket.
    Aws::String objectKey = "<enter object key>";

    //TODO(user): Change from_bucket to the name of your bucket that already contains "my-file.txt".
    Aws::String fromBucket = "<Enter bucket name>";

    //TODO(user): Change to the name of another bucket in your account.
    Aws::String toBucket = "<Enter bucket name>";

    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::CopyObject(objectKey, fromBucket, toBucket, clientConfig);
    }

    ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD


/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteObjectRequest.h>
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
 * Demonstrates using the AWS SDK for C++ to delete an object in an S3 bucket.
 *
 */

//! Routine which demonstrates deleting an object in an S3 bucket.
/*!
  \sa DeleteObject()
  \param objectKey Name of an object.
  \param fromBucket Name of a bucket with an object to delete.
  \param clientConfig Aws client configuration.
*/

// snippet-start:[s3.cpp.delete_object.code]
bool AwsDoc::S3::DeleteObject(const Aws::String &objectKey,
                              const Aws::String &fromBucket,
                              const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::DeleteObjectRequest request;

    request.WithKey(objectKey)
            .WithBucket(fromBucket);

    Aws::S3::Model::DeleteObjectOutcome outcome =
            client.DeleteObject(request);

    if (!outcome.IsSuccess()) {
        auto err = outcome.GetError();
        std::cerr << "Error: DeleteObject: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }
    else {
        std::cout << "Successfully deleted the object." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.delete_object.code]

/*
 *
 * main function
 *
 * Prerequisites: The bucket containing the object to delete.
 *
 * TODO(user): items: Set the following variable
 * - objectKey: The name of the object to delete.
 * - fromBucket: The name of the bucket to delete the object from.
 *
 */

#ifndef TESTING_BUILD

int main() {
    //TODO(user): The object_key is the unique identifier for the object in the bucket. In this example set,
    //it is the filename you added in put_object.cpp.
    Aws::String objectKey = "<Enter object key>";
    //TODO(user): Change from_bucket to the name of a bucket in your account.
    Aws::String fromBucket = "<Enter bucket name>";

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::S3::DeleteObject(objectKey, fromBucket, clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/GetObjectRequest.h>
#include <fstream>
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
 * Demonstrates using the AWS SDK for C++ to getting an object in an S3 bucket.
 *
 */

//! Routine which demonstrates getting an object in an S3 bucket.
/*!
  \sa GetObject()
  \param objectKey Name of an object in a bucket.
  \param toBucket: Name of a bucket.
  \param clientConfig: Aws client configuration.
*/

// snippet-start:[s3.cpp.get_object.code]
bool AwsDoc::S3::GetObject(const Aws::String &objectKey,
                           const Aws::String &fromBucket,
                           const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);

    Aws::S3::Model::GetObjectRequest request;
    request.SetBucket(fromBucket);
    request.SetKey(objectKey);

    Aws::S3::Model::GetObjectOutcome outcome =
            client.GetObject(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "Error: GetObject: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }
    else {
        std::cout << "Successfully retrieved '" << objectKey << "' from '"
                  << fromBucket << "'." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.get_object.code]

/*
 *
 * main function
 *
 * Prerequisites: The bucket with an object to be retrieved.
 *
 * TODO(user): items: Set the following variable
 * - bucketName: The name of the bucket.
 * - objectName: The name of an object in the bucket.
*
*/

#ifndef TESTING_BUILD

int main() {
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        //TODO(user): Change bucketName to the name of a bucket in your account.
        const Aws::String bucketName = "<Enter bucket name>";

        //TODO(user): Change objectName to the name of an object in the bucket.
        //See create_bucket.cpp and put_object.cpp to create a bucket and load an object into that bucket.
        const Aws::String objectName = "<Enter object name>";

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::GetObject(objectName, bucketName, clientConfig);
    }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD
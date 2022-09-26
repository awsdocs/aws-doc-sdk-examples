/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketPolicyRequest.h>
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
 * Demonstrates using the AWS SDK for C++ to delete the policy in an S3 bucket.
 *
 */

//! Routine which demonstrates deleting the policy in an S3 bucket.
/*!
  \sa DeleteBucketPolicy()
  \param toBucket Name of a bucket with the policy to delete.
  \param clientConfig Aws client configuration.
*/

// snippet-start:[s3.cpp.delete_bucket_policy.code]
bool AwsDoc::S3::DeleteBucketPolicy(const Aws::String &bucketName,
                                    const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);

    Aws::S3::Model::DeleteBucketPolicyRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketPolicyOutcome outcome = client.DeleteBucketPolicy(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "Error: DeleteBucketPolicy: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }
    else {
        std::cout << "Policy was deleted from the bucket." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.delete_bucket_policy.code]


/*
 *
 * main function
 *
 * Prerequisites: The bucket with a policy to be deleted.
 *
 * TODO(user): items: Set the following variable
 * - bucketName: The name of the bucket with a policy to delete.
 *
*/

#ifndef TESTING_BUILD

int main() {
    //TODO(user): Change bucket_name to the name of a bucket in your account.
    const Aws::String bucketName = "<Enter bucket name>";

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::S3::DeleteBucketPolicy(bucketName, clientConfig);
    }

    ShutdownAPI(options);
}

#endif // TESTING_BUILD
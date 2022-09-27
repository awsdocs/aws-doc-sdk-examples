/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/


#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/GetBucketPolicyRequest.h>
#include "awsdoc/s3/s3_examples.h"

/**
* Before running this C++ code example, set up your development environment, including your credentials.
*
* For more information, see the following documentation topic:
*
* https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
*
* Purpose
*
* Demonstrates using the AWS SDK for C++ to get the policy for an S3 bucket.
*
*/

//! Routine which demonstrates setting the ACL for an S3 bucket.
/*!
  \sa GetBucketPolicy()
  \param bucketName Name of a bucket.
  \param clientConfig Aws client configuration.
*/

// snippet-start:[s3.cpp.get_bucket_policy.code]
bool AwsDoc::S3::GetBucketPolicy(const Aws::String &bucketName,
                                 const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3_client(clientConfig);

    Aws::S3::Model::GetBucketPolicyRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::GetBucketPolicyOutcome outcome =
            s3_client.GetBucketPolicy(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "Error: GetBucketPolicy: "
                  << err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }
    else {
        Aws::StringStream policy_stream;
        Aws::String line;

        outcome.GetResult().GetPolicy() >> line;
        policy_stream << line;

        std::cout << "Retrieve the policy for bucket '" << bucketName << "':\n\n" <<
                  policy_stream.str() << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.get_bucket_policy.code]

/*
 *
 * main function
 * Prerequisites: Create an S3 bucket to get the bucket policy information about it.
 *
 * TODO(user): items: Set the following variables
 * - bucketName: The name of the bucket to get bucket policy information about.
 *
*/

#ifndef TESTING_BUILD

int main() {
    //TODO(user): Change bucket_name to the name of a bucket in your account.
    const Aws::String bucket_name = "<Enter bucket name>";

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::S3::GetBucketPolicy(bucket_name, clientConfig);
    }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <iostream>
#include <cstdio>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutBucketPolicyRequest.h>
#include <aws/sts/STSClient.h>
#include <aws/sts/model/GetCallerIdentityRequest.h>
#include "s3_examples.h"

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to set a policy on an S3 bucket.
 *
 */

static Aws::String getPolicyString(const Aws::String &userArn,
                                   const Aws::String &bucketName);


//! Routine which demonstrates setting a policy on an S3 bucket.
/*!
  \param bucketName: Name of a bucket.
  \param  policyBody: The bucket policy to add.
  \param clientConfig: Aws client configuration.
*/

// snippet-start:[s3.cpp.put_bucket_policy02.code]
bool AwsDoc::S3::putBucketPolicy(const Aws::String &bucketName,
                                 const Aws::String &policyBody,
                                 const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3Client(clientConfig);

    std::shared_ptr<Aws::StringStream> request_body =
            Aws::MakeShared<Aws::StringStream>("");
    *request_body << policyBody;

    Aws::S3::Model::PutBucketPolicyRequest request;
    request.SetBucket(bucketName);
    request.SetBody(request_body);

    Aws::S3::Model::PutBucketPolicyOutcome outcome =
            s3Client.PutBucketPolicy(request);

    if (!outcome.IsSuccess()) {
        std::cerr << "Error: putBucketPolicy: "
                  << outcome.GetError().GetMessage() << std::endl;
    } else {
        std::cout << "Set the following policy body for the bucket '" <<
                  bucketName << "':" << std::endl << std::endl;
        std::cout << policyBody << std::endl;
    }

    return outcome.IsSuccess();
}

// snippet-end:[s3.cpp.put_bucket_policy02.code]

// snippet-start:[s3.cpp.put_bucket_policy01.code]
//! Build a policy JSON string.
/*!
  \param userArn: Aws user Amazon Resource Name (ARN).
      For more information, see https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns.
  \param bucketName: Name of a bucket.
  \return String: Policy as JSON string.
*/

Aws::String getPolicyString(const Aws::String &userArn,
                            const Aws::String &bucketName) {
    return
            "{\n"
            "   \"Version\":\"2012-10-17\",\n"
            "   \"Statement\":[\n"
            "       {\n"
            "           \"Sid\": \"1\",\n"
            "           \"Effect\": \"Allow\",\n"
            "           \"Principal\": {\n"
            "               \"AWS\": \""
            + userArn +
            "\"\n""           },\n"
            "           \"Action\": [ \"s3:getObject\" ],\n"
            "           \"Resource\": [ \"arn:aws:s3:::"
            + bucketName +
            "/*\" ]\n"
            "       }\n"
            "   ]\n"
            "}";
}
// snippet-end:[s3.cpp.put_bucket_policy01.code]

/*
 *
 * main function
 *
 * Prerequisites: Create an S3 bucket to set a bucket policy on it
 *
 * usage: Usage: run_put_bucket_policy <bucket_name>
 * where:
 *   bucket_name   - the name of the bucket
 *
b*/

#ifndef TESTING_BUILD

int main(int argc, char* argv[])
{
    if (argc != 2)
    {
        std::cout << R"(
Usage:
    run_put_bucket_policy <bucket_name>
Where:
    bucket_name - The name of the bucket to put the policy.
)" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String bucketName = argv[1];

        Aws::String userArn;
        // Get the caller's AWS ARN to be used in the bucket policy.
        {
            Aws::STS::STSClientConfiguration stsClientConfiguration;
            // Optional: Set to the AWS Region (overrides config file).
            // stsClientConfiguration.region = "us-east-1"

            Aws::STS::STSClient sts_client(stsClientConfiguration);
            Aws::STS::Model::GetCallerIdentityRequest request;
            Aws::STS::Model::GetCallerIdentityOutcome outcome =
                    sts_client.GetCallerIdentity(request);

            if (!outcome.IsSuccess())
            {
                std::cout << "Error: getBucketPolicy setup: Get identity information: "
                          << outcome.GetError().GetMessage() << std::endl;
                return 1;
            }

            userArn = outcome.GetResult().GetArn();
        }

        // Use the account ID and bucket name to form the bucket policy to be added.
        Aws::String policy_string = getPolicyString(userArn, bucketName);

        Aws::S3::S3ClientConfiguration s3ClientConfiguration;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // s3ClientConfiguration.region = "us-east-1";

        AwsDoc::S3::putBucketPolicy(bucketName, policy_string, s3ClientConfiguration);
    }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD

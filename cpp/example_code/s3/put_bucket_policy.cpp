// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <iostream>
#include <cstdio>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutBucketPolicyRequest.h>
#include <aws/sts/STSClient.h>
#include <aws/sts/model/GetCallerIdentityRequest.h>
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
 * Demonstrates using the AWS SDK for C++ to set a policy on an S3 bucket.
 *
 */

static Aws::String GetPolicyString(const Aws::String &userArn,
                                   const Aws::String &bucketName);


//! Routine which demonstrates setting a policy on an S3 bucket.
/*!
  \sa PutBucketPolicy()
  \param bucketName: Name of a bucket.
  \param  policyBody: The bucket policy to add..
  \param clientConfig: Aws client configuration.
*/

// snippet-start:[s3.cpp.put_bucket_policy02.code]
bool AwsDoc::S3::PutBucketPolicy(const Aws::String &bucketName,
                                 const Aws::String &policyBody,
                                 const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3_client(clientConfig);

    std::shared_ptr<Aws::StringStream> request_body =
            Aws::MakeShared<Aws::StringStream>("");
    *request_body << policyBody;

    Aws::S3::Model::PutBucketPolicyRequest request;
    request.SetBucket(bucketName);
    request.SetBody(request_body);

    Aws::S3::Model::PutBucketPolicyOutcome outcome =
            s3_client.PutBucketPolicy(request);

    if (!outcome.IsSuccess()) {
        std::cerr << "Error: PutBucketPolicy: "
                  << outcome.GetError().GetMessage() << std::endl;
    }
    else {
        std::cout << "Set the following policy body for the bucket '" <<
                  bucketName << "':" << std::endl << std::endl;
        std::cout << policyBody << std::endl;
    }

    return outcome.IsSuccess();
}

//! Build a policy JSON string.
/*!
  \sa GetPolicyString()
  \param userArn Aws user ARN.
      See https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns.
  \param bucketName Name of a bucket.
*/

Aws::String GetPolicyString(const Aws::String &userArn,
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
            "           \"Action\": [ \"s3:GetObject\" ],\n"
            "           \"Resource\": [ \"arn:aws:s3:::"
            + bucketName +
            "/*\" ]\n"
            "       }\n"
            "   ]\n"
            "}";
}
// snippet-end:[s3.cpp.put_bucket_policy02.code]

/*
 *
 * main function
 * Prerequisites: The bucket to set a bucket policy.
 *
 * TODO(User) items: Set the following variables
 * - bucketName: The name of the bucket to put the bucket policy information in.
 *
*/

#ifndef TESTING_BUILD

int main() {
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        //TODO(User): Change bucket_name to the name of a bucket in your account.
        const Aws::String bucket_name = "<Enter bucket name>";

        Aws::String userArn;
        // Get the caller's AWS account ID to be used in the bucket policy.
        {
            Aws::Client::ClientConfiguration clientConfig;
            clientConfig.region = "us-east-1";  // ensure valid IAM region
            Aws::STS::STSClient sts_client(clientConfig);
            Aws::STS::Model::GetCallerIdentityRequest request;
            Aws::STS::Model::GetCallerIdentityOutcome outcome =
                    sts_client.GetCallerIdentity(request);

            if (!outcome.IsSuccess()) {
                std::cout << "Error: GetBucketPolicy setup: Get identity information: "
                          << outcome.GetError().GetMessage() << std::endl;

                return 1;
            }

            // Extract the caller's AWS account ID from the call to AWS STS.
            userArn = outcome.GetResult().GetArn();
        }

        // Use the account ID and bucket name to form the bucket policy to be added.
        Aws::String policy_string = GetPolicyString(userArn, bucket_name);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::PutBucketPolicy(bucket_name, policy_string, clientConfig);
    }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD

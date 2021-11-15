// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

//snippet-start:[s3.cpp.put_bucket_policy.inc]
#include <iostream>
#include <cstdio>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutBucketPolicyRequest.h>
#include <aws/sts/STSClient.h>
#include <aws/sts/model/GetCallerIdentityRequest.h>
#include <awsdoc/s3/s3_examples.h>
//snippet-end:[s3.cpp.put_bucket_policy.inc]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Gets a string representing a bucket policy. This particular string 
 * demonstrates allowing the s3:GetObject action by the specified account's 
 * root user for all objects in the target bucket. You can modify 
 * this function's signature and implementation to form other kinds of bucket 
 * policies, for example, by allowing various principals, actions, 
 * and resources.
 *
 * Prerequisites: The AWS account ID and Amazon S3 bucket name to be inserted
 * into the bucket policy.
 *
 * Inputs:
 * - accountID: The account ID to be inserted into the bucket policy.
 * - bucketName: The bucket name to be inserted into the bucket policy.
 *
 * Outputs: A string representing the bucket policy.
 * ///////////////////////////////////////////////////////////////////////// */

Aws::String GetPolicyString(const Aws::String& accountID,
    const Aws::String& bucketName)
{
    return
        // snippet-start:[s3.cpp.put_bucket_policy01.code]
        "{\n"
        "   \"Version\":\"2012-10-17\",\n"
        "   \"Statement\":[\n"
        "       {\n"
        "           \"Sid\": \"1\",\n"
        "           \"Effect\": \"Allow\",\n"
        "           \"Principal\": {\n"
        "               \"AWS\": \"arn:aws:iam::" + accountID + ":root\"\n"
        "           },\n"
        "           \"Action\": [ \"s3:GetObject\" ],\n"
        "           \"Resource\": [ \"arn:aws:s3:::" + bucketName + "/*\" ]\n"
        "       }\n"
        "   ]\n"
        "}";
        // snippet-end:[s3.cpp.put_bucket_policy01.code]
}

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Adds a bucket policy to a bucket in Amazon S3.
 *
 * Prerequisites: The bucket for the bucket policy to be added with 
 * the bucket policy to add.
 *
 * Inputs:
 * - bucketName: The name of the bucket to add the bucket policy to.
 * - policyBody: The bucket policy to add.
 * - region: The AWS Region of the bucket.
 *
 * Outputs: true if the bucket policy was added; otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[s3.cpp.put_bucket_policy02.code]
bool AwsDoc::S3::PutBucketPolicy(const Aws::String& bucketName, 
    const Aws::String& policyBody,
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;

    if (!region.empty())
    {
        config.region = region;
    }

    Aws::S3::S3Client s3_client(config);

    std::shared_ptr<Aws::StringStream> request_body = 
        Aws::MakeShared<Aws::StringStream>("");
    *request_body << policyBody;

    Aws::S3::Model::PutBucketPolicyRequest request;
    request.SetBucket(bucketName);
    request.SetBody(request_body);

    Aws::S3::Model::PutBucketPolicyOutcome outcome = 
        s3_client.PutBucketPolicy(request);

    if (outcome.IsSuccess()) {
        std::cout << "Set the following policy body for the bucket '" <<
            bucketName << "':" << std::endl << std::endl;
        std::cout << policyBody << std::endl;

        return true;
    }
    else {
        std::cout << "Error: PutBucketPolicy: "
            << outcome.GetError().GetMessage() << std::endl;

        return false;
    }
}

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        //TODO: Change bucket_name to the name of a bucket in your account.
        const Aws::String bucket_name = "DOC-EXAMPLE-BUCKET";
        //TODO: Set to the AWS Region in which the bucket was created.
        const Aws::String region = "us-east-1";


        // Get the caller's AWS account ID to be used in the bucket policy.
        Aws::STS::STSClient sts_client;
        Aws::STS::Model::GetCallerIdentityRequest request;
        Aws::STS::Model::GetCallerIdentityOutcome outcome =
            sts_client.GetCallerIdentity(request);

        if (!outcome.IsSuccess())
        {
            std::cout << "Error: GetBucketPolicy setup: Get identity information: " 
                << outcome.GetError().GetMessage() << std::endl;

            return 1;
        }

        // Extract the caller's AWS account ID from the call to AWS STS.
        Aws::String account_id = outcome.GetResult().GetAccount();

        // Use the account ID and bucket name to form the bucket policy to be added.
        Aws::String policy_string = GetPolicyString(account_id, bucket_name);

        if (!AwsDoc::S3::PutBucketPolicy(bucket_name, policy_string, region))
        {
            return 1;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3.cpp.put_bucket_policy02.code]

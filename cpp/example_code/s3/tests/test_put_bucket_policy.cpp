// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/core/utils/UUID.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/sts/STSClient.h>
#include <aws/sts/model/GetCallerIdentityRequest.h>
#include <aws/s3/model/GetBucketPolicyRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <awsdoc/s3/s3_examples.h>

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // 1/4. Create the Amazon S3 bucket.
        Aws::Client::ClientConfiguration config;
        config.region = "us-east-1";
        Aws::S3::S3Client s3_client(config);

        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String bucket_name = "my-bucket-" +
            Aws::Utils::StringUtils::ToLower(uuid.c_str());

        Aws::S3::Model::CreateBucketRequest create_bucket_request;
        create_bucket_request.SetBucket(bucket_name);

        Aws::S3::Model::CreateBucketOutcome create_bucket_outcome =
            s3_client.CreateBucket(create_bucket_request);

        if (!create_bucket_outcome.IsSuccess())
        {
            std::cout << "Error: PutBucketPolicy test setup: Create bucket '" <<
                bucket_name << "': " <<
                create_bucket_outcome.GetError().GetMessage() << std::endl;
            std::cout << "No cleanup needed." << std::endl;

            return 1;
        }

        // 2/2. Add the policy.
        // Get the caller's AWS account ID to be used in the bucket policy.
        Aws::STS::STSClient sts_client;
        Aws::STS::Model::GetCallerIdentityRequest get_caller_identity_request;
        Aws::STS::Model::GetCallerIdentityOutcome get_caller_identity_outcome =
            sts_client.GetCallerIdentity(get_caller_identity_request);

        if (!get_caller_identity_outcome.IsSuccess())
        {
            std::cout << "Error: PutBucketPolicy test setup: Get identity information: "
                << get_caller_identity_outcome.GetError().GetMessage() << std::endl;
            std::cout << "To clean up, you must delete the bucket '" << bucket_name << 
                "' yourself." << std::endl;

            return 1;
        }

        // Extract the caller's AWS account ID from the call to AWS STS.
        Aws::String account_id = get_caller_identity_outcome.GetResult().GetAccount();

        // Use the account ID and bucket name to form the bucket policy to be added.
        Aws::String policy_string = "{\n"
            "   \"Version\":\"2012-10-17\",\n"
            "   \"Statement\":[\n"
            "       {\n"
            "           \"Sid\": \"1\",\n"
            "           \"Effect\": \"Allow\",\n"
            "           \"Principal\": {\n"
            "               \"AWS\": \"arn:aws:iam::" + account_id + ":root\"\n"
            "           },\n"
            "           \"Action\": [ \"s3:GetObject\" ],\n"
            "           \"Resource\": [ \"arn:aws:s3:::" + bucket_name + "/*\" ]\n"
            "       }\n"
            "   ]\n"
            "}";

        if (!AwsDoc::S3::PutBucketPolicy(bucket_name, policy_string, config.region))
        {
            std::cout << "Error: PutBucketPolicy test." << std::endl;
            std::cout << "To clean up, you must delete the bucket '" << bucket_name <<
                "' yourself." << std::endl;

            return 1;
        }

        // 3/4. Get the policy.
        Aws::S3::Model::GetBucketPolicyRequest get_bucket_policy_request;
        get_bucket_policy_request.SetBucket(bucket_name);

        Aws::S3::Model::GetBucketPolicyOutcome get_bucket_policy_outcome =
            s3_client.GetBucketPolicy(get_bucket_policy_request);

        if (get_bucket_policy_outcome.IsSuccess())
        {
            Aws::StringStream policy_stream;
            Aws::String line;

            get_bucket_policy_outcome.GetResult().GetPolicy() >> line;
            policy_stream << line;

            std::cout << "Policy:" << std::endl << std::endl <<
                policy_stream.str() << std::endl;
        }
        else
        {
            std::cout << "Error: PutBucketPolicy test verification: "
                << get_bucket_policy_outcome.GetError().GetMessage() << std::endl;
            std::cout << "To clean up, you must delete the bucket '" << bucket_name <<
                "' yourself." << std::endl;

            return 1;
        }

        // 4/4. Delete the bucket.
        Aws::S3::Model::DeleteBucketRequest delete_bucket_request;
        delete_bucket_request.SetBucket(bucket_name);

        Aws::S3::Model::DeleteBucketOutcome delete_bucket_outcome =
            s3_client.DeleteBucket(delete_bucket_request);

        if (!delete_bucket_outcome.IsSuccess())
        {
            std::cout << "Error: ListObjects test cleanup: Delete bucket: " <<
                delete_bucket_outcome.GetError().GetMessage() << std::endl;
            std::cout << "To clean up, you must delete the bucket '" <<
                bucket_name << "' yourself." << std::endl;

            return 1;
        }

    }
    ShutdownAPI(options);

    return 0;
}
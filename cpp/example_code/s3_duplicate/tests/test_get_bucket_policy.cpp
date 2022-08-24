// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/sts/STSClient.h>
#include <aws/sts/model/GetCallerIdentityRequest.h>
#include <aws/s3/model/PutBucketPolicyRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <awsdoc/s3/s3_examples.h>

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration config;
        config.region = "us-east-1";

        Aws::S3::S3Client s3_client(config);

        // 1/4. Create the bucket to be deleted.
        // Create a unique bucket name to increase the chance of success 
        // when trying to create the bucket.
        // Format: "my-bucket-" + lowercase UUID.
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String bucket_name = "my-bucket-" +
            Aws::Utils::StringUtils::ToLower(uuid.c_str());

        Aws::S3::Model::CreateBucketRequest create_request;
        create_request.SetBucket(bucket_name);

        Aws::S3::Model::CreateBucketOutcome create_outcome =
            s3_client.CreateBucket(create_request);

        if (!create_outcome.IsSuccess())
        {
            auto err = create_outcome.GetError();
            std::cout << "Error: GetBucketPolicy test setup: Create bucket '" <<
                bucket_name << "': " <<
                err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            std::cout << "No cleanup needed." << std::endl;

            return 1;
        }

        // 2/4. Create and then add a bucket policy to the bucket.

        // Get the caller's AWS account ID to be used in the bucket policy.
        Aws::STS::STSClient sts_client;
        Aws::STS::Model::GetCallerIdentityRequest identity_request;
        Aws::STS::Model::GetCallerIdentityOutcome identity_outcome =
            sts_client.GetCallerIdentity(identity_request);

        if (!identity_outcome.IsSuccess())
        {
            auto err = identity_outcome.GetError();
            std::cout << "Error: GetBucketPolicy test setup: Get identity information: " <<
                err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            std::cout << "To clean up, you must delete the bucket '" << bucket_name <<
                "' yourself." << std::endl;

            return 1;
        }

        Aws::String account_id = identity_outcome.GetResult().GetAccount();

        // Define the bucket policy.
        std::shared_ptr<Aws::StringStream> bucket_policy = 
            Aws::MakeShared<Aws::StringStream>("");

        Aws::String policy_string =
            "{\n"
            "   \"Version\": \"2012-10-17\",\n"
            "   \"Id\": \"Policy" + uuid + "\",\n"
            "   \"Statement\": [\n"
            "       {\n"
            "           \"Sid\": \"Stmt" + uuid + "\",\n"
            "           \"Effect\" : \"Allow\",\n"
            "           \"Principal\": {\n"
            "               \"AWS\": \"arn:aws:iam::" + account_id + ":root\"\n"
            "           },\n"
            "           \"Action\": \"s3:GetObject\",\n"
            "           \"Resource\": \"arn:aws:s3:::" + bucket_name + "/*\"\n"
            "       }\n"
            "   ]\n"
            "}\n";

        *bucket_policy << policy_string;

        // Add the bucket policy to the bucket.
        Aws::S3::Model::PutBucketPolicyRequest put_policy_request;
        put_policy_request.SetBucket(bucket_name);
        put_policy_request.SetBody(bucket_policy);

        Aws::S3::Model::PutBucketPolicyOutcome put_policy_outcome =
            s3_client.PutBucketPolicy(put_policy_request);

        if (!put_policy_outcome.IsSuccess())
        {
            auto err = put_policy_outcome.GetError();
            std::cout << "Error: GetBucketPolicy test setup: Add policy to bucket: '" <<
                bucket_name << "': " <<
                err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            std::cout << "To clean up, you must delete the bucket '" <<
                bucket_name << "' yourself." << std::endl;

            return 1;
        }

        // 3/4. Get information about the bucket policy that was added.
        if (!AwsDoc::S3::GetBucketPolicy(bucket_name, config.region))
        {
            std::cout << "Error: GetBucketPolicy test. To clean up, you must delete the bucket '" <<
                bucket_name << "' yourself." << std::endl;

            return 1;
        }

        // 4/4. Delete the bucket.
        Aws::S3::Model::DeleteBucketRequest delete_request;
        delete_request.SetBucket(bucket_name);

        Aws::S3::Model::DeleteBucketOutcome delete_outcome =
            s3_client.DeleteBucket(delete_request);

        if (!delete_outcome.IsSuccess())
        {
            auto err = delete_outcome.GetError();
            std::cout << "Error: GetBucketPolicy test cleanup: Delete bucket: '" <<
                bucket_name << "': " <<
                err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            std::cout << "To clean up, you must delete the bucket '" <<
                bucket_name << "' yourself." << std::endl;

            return 1;
        }
    }
    ShutdownAPI(options);

    return 0;
}
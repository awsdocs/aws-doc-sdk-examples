// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/PutBucketPolicyRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <awsdoc/s3/s3_examples.h>
#include "test_utils.h"

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration config;
        config.region = "us-east-1";

        Aws::S3::S3Client s3_client(config);

        // 1/4. Create the bucket to add the bucket policy to.
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
            std::cout << "Error: DeleteBucketPolicy test setup: Create bucket '" <<
                bucket_name << "': " <<
                err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            std::cout << "No cleanup needed." << std::endl;

            return 1;
        }

        Aws::String user_arn = AwsTest::TestUtils::getArnForUser(config);
        if (user_arn.empty())
        {
            return 1;
        }

        // 2/4. Create the bucket policy, and then add the bucket policy 
        // to the bucket.
        Aws::String policy_string =
            "{\n"
            "  \"Version\":\"2012-10-17\",\n"
            "  \"Statement\":[\n"
            "   {\n"
            "     \"Sid\": \"1\",\n"
            "     \"Effect\": \"Allow\",\n"
            "     \"Principal\": {\n"
            "          \"AWS\": \"" + user_arn + "\"\n"
            "     },\n"
            "     \"Action\": [\"s3:GetObject\"],\n"
            "     \"Resource\": [\"arn:aws:s3:::" + bucket_name + "/*\"]\n"
            "   }]\n"
            "}";

        std::shared_ptr<Aws::StringStream> policy_body = 
            Aws::MakeShared<Aws::StringStream>("");

        *policy_body << policy_string;

        Aws::S3::Model::PutBucketPolicyRequest policy_request;
        policy_request.SetBucket(bucket_name);
        policy_request.SetBody(policy_body);

        Aws::S3::Model::PutBucketPolicyOutcome policy_outcome =
            s3_client.PutBucketPolicy(policy_request);

        if (!policy_outcome.IsSuccess())
        {
            auto err = create_outcome.GetError();
            std::cout << "Error: DeleteBucketPolicy test setup: Add bucket policy '" <<
                policy_string << "' to bucket '" << bucket_name << "': " <<
                err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            std::cout << "To clean up, you must delete the bucket '" <<
                bucket_name << "' yourself. " << std::endl;

            return 1;
        }

        // 3/4. Delete the bucket policy from the bucket.
        if (!AwsDoc::S3::DeleteBucketPolicy(bucket_name, config.region))
        {
            std::cout << "Error: DeleteBucketPolicy test: Delete bucket policy '" <<
                "' from bucket '" << bucket_name << "'. To clean up, you must delete " << 
                "the bucket '" << bucket_name << "' yourself. " << std::endl;

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
            std::cout << "Error: DeleteBucketPolicy test cleanup: Delete bucket: '" <<
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
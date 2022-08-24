// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/ListBucketsResult.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <awsdoc/s3/s3_examples.h>

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::S3::Model::BucketLocationConstraint region =
            Aws::S3::Model::BucketLocationConstraint::us_east_1;

        Aws::Client::ClientConfiguration config;
        config.region = "us-east-1";
        Aws::S3::S3Client s3_client(config);

        // 1/4. Create the bucket to upload the object to.
        // Create a unique bucket name to increase the chance of success 
        // when trying to create the bucket.
        // Format: "my-bucket-" + lowercase UUID.
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String bucket_name = "my-bucket-" +
            Aws::Utils::StringUtils::ToLower(uuid.c_str());

        Aws::S3::Model::CreateBucketRequest create_bucket_request;
        create_bucket_request.SetBucket(bucket_name);

        Aws::S3::Model::CreateBucketOutcome create_bucket_outcome =
            s3_client.CreateBucket(create_bucket_request);

        if (!create_bucket_outcome.IsSuccess())
        {
            auto err = create_bucket_outcome.GetError();
            std::cout << "Error: PutBucketAcl test setup: Create bucket '" <<
                bucket_name << "': " <<
                err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            std::cout << "No cleanup needed." << std::endl;

            return 1;
        }

        // 2/4. Get an owner ID and then use that ID to set the ACL.
        Aws::String owner_id;
        Aws::S3::Model::ListBucketsOutcome list_buckets_outcome =
            s3_client.ListBuckets();

        if (!list_buckets_outcome.IsSuccess())
        {
            auto err = list_buckets_outcome.GetError();
            std::cout << "Error: PutBucketAcl test setup: List buckets: " << 
                err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            std::cout << "To clean up, you must delete the bucket '" <<
                bucket_name << "' yourself." << std::endl;

            return 1;
        }
        else
        {
            owner_id = list_buckets_outcome.GetResult().GetOwner().GetID();
        }

        if (!AwsDoc::S3::PutBucketAcl(bucket_name,
                                      owner_id,
                                      "READ",
                                      "Canonical user",
                                      config.region,
                                      owner_id,
                                      "",
                                      "",
                                      ""))
        {
            std::cout << "Error: PutBucketAcl test: Set ACL for bucket '" <<
                bucket_name << "': To clean up, you must delete the bucket '" <<
                bucket_name << "' yourself." << std::endl;

            return 1;
        }

        // 3/4. Get information about the ACL that was just set.
        if (!AwsDoc::S3::GetBucketAcl(bucket_name, "us-east-1"))
        {
            std::cout << "Error: PutBucketAcl test: Get ACL for bucket '" <<
                bucket_name << "': To clean up, you must delete the bucket '" <<
                bucket_name << "' yourself." << std::endl;

            return 1;
        }

        // 4/4. Delete the bucket.
        Aws::S3::Model::DeleteBucketRequest delete_bucket_request;
        delete_bucket_request.SetBucket(bucket_name);

        Aws::S3::Model::DeleteBucketOutcome delete_bucket_outcome =
            s3_client.DeleteBucket(delete_bucket_request);

        if (!delete_bucket_outcome.IsSuccess())
        {
            auto err = delete_bucket_outcome.GetError();
            std::cout << "Error: PutBucketAcl test cleanup: Delete bucket: '" <<
                bucket_name << "':" <<
                err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            std::cout << "To clean up, you must delete the bucket '" <<
                bucket_name << "' yourself." << std::endl;

            return 1;
        }
    }
    ShutdownAPI(options);

    return 0;
}
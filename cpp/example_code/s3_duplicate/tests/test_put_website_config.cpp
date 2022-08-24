// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <aws/s3/S3Client.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/IndexDocument.h>
#include <aws/s3/model/ErrorDocument.h>
#include <aws/s3/model/WebsiteConfiguration.h>
#include <aws/s3/model/GetBucketWebsiteRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <awsdoc/s3/s3_examples.h>

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // 1/4. Create an Amazon S3 bucket.
        // Create a unique bucket name to increase the chance of success 
        // when trying to create the bucket.
        // Format: "my-bucket-" + lowercase UUID.
        Aws::S3::Model::BucketLocationConstraint region =
            Aws::S3::Model::BucketLocationConstraint::us_east_1;

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
            std::cout << "Error: PutWebsiteConfig test setup: Create bucket '" <<
                bucket_name << "': " <<
                create_bucket_outcome.GetError().GetMessage() << std::endl;
            std::cout << "No cleanup needed." << std::endl;

            return 1;
        }

        // 2/4. Configure the bucket as a static website.
        if (!AwsDoc::S3::PutWebsiteConfig(bucket_name, "index.html", "404.html", config.region))
        {
            std::cout << "Error: PutWebsiteConfig test: Put website config." << std::endl;
            std::cout << "To clean up, you must delete bucket '" <<
                bucket_name << "' yourself." << std::endl;

            return 1;
        }

        // 3/4. Get the static website configuration details for the bucket.
        Aws::S3::Model::GetBucketWebsiteRequest get_bucket_website_request;
        get_bucket_website_request.SetBucket(bucket_name);

        Aws::S3::Model::GetBucketWebsiteOutcome get_bucket_website_outcome =
            s3_client.GetBucketWebsite(get_bucket_website_request);

        if (!get_bucket_website_outcome.IsSuccess())
        {
            std::cout << "Error: PutWebsiteConfig test verification: Get website config: " <<
                get_bucket_website_outcome.GetError().GetMessage() << std::endl;
            std::cout << "To clean up, you must delete bucket '" <<
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
            std::cout << "Error: PutWebsiteConfig test cleanup: Delete bucket: '" <<
                bucket_name << "': " <<
                delete_bucket_outcome.GetError().GetMessage() << std::endl;
            std::cout << "To clean up, you must delete bucket '" <<
                bucket_name << "' yourself." << std::endl;

            return 1;
        }
    }
    ShutdownAPI(options);

    return 0;
}
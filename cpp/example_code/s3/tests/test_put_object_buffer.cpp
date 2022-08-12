// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <aws/s3/S3Client.h>
#include <aws/core/utils/UUID.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <fstream>
#include <aws/s3/model/DeleteObjectRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <awsdoc/s3/s3_examples.h>

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // 1/4. Create the Amazon S3 bucket.
        // Create a unique bucket name to increase the chance of success 
        // when trying to create the bucket.
        // Format: "my-bucket-" + lowercase UUID.
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
            std::cout << "Error: PutObjectBuffer test setup: Create bucket '" <<
                bucket_name << "': " <<
                create_bucket_outcome.GetError().GetMessage() << std::endl;
            std::cout << "No cleanup needed." << std::endl;

            return 1;
        }

        // 2/4. Add an object to the bucket.
        const char* file_name = "my-file.txt";
        std::ofstream myFile(file_name);
        myFile.close();

        if (!AwsDoc::S3::PutObjectBuffer(bucket_name, file_name, 
            "This is my sample text content.", config.region))
        {
            std::cout << "Error: PutObjectBuffer test. To clean up, you must "
                "delete the bucket '" << bucket_name <<
                "' yourself." << std::endl;

            return 1;
        }

        // 3/4. Delete the object from the bucket.
        Aws::S3::Model::DeleteObjectRequest delete_object_request;
        delete_object_request.SetBucket(bucket_name);
        delete_object_request.SetKey(file_name);

        Aws::S3::Model::DeleteObjectOutcome delete_object_outcome =
            s3_client.DeleteObject(delete_object_request);

        if (!delete_object_outcome.IsSuccess())
        {
            std::cout << "Error: PutObjectBuffer test cleanup: Delete object '" <<
                file_name << "' from bucket '" << bucket_name << "': " <<
                delete_object_outcome.GetError().GetMessage() << std::endl;
            std::cout << "To clean up, you must delete the bucket '" <<
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
            std::cout << "Error: PutObjectBuffer test cleanup: Delete bucket: " <<
                delete_bucket_outcome.GetError().GetMessage() << std::endl;
            std::cout << "To clean up, you must delete the bucket '" <<
                bucket_name << "' yourself." << std::endl;

            return 1;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}
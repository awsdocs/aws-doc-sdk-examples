// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <fstream>
#include <aws/s3/model/PutObjectRequest.h>
#include <aws/s3/model/DeleteObjectRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <awsdoc/s3/s3_examples.h>

bool CreateBucket(const Aws::S3::S3Client& s3Client, 
    const Aws::String& bucketName)
{
    Aws::S3::Model::CreateBucketRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::CreateBucketOutcome outcome =
        s3Client.CreateBucket(request);

    if (!outcome.IsSuccess())
    {
        auto err = outcome.GetError();
        std::cout << "Error: CopyObject test setup: Create bucket '" << 
            bucketName << "': " <<
            err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        std::cout << "The other bucket might have already been created. " 
            "To clean up, you need to delete the other bucket if it exists." << std::endl;

        return false;
    }

    return true;
}

bool DeleteObject(const Aws::S3::S3Client& s3Client,
    const Aws::String& bucketName,
    const Aws::String& objectKey)
{
    Aws::S3::Model::DeleteObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(objectKey);

    Aws::S3::Model::DeleteObjectOutcome outcome =
        s3Client.DeleteObject(request);

    if (!outcome.IsSuccess())
    {
        auto err = outcome.GetError();
        std::cout << "Error: CreateBucket test cleanup: Delete object '" <<
            objectKey << "' from bucket '" << bucketName << "': " << 
            err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        std::cout << "To clean up, you must delete the bucket '" <<
            bucketName << "' and the other bucket yourself." << std::endl;

        return false;
    }

    return true;
}

bool DeleteBucket(const Aws::S3::S3Client& s3Client,
    const Aws::String& bucketName)
{
    Aws::S3::Model::DeleteBucketRequest request;

    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketOutcome outcome =
        s3Client.DeleteBucket(request);

    if (!outcome.IsSuccess())
    {
        auto err = outcome.GetError();
        std::cout << "Error: CreateBucket test cleanup: Delete bucket: '" <<
            bucketName << "':" <<
            err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        std::cout << "To clean up, you must delete the bucket '" <<
            bucketName << "', and the other bucket if it still exists, yourself." << std::endl;

        return false;
    }

    return true;
}

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String region = "us-east-1";

        Aws::Client::ClientConfiguration config;
        config.region = region;
        Aws::S3::S3Client s3_client(config);

        const char* file_name = "my-file.txt";

        // 1/8. Create the bucket to copy the object from.
        // Create a unique bucket name to increase the chance of success 
        // when trying to create the bucket.
        // Format: "my-from-bucket-" + lowercase UUID.
        Aws::String from_uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String from_bucket_name = "my-from-bucket-" +
            Aws::Utils::StringUtils::ToLower(from_uuid.c_str());
        
        if (!CreateBucket(s3_client, from_bucket_name))
        {
            return 1;
        }

        // 2/8. Create the object to upload, and then upload the object 
        // to the 'from' bucket. 
        // For this test, create a text file named 'my-file.txt' in the same
        // directory as this test.
        std::ofstream myFile(file_name);
        myFile << "My content.";
        myFile.close();
        
        Aws::S3::Model::PutObjectRequest put_object_request;
        put_object_request.SetBucket(from_bucket_name);
        put_object_request.SetKey(file_name);

        std::shared_ptr<Aws::IOStream> file_body =
            Aws::MakeShared<Aws::FStream>("SampleAllocationTag", file_name,
                std::ios_base::in | std::ios_base::binary);

        put_object_request.SetBody(file_body);

        Aws::S3::Model::PutObjectOutcome put_object_outcome =
            s3_client.PutObject(put_object_request);

        if (!put_object_outcome.IsSuccess())
        {
            auto err = put_object_outcome.GetError();
            std::cout << "Error: CopyObject test setup: Upload object '" << file_name << "' " << 
                "to bucket '" << from_bucket_name << "': " <<
                err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            std::cout << "To clean up, you must delete the bucket '" <<
                from_bucket_name << "' yourself." << std::endl;

            return 1;
        }

        // 3/8. Create the bucket to copy the object to.
        // Create a unique bucket name to increase the chance of success 
        // when trying to create the bucket.
        // Format: "my-to-bucket-" + lowercase UUID.
        Aws::String to_uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String to_bucket_name = "my-to-bucket-" +
            Aws::Utils::StringUtils::ToLower(to_uuid.c_str());

        if (!CreateBucket(s3_client, to_bucket_name))
        {
            return 1;
        }

        // 4/8. Copy the object from the 'from' bucket to the 'to' bucket.
        if (!AwsDoc::S3::CopyObject(Aws::String(file_name), from_bucket_name,
            to_bucket_name, config.region))
        {
            std::cout << "Error: CopyObject test: Copy object '" << file_name << "' from bucket '" << 
                from_bucket_name << "' to bucket '" << to_bucket_name << "'. To clean up, "
                "you must delete the buckets '" <<
                from_bucket_name << "' and '" << 
                to_bucket_name << "' yourself." << std::endl;

            return 1;
        }

        // 5/8. Delete the object from the 'from' bucket.
        if (!DeleteObject(s3_client, from_bucket_name, Aws::String(file_name)))
        {
            return 1;
        }

        // 6/8. Delete the 'from' bucket.
        if (!DeleteBucket(s3_client, from_bucket_name))
        {
            return 1;
        }

        // 7/8. Delete the object from the 'to' bucket.
        if (!DeleteObject(s3_client, to_bucket_name, Aws::String(file_name)))
        {
            return 1;
        }

        // 8/8. Delete the 'to' bucket.
        if (!DeleteBucket(s3_client, to_bucket_name))
        {
            return 1;
        }
    }

    std::cout << "The object was copied." << std::endl;

    ShutdownAPI(options);

    return 0;
}
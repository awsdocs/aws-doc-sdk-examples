// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <aws/s3/S3Client.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <awsdoc/s3/s3_examples.h>

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String region = "us-east-1";
        Aws::Client::ClientConfiguration config;
        config.region = region;
        Aws::S3::S3Client s3_client(config);

        // 1/2. Create the bucket to be deleted.
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
            std::cout << "Error: DeleteBucket test setup: Create bucket '" << 
                bucket_name << "': " <<
                err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            std::cout << "No cleanup needed." << std::endl;

            return 1;
        }

        // 2/2. Delete the bucket.
        if (!AwsDoc::S3::DeleteBucket(bucket_name, region))
        {
            std::cout << "Error: DeleteBucket test cleanup: Delete bucket '" << 
                bucket_name << "': To clean up, you "
                " must delete the bucket '" << bucket_name << 
                "' yourself." << std::endl;

            return 1;
        }
    }
    ShutdownAPI(options);

    return 0;
}
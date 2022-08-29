// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <awsdoc/s3/s3_examples.h>

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String region ="us-east-1";

        // Create a unique bucket name to increase the chance of success 
        // when trying to create the bucket.
        // Format: "my-bucket-" + lowercase UUID.
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String bucket_name = "my-bucket-" +
            Aws::Utils::StringUtils::ToLower(uuid.c_str());

        // Create the bucket.
        if (!AwsDoc::S3::CreateBucket(bucket_name, region))
        {
            return 1;
        }

        // Delete the bucket, leaving the AWS account in its previous state.
        Aws::Client::ClientConfiguration config;
        config.region = region;

        Aws::S3::S3Client s3_client(config);
        Aws::S3::Model::DeleteBucketRequest request;
        request.SetBucket(bucket_name);

        Aws::S3::Model::DeleteBucketOutcome outcome = 
            s3_client.DeleteBucket(request);
        
        if (!outcome.IsSuccess())
        {
            auto err = outcome.GetError();
            std::cout << "Error: CreateBucket test cleanup: Delete bucket: " <<
                err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            std::cout << "To clean up, you must delete the bucket '" <<
                bucket_name << "' yourself." << std::endl;

            return 1;
        }

    }
    Aws::ShutdownAPI(options);

	return 0;
}

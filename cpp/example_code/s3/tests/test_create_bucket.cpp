// Copyright Amazon.com, Inc. or its affiliates.All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

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
        // Create a unique bucket name to increase the chance of success 
        // when trying to create the bucket.
        // Format: "my-bucket-" + lowercase UUID.
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String bucket_name = "my-bucket-" +
            Aws::Utils::StringUtils::ToLower(uuid.c_str());

        // Create the bucket.
        if (!AwsDoc::S3::CreateBucket(bucket_name, 
            AwsDoc::S3::TestConstants::S3_REGION))
        {
            return 1;
        }

        // Delete the bucket, leaving the AWS account in its previous state.
        Aws::S3::S3Client s3_client;
        Aws::S3::Model::DeleteBucketRequest request;
        request.SetBucket(bucket_name);

        Aws::S3::Model::DeleteBucketOutcome outcome = 
            s3_client.DeleteBucket(request);
        
        if (!outcome.IsSuccess())
        {
            // If the bucket cannot be deleted, notify the caller that they 
            // will need to delete the bucket themselves.
            std::cout << "Cannot delete bucket " << bucket_name
                << ". You will need to delete this bucket yourself.";

            return 1;
        }

    }
    Aws::ShutdownAPI(options);

	return 0;
}

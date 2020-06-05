// Copyright Amazon.com, Inc. or its affiliates.All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <aws/core/Aws.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <awsdoc/s3/s3_examples.h>

int main()
{
    Aws::String bucket_name = "my-bucket";
    Aws::S3::Model::BucketLocationConstraint region = 
        Aws::S3::Model::BucketLocationConstraint::us_east_1;

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (!AwsDoc::S3::CreateBucket(bucket_name, region))
        {
            return 1;
        }
    }
    Aws::ShutdownAPI(options);

	return 0;
}
// Copyright Amazon.com, Inc. or its affiliates.All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

// snippet-start:[s3.cpp.create_bucket.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/BucketLocationConstraint.h>
// snippet-end:[s3.cpp.create_bucket.inc]
#include <awsdoc/s3/s3_examples.h>

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Creates a bucket in Amazon S3.
 *
 * Inputs:
 * - bucketName: The name of the bucket to create. 
 * - region: The AWS Region to create the bucket in.
 * 
 * Outputs: true if the bucket was created; otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

 // snippet-start:[s3.cpp.create_bucket.code]
bool AwsDoc::S3::CreateBucket(Aws::String bucketName, 
        Aws::S3::Model::BucketLocationConstraint region)
{
	Aws::S3::S3Client s3_client;

    Aws::S3::Model::CreateBucketRequest request;
    request.SetBucket(bucketName);

    // You only need to set the AWS Region for the bucket if it is 
    // other than US East (N. Virginia) us-east-1.
    if (region != Aws::S3::Model::BucketLocationConstraint::us_east_1)
    {
        Aws::S3::Model::CreateBucketConfiguration bucket_config;
        bucket_config.SetLocationConstraint(region);

        request.SetCreateBucketConfiguration(bucket_config);
    }

    Aws::S3::Model::CreateBucketOutcome outcome = 
        s3_client.CreateBucket(request);

    if (!outcome.IsSuccess())
    {
        auto err = outcome.GetError();
        std::cout << "Error: CreateBucket: " <<
            err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        return false;
    }

    return true;
}
// snippet-end:[s3.cpp.create_bucket.code]

int main()
{	
    Aws::String bucket_name = "my-bucket";
    Aws::S3::Model::BucketLocationConstraint region = 
        Aws::S3::Model::BucketLocationConstraint::us_east_1;

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (AwsDoc::S3::CreateBucket(bucket_name, region))
        {
            std::cout << "Created bucket " << bucket_name <<
                " in the specified AWS Region." << std::endl;
        }
    }
    ShutdownAPI(options);

	return 0;
}

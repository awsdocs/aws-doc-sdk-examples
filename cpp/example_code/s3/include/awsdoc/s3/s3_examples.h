// Copyright Amazon.com, Inc. or its affiliates.All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#pragma once

#include <aws/core/Aws.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <awsdoc/s3/S3_EXPORTS.h>

namespace AwsDoc
{
    namespace S3
    {
        // Need to fix up implementation for this.
        AWSDOC_S3_API bool CopyObject(const Aws::String &objectKey, 
            const Aws::String &fromBucket, const Aws::String &toBucket);

        AWSDOC_S3_API bool CreateBucket(const Aws::String &bucketName, 
            const Aws::S3::Model::BucketLocationConstraint &region);
        
        // Need to fix up implementation for these.
        AWSDOC_S3_API bool DeleteBucket(const Aws::String &bucketName,
            const Aws::String &region);
        AWSDOC_S3_API bool DeleteBucketPolicy(const Aws::String &bucketName,
            const Aws::String &region);
        AWSDOC_S3_API bool DeleteObject(const Aws::String &objectKey,
            const Aws::String &fromBucket);
        AWSDOC_S3_API bool DeleteBucketWebsite(const Aws::String &bucketName,
            const Aws::String &region);
        AWSDOC_S3_API bool GetBucketAcl(const Aws::String &bucketName,
            const Aws::String &region);
        AWSDOC_S3_API bool GetBucketPolicy(const Aws::String &bucketName,
            const Aws::String &region);
        
        // Need to create implementations for these.
        AWSDOC_S3_API bool GetObject(const Aws::String &objectKey,
            const Aws::String& fromBucket);
        AWSDOC_S3_API bool PutBucketAcl(const Aws::String &bucketName, 
            const Aws::String &granteeId, const Aws::String &permission);
        AWSDOC_S3_API bool PutObjectAcl(const Aws::String& bucketName,
            const Aws::String &objectKey, const Aws::String &granteeId, 
            const Aws::String &permission);

        // Need to fix up implementation for this.
        AWSDOC_S3_API bool GetObjectAcl(const Aws::String &bucketName,
            const Aws::String &objectKey, const Aws::String &region);

        // Constants used for testing the code examples.
        namespace TestConstants
        {
            // Replace these with randomly-created buckets and an already-uploaded object.
            Aws::String COPY_FROM_BUCKET = "my-bucket-1";            // test_copy_object.cpp
            Aws::String COPY_OBJECT_KEY = "my-key";                  // test_copy_object.cpp
            Aws::String COPY_TO_BUCKET = "my-bucket-2";              // test_copy_object.cpp

            // Replace this with a randomly-created bucket with an already-existing policy and an already-uploaded object.
            Aws::String DELETE_BUCKET_NAME = "my-bucket";            // test_delete_bucket.cpp
            Aws::String DELETE_BUCKET_POLICY_NAME = "my-bucket";     // test_delete_bucket_policy.cpp
            Aws::String DELETE_FROM_BUCKET = "my-bucket";            // test_delete_object.cpp
            Aws::String DELETE_OBJECT_KEY = "my-key";                // test_delete_object.cpp
 
            // Replace this with a randomly-created bucket already enabled as a bucket website.
            Aws::String DELETE_BUCKET_WEBSITE_NAME = "my-bucket";    // test_delete_website_config.cpp

            // Replace this with a randomly-created bucket already and an already-uploaded object, both with an already-existing ACL.
            Aws::String ACL_BUCKET_NAME = "my-bucket";               // get_acl.cpp
            Aws::String ACL_OBJECT_KEY = "my-file.txt";              // get_acl.cpp

            Aws::String AWS_REGION = "us-east-1";                    // Used by various tests.
            Aws::S3::Model::BucketLocationConstraint S3_REGION =
                Aws::S3::Model::BucketLocationConstraint::us_east_1; // Used by test_create_bucket.cpp.
        }
    }
}

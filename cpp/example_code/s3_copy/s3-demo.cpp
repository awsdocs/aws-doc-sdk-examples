//snippet-sourcedescription:[s3-demo.cpp demonstrates how to perform various operations for Amazon Simple Storage Service (Amazon S3).]
//snippet-keyword:[AWS SDK for C++]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[12/15/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <awsdoc/s3/s3-demo.h>
// snippet-start:[s3.cpp.bucket_operations.list_create_delete]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/Bucket.h>
#include <aws/s3/model/CreateBucketConfiguration.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>

// Look for a bucket among all currently available Amazon S3 buckets.
bool FindTheBucket(const Aws::S3::S3Client& s3Client,
    const Aws::String& bucketName) {

    Aws::S3::Model::ListBucketsOutcome outcome = s3Client.ListBuckets();

    if (outcome.IsSuccess()) {

        std::cout << "Looking for a bucket named '" << bucketName << "'..." 
            << std::endl << std::endl;

        Aws::Vector<Aws::S3::Model::Bucket> bucket_list =
            outcome.GetResult().GetBuckets();

        for (Aws::S3::Model::Bucket const& bucket : bucket_list) 
        {
            if (bucket.GetName() == bucketName)
            {
                std::cout << "Found the bucket." << std::endl << std::endl; 

                return true;
            }
        }

        std::cout << "Could not find the bucket." << std::endl << std::endl;

        return true;
    }
    else {
        std::cout << "ListBuckets error: "
            << outcome.GetError().GetMessage() << std::endl;
    }

    return false;
}

// Create an Amazon S3 bucket.
bool CreateTheBucket(const Aws::S3::S3Client& s3Client, 
    const Aws::String& bucketName) {

    std::cout << "Creating a bucket named '"
        << bucketName << "'..." << std::endl << std::endl;

    Aws::S3::Model::CreateBucketRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::CreateBucketOutcome outcome = 
        s3Client.CreateBucket(request);

    if (outcome.IsSuccess()) {
        std::cout << "Bucket created." << std::endl << std::endl;

        return true;
    }
    else {
        std::cout << "CreateBucket error: "
            << outcome.GetError().GetMessage() << std::endl;

        return false;
    }
}

// Delete an existing Amazon S3 bucket.
bool DeleteTheBucket(const Aws::S3::S3Client& s3Client, 
    const Aws::String& bucketName) {

    std::cout << "Deleting the bucket named '"
        << bucketName << "'..." << std::endl << std::endl;

    Aws::S3::Model::DeleteBucketRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketOutcome outcome = 
        s3Client.DeleteBucket(request);

    if (outcome.IsSuccess()) {
        std::cout << "Bucket deleted." << std::endl << std::endl;

        return true;
    }
    else {
        std::cout << "DeleteBucket error: "
            << outcome.GetError().GetMessage() << std::endl;

        return false;
    }
}

// Create an Amazon S3 bucket and then delete it. 
// Before and after creating the bucket, and then after deleting the bucket, 
// try to determine whether that bucket still exists. 
int main(int argc, char* argv[]) {

    if (argc < 3) {
        std::cout << "Usage: s3-demo <bucket name> <AWS Region>" << std::endl
            << "Example: s3-demo my-bucket us-east-1" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String bucket_name = argv[1];
        Aws::String region = argv[2];

        Aws::Client::ClientConfiguration config;

        config.region = region;

        Aws::S3::S3Client s3_client(config);

        if (!FindTheBucket(s3_client, bucket_name)) {
            return 1;
        }

        if (!CreateTheBucket(s3_client, bucket_name)) {
            return 1;
        }

        if (!FindTheBucket(s3_client, bucket_name)) {
            return 1;
        }

        if (!DeleteTheBucket(s3_client, bucket_name)) {
            return 1;
        }

        if (!FindTheBucket(s3_client, bucket_name)) {
            return 1;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3.cpp.bucket_operations.list_create_delete]

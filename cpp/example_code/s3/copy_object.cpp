// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CopyObjectRequest.h>
#include <awsdoc/s3/s3_examples.h>

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Copies an object from one bucket in Amazon S3 to another bucket.
 * 
 * Prerequisites: Two buckets. One of the buckets must contain the object to 
 * be copied to the other bucket.
 *
 * Inputs:
 * - objectKey: The name of the object to copy.
 * - fromBucket: The name of the bucket to copy the object from.
 * - toBucket: The name of the bucket to copy the object to.
 * - region: The AWS Region to create the bucket in.
 *
 * Outputs: true if the object was copied; otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

bool AwsDoc::S3::CopyObject(const Aws::String& objectKey, 
    const Aws::String& fromBucket, const Aws::String& toBucket, const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;

    if (!region.empty())
    {
        config.region = region;
    }

    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::CopyObjectRequest request;

    request.WithCopySource(fromBucket + "/" + objectKey)
        .WithKey(objectKey)
        .WithBucket(toBucket);
    
    Aws::S3::Model::CopyObjectOutcome outcome = s3_client.CopyObject(request);

    if (!outcome.IsSuccess())
    {
        auto err = outcome.GetError();
        std::cout << "Error: CopyObject: " <<
            err.GetExceptionName() << ": " << err.GetMessage() << std::endl;

        return false;
    }
    else
    {
        return true;
    }
}

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        //TODO: Name of object already in bucket.
        Aws::String object_key = "my-file.txt";
        //TODO: Change from_bucket to the name of your bucket that already contains "my-file.txt". 
        //See create_bucket.cpp and put_object.cpp to create a bucket and load an object into that bucket.
        Aws::String from_bucket = "MY-FROM-BUCKET";
        //TODO: Change to the name of another bucket in your account.
        Aws::String to_bucket = "MY-TO-BUCKET";
        //TODO: Set to the AWS Region in which the bucket was created.
        Aws::String region = "us-east-1";

        if (AwsDoc::S3::CopyObject(object_key, from_bucket, to_bucket, region))
        {
            std::cout << "Copied object '" << object_key <<
                "' from '" << from_bucket << "' to '" << to_bucket << "'." << 
                std::endl;
        }
        else
        {
            return 1;
        }
    }
    ShutdownAPI(options);

    return 0;
}
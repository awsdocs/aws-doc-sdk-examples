// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

//snippet-start:[s3.cpp.put_object.inc]
#include <iostream>
#include <fstream>
#include <sys/stat.h>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <awsdoc/s3/s3_examples.h>
//snippet-end:[s3.cpp.put_object.inc]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Adds an object to a bucket in Amazon S3 bucket.
 *
 * Prerequisites: An Amazon S3 bucket and the object to be added.
 *
 * Inputs:
 * - bucketName: The name of the bucket.
 * - objectName: The name of the object.
 * - region: The AWS Region for the bucket.
 *
 * Outputs: true if the object was added to the bucket; otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[s3.cpp.put_object.code]
bool AwsDoc::S3::PutObject(const Aws::String& bucketName, 
    const Aws::String& objectName,
    const Aws::String& region)
{
    // Verify that the file exists.
    struct stat buffer;

    if (stat(objectName.c_str(), &buffer) == -1)
    {
        std::cout << "Error: PutObject: File '" <<
            objectName << "' does not exist." << std::endl;

        return false;
    }

    Aws::Client::ClientConfiguration config;

    if (!region.empty())
    {
        config.region = region;
    }

    Aws::S3::S3Client s3_client(config);
    
    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(objectName);

    std::shared_ptr<Aws::IOStream> input_data = 
        Aws::MakeShared<Aws::FStream>("SampleAllocationTag", 
            objectName.c_str(), 
            std::ios_base::in | std::ios_base::binary);

    request.SetBody(input_data);

    Aws::S3::Model::PutObjectOutcome outcome = 
        s3_client.PutObject(request);

    if (outcome.IsSuccess()) {

        std::cout << "Added object '" << objectName << "' to bucket '"
            << bucketName << "'.";
        return true;
    }
    else 
    {
        std::cout << "Error: PutObject: " << 
            outcome.GetError().GetMessage() << std::endl;
       
        return false;
    }
}

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String bucket_name = "my-bucket";
        const Aws::String object_name = "my-file.txt";
        const Aws::String region = "us-east-1";

        if (!AwsDoc::S3::PutObject(bucket_name, object_name, region)) {
            
            return 1;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3.cpp.put_object.code]

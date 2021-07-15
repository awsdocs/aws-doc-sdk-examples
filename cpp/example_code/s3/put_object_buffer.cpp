// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Adds content to an object and then adds the object to a bucket 
 * in Amazon S3.
 *
 * Prerequisites: An Amazon S3 bucket and content to be added to the bucket 
 * as an object.
 *
 * Inputs:
 * - bucketName: The name of the bucket.
 * - objectName: The name of the object to be added to the bucket.
 * - objectContent: The content to be added to the object.
 * - region: The AWS Region for the bucket.
 *
 * Outputs: true if the object was added to the bucket; otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[s3.cpp.objects.put_string_into_object_bucket]
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <iostream>
#include <fstream>
#include <awsdoc/s3/s3_examples.h>

bool AwsDoc::S3::PutObjectBuffer(const Aws::String& bucketName,
    const Aws::String& objectName,
    const std::string& objectContent,
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    
    if (!region.empty())
    {
        config.region = region;
    }

    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(objectName);

    const std::shared_ptr<Aws::IOStream> input_data =
        Aws::MakeShared<Aws::StringStream>("");
    *input_data << objectContent.c_str();

    request.SetBody(input_data);

    Aws::S3::Model::PutObjectOutcome outcome = s3_client.PutObject(request);

    if (!outcome.IsSuccess()) {
        std::cout << "Error: PutObjectBuffer: " << 
            outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: Object '" << objectName << "' with content '"
            << objectContent << "' uploaded to bucket '" << bucketName << "'.";

        return true;
    }
}

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // TODO: Change bucket_name to the name of a bucket in your account.
        const Aws::String bucket_name = "DOC-EXAMPLE-BUCKET";
        const Aws::String object_name = "my-file.txt";
        const std::string object_content = "This is my sample text content.";
        //TODO: Set to the AWS Region in which the bucket was created.
        const Aws::String region = "us-east-1";

        if (!AwsDoc::S3::PutObjectBuffer(bucket_name, object_name, object_content, region)) 
        {
            return 1;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3.cpp.objects.put_string_into_object_bucket]

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

//snippet-start:[s3.cpp.get_object.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/GetObjectRequest.h>
#include <fstream>
#include <awsdoc/s3/s3_examples.h>
//snippet-end:[s3.cpp.get_object.inc]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Prints the beginning contents of a text file in a 
 * bucket in Amazon S3.
 *
 * Prerequisites: The bucket that contains the text file.
 *
 * Inputs:
 * - objectKey: The name of the text file.
 * - fromBucket: The name of the bucket that contains the text file.
 *
 * Outputs: true if the contents of the text file were retrieved; 
 * otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

 // snippet-start:[s3.cpp.get_object.code]
bool AwsDoc::S3::GetObject(const Aws::String& objectKey,
    const Aws::String& fromBucket, const Aws::String& region)
{
    Aws::S3::S3Client s3_client;
    Aws::S3::Model::GetObjectRequest object_request;
    object_request.SetBucket(fromBucket);
    object_request.SetKey(objectKey);

    Aws::S3::Model::GetObjectOutcome get_object_outcome = 
        s3_client.GetObject(object_request);

    if (get_object_outcome.IsSuccess())
    {
        auto& retrieved_file = get_object_outcome.GetResultWithOwnership().
            GetBody();

        // Print a beginning portion of the text file.
        std::cout << "Beginning of file contents:\n";
        char file_data[255] = { 0 };
        retrieved_file.getline(file_data, 254);
        std::cout << file_data << std::endl;

        return true;
    }
    else
    {
        auto err = get_object_outcome.GetError();
        std::cout << "Error: GetObject: " <<
            err.GetExceptionName() << ": " << err.GetMessage() << std::endl;

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

        if (!AwsDoc::S3::GetObject(object_name, bucket_name))
        {
            return 1;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3.cpp.get_object.code]

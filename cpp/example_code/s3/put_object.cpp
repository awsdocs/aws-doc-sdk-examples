 
//snippet-sourcedescription:[put_object.cpp demonstrates how to put a file into an Amazon S3 bucket.]
//snippet-service:[s3]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourceauthor:[AWS]

/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

//snippet-start:[s3.cpp.put_object.inc]
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <iostream>
#include <fstream>
#include <sys/stat.h>
//snippet-end:[s3.cpp.put_object.inc]

/**
 * Check if file exists
 * 
 * Note: If using C++17, can use std::filesystem::exists()
 */
inline bool file_exists(const std::string& name)
{
    struct stat buffer;
    return (stat(name.c_str(), &buffer) == 0);
}

/**
 * Put an object into an Amazon S3 bucket
 */
bool put_s3_object(const Aws::String& s3_bucket_name, 
    const Aws::String& s3_object_name, 
    const std::string& file_name, 
    const Aws::String& region = "")
{
    // Verify file_name exists
    if (!file_exists(file_name)) {
        std::cout << "ERROR: NoSuchFile: The specified file does not exist" 
            << std::endl;
        return false;
    }

    // If region is specified, use it
    Aws::Client::ClientConfiguration clientConfig;
    if (!region.empty())
        clientConfig.region = region;

    // Set up request
    // snippet-start:[s3.cpp.put_object.code]
    Aws::S3::S3Client s3_client(clientConfig);
    Aws::S3::Model::PutObjectRequest object_request;

    object_request.SetBucket(s3_bucket_name);
    object_request.SetKey(s3_object_name);
    const std::shared_ptr<Aws::IOStream> input_data = 
        Aws::MakeShared<Aws::FStream>("SampleAllocationTag", 
                                      file_name.c_str(), 
                                      std::ios_base::in | std::ios_base::binary);
    object_request.SetBody(input_data);

    // Put the object
    auto put_object_outcome = s3_client.PutObject(object_request);
    if (!put_object_outcome.IsSuccess()) {
        auto error = put_object_outcome.GetError();
        std::cout << "ERROR: " << error.GetExceptionName() << ": " 
            << error.GetMessage() << std::endl;
        return false;
    }
    return true;
    // snippet-end:[s3.cpp.put_object.code]
}

/**
 * Exercise put_s3_object()
 */
int main(int argc, char** argv)
{

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // Assign these values before running the program
        const Aws::String bucket_name = "BUCKET_NAME";
        const Aws::String object_name = "OBJECT_NAME";
        const std::string file_name = "FILE_NAME";
        const Aws::String region = "";      // Optional

        // Put the file into the S3 bucket
        if (put_s3_object(bucket_name, object_name, file_name, region)) {
            std::cout << "Put file " << file_name 
                << " to S3 bucket " << bucket_name 
                << " as object " << object_name << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
}

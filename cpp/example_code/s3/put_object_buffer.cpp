
//snippet-sourcedescription:[put_object_buffer.cpp demonstrates how to put characters into an Amazon S3 object.]
//snippet-service:[s3]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
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

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <iostream>
#include <fstream>

/**
 * Put a string into an Amazon S3 object
 */
bool put_string_into_s3_object(const Aws::String& s3_bucket_name,
    const Aws::String& s3_object_name,
    const std::string& object_contents,
    const Aws::String& region = "")
{
    // If region is specified, use it
    Aws::Client::ClientConfiguration clientConfig;
    if (!region.empty())
        clientConfig.region = region;

    // Set up request
    Aws::S3::S3Client s3_client(clientConfig);
    Aws::S3::Model::PutObjectRequest object_request;

    object_request.SetBucket(s3_bucket_name);
    object_request.SetKey(s3_object_name);
    const std::shared_ptr<Aws::IOStream> input_data =
        Aws::MakeShared<Aws::StringStream>("");
    *input_data << object_contents.c_str();
    object_request.SetBody(input_data);

    // Put the string into the S3 object
    auto put_object_outcome = s3_client.PutObject(object_request);
    if (!put_object_outcome.IsSuccess()) {
        auto error = put_object_outcome.GetError();
        std::cout << "ERROR: " << error.GetExceptionName() << ": "
            << error.GetMessage() << std::endl;
        return false;
    }
    return true;
}

/**
 * Exercise put_string_into_s3_object()
 */
int main(int argc, char** argv)
{

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // Assign these values before running the program
        const Aws::String bucket_name = "BUCKET_NAME";
        const Aws::String object_name = "OBJECT_NAME";
        const std::string object_contents = "Put this text into the object.";
        const Aws::String region = "";      // Optional

        // Put the file into the S3 bucket
        if (put_string_into_s3_object(bucket_name, object_name, object_contents, region)) {
            std::cout << "The string was put into the object " << object_name
                << " in S3 bucket " << bucket_name << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
}

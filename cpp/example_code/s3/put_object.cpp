/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
 * Put an object from an Amazon S3 bucket.
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    if(argc < 3) {
        std::cout << std::endl <<
            "To run this example, supply the name of an S3 bucket and object to"
            << std::endl << "upload to it." << std::endl << std::endl <<
            "Ex: put_object <bucketname> <filename>\n" << std::endl;
        exit(1);
    }

    const Aws::String bucket_name = argv[1];
    const Aws::String key_name = argv[2];
    const Aws::String dir_name = ".";

    std::cout << "Uploading " << key_name << " to S3 bucket: " <<
        bucket_name << std::endl;

    Aws::S3::S3Client s3_client;

    Aws::S3::Model::PutObjectRequest object_request;
    object_request.WithBucket(bucket_name).WithKey(key_name);

    auto input_data = Aws::MakeShared<Aws::FStream>(key_name.c_str(), dir_name.c_str(), std::ios_base::in);

    object_request.SetBody(input_data);

    auto put_object_outcome = s3_client.PutObject(object_request);

    if(put_object_outcome.IsSuccess()) {
        std::cout << "Done!" << std::endl;
    }
    else {
         std::cout << "PutObject error: " <<
             put_object_outcome.GetError().GetExceptionName() << " " <<
             put_object_outcome.GetError().GetMessage() << std::endl;
    }

    Aws::ShutdownAPI(options);
}


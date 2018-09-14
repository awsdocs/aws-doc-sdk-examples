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
#include <aws/s3/model/GetObjectRequest.h>
#include <fstream>

/**
 * Get an object from an Amazon S3 bucket.
 */
int main(int argc, char** argv)
{
    if (argc < 3)
    {
        std::cout << std::endl <<
            "To run this example, supply the name of an S3 bucket and object to"
            << std::endl << "download from it." << std::endl << std::endl <<
            "Ex: get_object <bucketname> <filename>\n" << std::endl;
        exit(1);
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String bucket_name = argv[1];
        const Aws::String key_name = argv[2];

        std::cout << "Downloading " << key_name << " from S3 bucket: " <<
            bucket_name << std::endl;

        Aws::S3::S3Client s3_client;

        Aws::S3::Model::GetObjectRequest object_request;
        object_request.WithBucket(bucket_name).WithKey(key_name);

        auto get_object_outcome = s3_client.GetObject(object_request);

        if (get_object_outcome.IsSuccess())
        {
            Aws::OFStream local_file;
            local_file.open(key_name.c_str(), std::ios::out | std::ios::binary);
            local_file << get_object_outcome.GetResult().GetBody().rdbuf();
            std::cout << "Done!" << std::endl;
        }
        else
        {
            std::cout << "GetObject error: " <<
                get_object_outcome.GetError().GetExceptionName() << " " <<
                get_object_outcome.GetError().GetMessage() << std::endl;
        }
    }

    Aws::ShutdownAPI(options);
}


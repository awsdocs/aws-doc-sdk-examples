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
#include <aws/s3/model/CopyObjectRequest.h>
#include <fstream>

/**
 * List objects (keys) within an Amazon S3 bucket.
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    if(argc < 4) {
        std::cout << std::endl <<
            "To run this example, supply the name (key) of an S3 object, the bucket name that\n"
            "it's contained within, and the bucket to copy it to.\n"
            "\n"
            "Ex: copy_object <objectname> <frombucket> <tobucket>\n";
        exit(1);
    }

    const Aws::String key_name = argv[1];
    const Aws::String from_bucket = argv[2];
    const Aws::String to_bucket = argv[3];

    std::cout << "Copying" << key_name << " from bucket " << from_bucket <<
        " to " << to_bucket << std::endl;

    Aws::S3::S3Client s3_client;

    Aws::S3::Model::CopyObjectRequest object_request;
    object_request.WithBucket(to_bucket).WithKey(key_name).WithCopySource(from_bucket + "/" + key_name);

    auto copy_object_outcome = s3_client.CopyObject(object_request);

    if(copy_object_outcome.IsSuccess()) {
        std::cout << "Done!" << std::endl;
    }
    else {
         std::cout << "CopyObject error: " <<
             copy_object_outcome.GetError().GetExceptionName() << " " <<
             copy_object_outcome.GetError().GetMessage() << std::endl;
    }

    Aws::ShutdownAPI(options);
}




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
#include <aws/s3/model/ListObjectsRequest.h>
#include <aws/s3/model/Object.h>

/**
 * List objects (keys) within an Amazon S3 bucket.
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (argc < 2)
        {
            std::cout << std::endl <<
                "To run this example, supply the name of a bucket to list!" <<
                std::endl << "Ex: list_objects <bucket-name>" << std::endl
                << std::endl;
            exit(1);
        }

        const Aws::String bucket_name = argv[1];
        std::cout << "Objects in S3 bucket: " << bucket_name << std::endl;

        Aws::S3::S3Client s3_client;

        Aws::S3::Model::ListObjectsRequest objects_request;
        objects_request.WithBucket(bucket_name);

        auto list_objects_outcome = s3_client.ListObjects(objects_request);

        if (list_objects_outcome.IsSuccess())
        {
            Aws::Vector<Aws::S3::Model::Object> object_list =
                list_objects_outcome.GetResult().GetContents();

            for (auto const &s3_object : object_list)
            {
                std::cout << "* " << s3_object.GetKey() << std::endl;
            }
        }
        else
        {
            std::cout << "ListObjects error: " <<
                list_objects_outcome.GetError().GetExceptionName() << " " <<
                list_objects_outcome.GetError().GetMessage() << std::endl;
        }
    }

    Aws::ShutdownAPI(options);
}


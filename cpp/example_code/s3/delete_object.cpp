 
//snippet-sourcedescription:[delete_object.cpp demonstrates how to delete an object from an Amazon S3 bucket.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
#include <aws/s3/model/DeleteObjectRequest.h>
#include <fstream>

/**
 * Delete an object from an Amazon S3 bucket.
 *
 * ++ warning ++ This will actually delete the named object!
 */
int main(int argc, char** argv)
{
    if (argc < 3)
    {
        std::cout << std::endl <<
            "To run this example, supply the name of an S3 bucket and object to"
            << std::endl << "delete from it." << std::endl << std::endl <<
            "Ex: delete_object <bucketname> <filename>\n" << std::endl;
        exit(1);
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String bucket_name = argv[1];
        const Aws::String key_name = argv[2];

        std::cout << "Deleting" << key_name << " from S3 bucket: " <<
            bucket_name << std::endl;

        Aws::S3::S3Client s3_client;

        Aws::S3::Model::DeleteObjectRequest object_request;
        object_request.WithBucket(bucket_name).WithKey(key_name);

        auto delete_object_outcome = s3_client.DeleteObject(object_request);

        if (delete_object_outcome.IsSuccess())
        {
            std::cout << "Done!" << std::endl;
        }
        else
        {
            std::cout << "DeleteObject error: " <<
                delete_object_outcome.GetError().GetExceptionName() << " " <<
                delete_object_outcome.GetError().GetMessage() << std::endl;
        }
    }

    Aws::ShutdownAPI(options);
}


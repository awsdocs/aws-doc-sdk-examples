 
//snippet-sourcedescription:[delete_bucket.cpp demonstrates how to delete an Amazon S3 bucket.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
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
//snippet-start:[s3.cpp.delete_bucket.inc]
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketRequest.h>
//snippet-end:[s3.cpp.delete_bucket.inc]

/**
 * Delete an Amazon S3 bucket.
 *
 * ++ Warning ++ This code will actually delete the bucket that you specify!
 */
int main(int argc, char** argv)
{
    if (argc < 2)
    {
        std::cout << "delete_bucket - delete an S3 bucket" << std::endl
            << "\nUsage:" << std::endl
            << "  delete_bucket <bucket> [region]" << std::endl
            << "\nWhere:" << std::endl
            << "  bucket - the bucket to delete" << std::endl
            << "  region - AWS region for the bucket" << std::endl
            << "           (optional, default: us-east-1)" << std::endl
            << "\nNote! This will actually delete the bucket that you specify!"
            << std::endl
            << "\nExample:" << std::endl
            << "  delete_bucket testbucket\n" << std::endl << std::endl;
        exit(1);
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String bucket_name = argv[1];
        const Aws::String user_region = (argc >= 3) ? argv[2] : "us-east-1";

        std::cout << "Deleting S3 bucket: " << bucket_name << std::endl;

        // snippet-start:[s3.cpp.delete_bucket.code]
        Aws::Client::ClientConfiguration config;
        config.region = user_region;
        Aws::S3::S3Client s3_client(config);

        Aws::S3::Model::DeleteBucketRequest bucket_request;
        bucket_request.SetBucket(bucket_name);

        auto outcome = s3_client.DeleteBucket(bucket_request);

        if (outcome.IsSuccess())
        {
            std::cout << "Done!" << std::endl;
        }
        else
        {
            std::cout << "DeleteBucket error: "
                << outcome.GetError().GetExceptionName() << " - "
                << outcome.GetError().GetMessage() << std::endl;
        }
        // snippet-end:[s3.cpp.delete_bucket.code]
    }
    Aws::ShutdownAPI(options);
}


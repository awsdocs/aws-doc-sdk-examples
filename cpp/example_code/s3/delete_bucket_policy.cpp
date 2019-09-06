 
//snippet-sourcedescription:[delete_bucket_policy.cpp demonstrates how to delete an Amazon S3 bucket policy.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
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
//snippet-start:[s3.cpp.delete_bucket_policy.inc]
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketPolicyRequest.h>
//snippet-end:[s3.cpp.delete_bucket_policy.inc]

/**
 * Delete an Amazon S3 bucket policy.
 */
int main(int argc, char** argv)
{
    if (argc < 2)
    {
        std::cout << "delete_bucket_policy - delete the policy on an S3 bucket"
            << std::endl
            << "\nUsage:" << std::endl
            << "  delete_bucket_policy <bucket> [region]" << std::endl
            << "\nWhere:" << std::endl
            << "  bucket - the bucket to delete the policy from.\n"
            << std::endl
            << "  region - AWS region for the bucket" << std::endl
            << "           (optional, default: us-east-1)" << std::endl
            << "\nExample:" << std::endl
            << "  delete_bucket_policy testbucket\n" << std::endl;
        exit(1);
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String bucket_name = argv[1];
        const Aws::String user_region = (argc == 3) ? argv[2] : "us-east-1";
        std::cout << "Deleting policy from bucket: " << bucket_name << std::endl;

        Aws::Client::ClientConfiguration config;
        config.region = user_region;
        Aws::S3::S3Client s3_client(config);

        // snippet-start:[s3.cpp.delete_bucket_policy.code]
        Aws::S3::Model::DeleteBucketPolicyRequest request;
        request.SetBucket(bucket_name);

        auto outcome = s3_client.DeleteBucketPolicy(request);

        if (outcome.IsSuccess())
        {
            std::cout << "Done!" << std::endl;
        }
        else
        {
            std::cout << "DeleteBucketPolicy error: "
                << outcome.GetError().GetExceptionName() << " - "
                << outcome.GetError().GetMessage() << std::endl;
        }
        // snippet-end:[s3.cpp.delete_bucket_policy.code]
    }
    Aws::ShutdownAPI(options);
}


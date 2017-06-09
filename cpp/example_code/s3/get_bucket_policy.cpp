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
#include <aws/s3/model/GetBucketPolicyRequest.h>

/**
 * Get an Amazon S3 bucket policy.
 */
int main(int argc, char** argv)
{
    if(argc < 2) {
        std::cout << "delete_bucket_policy - delete the policy on an S3 bucket"
            << std::endl
            << "\nUsage:" << std::endl
            << "  get_bucket_policy <bucket> [region]\n" << std::endl
            << "\nWhere:" << std::endl
            << "  bucket - the bucket to get the policy from.\n" << std::endl
            << "  region - AWS region for the bucket" << std::endl
            << "           (optional, default: us-east-1)" << std::endl
            << "\nExample:" << std::endl
            << "  get_bucket_policy testbucket" << std::endl << std::endl;
       exit(1);
    }

    const Aws::String bucket_name = argv[1];
    const Aws::String user_region = (argc == 3) ? argv[2] : "us-east-1";
    std::cout << "Getting policy for bucket: " << bucket_name << std::endl;

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration config;
        config.region = user_region;
        Aws::S3::S3Client s3_client(config);

        Aws::S3::Model::GetBucketPolicyRequest request;
        request.SetBucket(bucket_name);

        auto outcome = s3_client.GetBucketPolicy(request);

        if (outcome.IsSuccess()) {
            Aws::StringStream policyStream;
            Aws::String line;
            while (outcome.GetResult().GetPolicy()) {
                outcome.GetResult().GetPolicy() >> line;
                policyStream << line;
            }
            std::cout << "Policy: " << std::endl << policyStream.str() << std::endl;
        } else {
            std::cout << "GetBucketPolicy error: " <<
                outcome.GetError().GetExceptionName() << std::endl <<
                outcome.GetError().GetMessage() << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
}

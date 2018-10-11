 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[<<snippet or full-example>>]
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
#include <cstdio>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutBucketPolicyRequest.h>

/**
 * Set an Amazon S3 bucket policy.
 */

int main(int argc, char** argv)
{
    if(argc < 2) {
        std::cout << std::endl
                  << "put_bucket_policy - set a policy on an S3 bucket"
                  << std::endl
                  << "\nUsage:" << std::endl
                  << "  set_bucket_policy <bucket> [region]" << std::endl
                  << "\nWhere:" << std::endl
                  << "  bucket - the bucket to set the policy on." << std::endl
                  << "  region - AWS region for the bucket" << std::endl
                  << "           (optional, default: us-east-1)" << std::endl
                  << "\nNote! A *public-read* policy will be set." << std::endl
                  << "\nExample:" << std::endl
                  << "  set_bucket_policy testbucket\n" << std::endl;
        exit(1);
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String bucket_name = argv[1];
        const Aws::String user_region = (argc >= 3) ? argv[2] : "us-east-1";
        const Aws::String policy_string =
            "{\n"
            "  \"Version\":\"2012-10-17\",\n"
            "  \"Statement\":[\n"
            "   {\n"
            "     \"Sid\": \"1\",\n"
            "     \"Effect\": \"Allow\",\n"
            "     \"Principal\": {\"AWS\":\"*\"},\n"
            "     \"Action\": [\"s3:GetObject\"],\n"
            "     \"Resource\": [\"arn:aws:s3:::" + bucket_name + "/*\"]\n"
            "   }]\n"
            "}";

        std::cout << "Setting policy:" << std::endl
            << "----" << std::endl
            << policy_string << std::endl
            << "----" << std::endl
            << "On S3 bucket: " << bucket_name << std::endl;

        Aws::Client::ClientConfiguration config;
        config.region = user_region;
        Aws::S3::S3Client s3_client(config);

        auto request_body = Aws::MakeShared<Aws::StringStream>("");
		*request_body << policy_string;

        Aws::S3::Model::PutBucketPolicyRequest request;
        request.SetBucket(bucket_name);
        request.SetBody(request_body);

        auto outcome = s3_client.PutBucketPolicy(request);

        if (outcome.IsSuccess()) {
            std::cout << "Done!" << std::endl;
        } else {
            std::cout << "SetBucketPolicy error: "
                      << outcome.GetError().GetExceptionName() << std::endl
                      << outcome.GetError().GetMessage() << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
}


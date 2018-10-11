 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
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
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/IndexDocument.h>
#include <aws/s3/model/ErrorDocument.h>
#include <aws/s3/model/WebsiteConfiguration.h>
#include <aws/s3/model/PutBucketWebsiteRequest.h>

/**
 * Set an Amazon S3 bucket website configuration.
 */
int main(int argc, char** argv)
{
    if (argc < 2)
    {
        std::cout << "put_website_config - set the website configuration for an S3 bucket"
            << std::endl
            << "\nUsage:" << std::endl
            << "  set_website_config <bucket> [region] [index_doc] [error_doc]\n"
            << std::endl
            << "\nWhere:" << std::endl
            << "  bucket    - the bucket to set the website configuration for."
            << std::endl
            << "  region    - AWS region for the bucket" << std::endl
            << "              (optional, default: us-east-1)" << std::endl
            << "  index_doc - the index page" << std::endl
            << "              (optional, default: index.html)" << std::endl
            << "  error_doc - the error page" << std::endl
            << "              (optional, default: 404.html)" << std::endl
            << "\nExample:" << std::endl
            << "  set_website_config testbucket index.html\n" << std::endl;
        exit(1);
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String bucket_name = argv[1];
        const Aws::String user_region = (argc >= 3) ? argv[2] : "us-east-1";
        const Aws::String index_suffix = (argc >= 4) ? argv[3] : "index.html";
        const Aws::String error_key = (argc >= 5) ? argv[4] : "404.html";

        std::cout << "Setting website configuration for bucket: "
            << bucket_name << std::endl
            << "  with index suffix: " << index_suffix << std::endl
            << "  and error key: " << error_key << std::endl;

        Aws::Client::ClientConfiguration config;
        config.region = user_region;
        Aws::S3::S3Client s3_client(config);

        Aws::S3::Model::IndexDocument index_doc;
        index_doc.SetSuffix(index_suffix);

        Aws::S3::Model::ErrorDocument error_doc;
        error_doc.SetKey(error_key);

        Aws::S3::Model::WebsiteConfiguration website_config;
        website_config.SetIndexDocument(index_doc);
        website_config.SetErrorDocument(error_doc);

        Aws::S3::Model::PutBucketWebsiteRequest request;
        request.SetBucket(bucket_name);
        request.SetWebsiteConfiguration(website_config);

        auto outcome = s3_client.PutBucketWebsite(request);

        if (outcome.IsSuccess())
        {
            std::cout << "Done!" << std::endl;
        }
        else
        {
            std::cout << "PutBucketWebsite error: "
                << outcome.GetError().GetExceptionName() << std::endl
                << outcome.GetError().GetMessage() << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
}


// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

//snippet-start:[s3.cpp.put_website_config.inc]
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/IndexDocument.h>
#include <aws/s3/model/ErrorDocument.h>
#include <aws/s3/model/WebsiteConfiguration.h>
#include <aws/s3/model/PutBucketWebsiteRequest.h>
#include <awsdoc/s3/s3_examples.h>
//snippet-end:[s3.cpp.put_website_config.inc]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Configures a bucket in Amazon S3 for static website hosting.
 *
 * Prerequisites: An Amazon S3 bucket.
 *
 * Inputs:
 * - bucketName: The name of the bucket.
 * - indexPage: The document to be used as the website's index page.
 * - errorPage: The document to be used as the website's error page.
 * - region: The AWS Region where the bucket is hosted.
 *
 * Outputs: true if static website hosting was configured for the
 * bucket; otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[s3.cpp.put_website_config.code]
bool AwsDoc::S3::PutWebsiteConfig(const Aws::String& bucketName, 
    const Aws::String& indexPage, const Aws::String& errorPage, 
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;

    if (!region.empty())
    {
        config.region = region;

    }

    Aws::S3::S3Client s3_client(config);
        
    Aws::S3::Model::IndexDocument index_doc;
    index_doc.SetSuffix(indexPage);

    Aws::S3::Model::ErrorDocument error_doc;
    error_doc.SetKey(errorPage);

    Aws::S3::Model::WebsiteConfiguration website_config;
    website_config.SetIndexDocument(index_doc);
    website_config.SetErrorDocument(error_doc);

    Aws::S3::Model::PutBucketWebsiteRequest request;
    request.SetBucket(bucketName);
    request.SetWebsiteConfiguration(website_config);

    Aws::S3::Model::PutBucketWebsiteOutcome outcome = 
        s3_client.PutBucketWebsite(request);

    if (outcome.IsSuccess())
    {
        std::cout << "Success: Set website configuration for bucket '" 
            << bucketName << "'." << std::endl;

        return true;
    }
    else
    {
        std::cout << "Error: PutBucketWebsite: "
            << outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    
    return 1;
}

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        //TODO: Change bucket_name to the name of a bucket in your account.
        const Aws::String bucket_name = "DOC-EXAMPLE-BUCKET";
        //TODO: Set to the AWS Region in which the bucket was created.
        const Aws::String region = "us-east-1";
        //TODO: Create these two files to serve as your website
        const Aws::String index_page = "index.html";
        const Aws::String error_page = "404.html";


        if (!AwsDoc::S3::PutWebsiteConfig(bucket_name, index_page, error_page, region))
        {
            return 1;
        }
        
    }
    Aws::ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3.cpp.put_website_config.code]

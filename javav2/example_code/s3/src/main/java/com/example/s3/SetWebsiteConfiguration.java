//snippet-sourceauthor: [soo-aws]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package com.example.s3;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ErrorDocument;
import software.amazon.awssdk.services.s3.model.IndexDocument;
import software.amazon.awssdk.services.s3.model.PutBucketWebsiteRequest;
import software.amazon.awssdk.services.s3.model.WebsiteConfiguration;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.regions.Region;

/**
 * Set the website configuration for an S3 bucket.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class SetWebsiteConfiguration
{
    public static void setWebsiteConfig(
        String bucket_name, String index_doc, String error_doc)
    {
        WebsiteConfiguration website_config = WebsiteConfiguration.builder()
        		.indexDocument(IndexDocument.builder().suffix(index_doc).build())
        		.errorDocument(ErrorDocument.builder().key(error_doc).build())
        		.build();

        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();
        
        PutBucketWebsiteRequest pubWebsiteReq = PutBucketWebsiteRequest.builder()
        		.bucket(bucket_name)
        		.websiteConfiguration(website_config)
        		.build();
        
        try {
            s3.putBucketWebsite(pubWebsiteReq);
        } catch (S3Exception e) {
            System.out.format(
                "Failed to set website configuration for bucket '%s'!\n",
                bucket_name);
            System.err.println(e.errorMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "SetWebsiteConfiguration - set the website configuration for an S3 bucket\n\n" +
            "Usage: SetWebsiteConfiguration <bucket> [indexdoc] [errordoc]\n\n" +
            "Where:\n" +
            "   bucket   - the bucket to set the website configuration on\n" +
            "   indexdoc - (optional) the index document, ex. 'index.html'\n" +
            "              If not specified, 'index.html' will be set.\n" +
            "   errordoc - (optional) the error document, ex. 'notfound.html'\n" +
            "              If not specified, no error doc will be set.\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        final String bucket_name = args[0];
        final String index_doc = (args.length > 1) ? args[1] : "index.html";
        final String error_doc = (args.length > 2) ? args[2] : null;

        setWebsiteConfig(bucket_name, index_doc, error_doc);
    }
}


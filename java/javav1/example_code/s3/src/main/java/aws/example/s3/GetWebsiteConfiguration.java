//snippet-sourcedescription:[GetWebsiteConfiguration.java demonstrates how to get the website configuration for an S3 bucket.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[getBucketWebsiteConfiguration]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
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
package aws.example.s3;
// snippet-start:[s3.java1.s3_get_website_config.import]

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
// snippet-end:[s3.java1.s3_get_website_config.import]

/**
 * Get the website configuration for an S3 bucket.
 * 
 * This code expects that you have AWS credentials get up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/getup-credentials.html
 */
// snippet-start:[s3.java1.s3_get_website_config.complete]
public class GetWebsiteConfiguration {
    public static void getWebsiteConfig(String bucket_name) {
        // snippet-start:[s3.java1.s3_get_website_config.main]
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
        try {
            BucketWebsiteConfiguration config =
                    s3.getBucketWebsiteConfiguration(bucket_name);
            if (config == null) {
                System.out.println("No website configuration found!");
            } else {
                System.out.format("Index document: %s\n",
                        config.getIndexDocumentSuffix());
                System.out.format("Error document: %s\n",
                        config.getErrorDocument());
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.out.println("Failed to get website configuration!");
            System.exit(1);
        }
        // snippet-end:[s3.java1.s3_get_website_config.main]
    }

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "GetWebsiteConfiguration - get the website configuration for an S3 bucket\n\n" +
                "Usage: GetWebsiteConfiguration <bucket>\n\n" +
                "Where:\n" +
                "   bucket   - the bucket to get the website configuration from\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        final String bucket_name = args[0];

        System.out.format("Retrieving website configuration for bucket: %s\n",
                bucket_name);
        getWebsiteConfig(bucket_name);
    }
}
// snippet-end:[s3.java1.s3_get_website_config.complete]
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.s3;
// snippet-start:[s3.java1.s3_delete_website_config.import]

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
// snippet-end:[s3.java1.s3_delete_website_config.import]

/**
 * Delete the website configuration for an S3 bucket.
 * 
 * This code expects that you have AWS credentials delete up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/deleteup-credentials.html
 */
// snippet-start:[s3.java1.s3_delete_website_config.complete]
public class DeleteWebsiteConfiguration {
    public static void deleteWebsiteConfig(String bucket_name) {
        // snippet-start:[s3.java1.s3_delete_website_config.main]
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
        try {
            s3.deleteBucketWebsiteConfiguration(bucket_name);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.out.println("Failed to delete website configuration!");
            System.exit(1);
        }
        // snippet-end:[s3.java1.s3_delete_website_config.main]
    }

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "DeleteWebsiteConfiguration - delete the website configuration for an S3 bucket\n\n" +
                "Usage: DeleteWebsiteConfiguration <bucket>\n\n" +
                "Where:\n" +
                "   bucket   - the bucket to delete the website configuration from\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        final String bucket_name = args[0];

        System.out.format("Deleting website configuration for bucket: %s\n",
                bucket_name);
        deleteWebsiteConfig(bucket_name);
        System.out.println("Done!");
    }
}
// snippet-end:[s3.java1.s3_delete_website_config.complete]

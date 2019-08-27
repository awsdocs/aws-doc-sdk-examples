//snippet-sourcedescription:[SetWebsiteConfiguration.java demonstrates how to set the website configuration for an S3 bucket.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[setBucketWebsiteConfiguration]
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
// snippet-start:[s3.java1.s3_set_website_config.import]

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
// snippet-end:[s3.java1.s3_set_website_config.import]

/**
 * Set the website configuration for an S3 bucket.
 * 
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
// snippet-start:[s3.java1.s3_set_website_config.complete]
public class SetWebsiteConfiguration {
    public static void setWebsiteConfig(
            // snippet-start:[s3.java1.s3_set_website_config.main]
            String bucket_name, String index_doc, String error_doc) {
        BucketWebsiteConfiguration website_config = null;

        if (index_doc == null) {
            website_config = new BucketWebsiteConfiguration();
        } else if (error_doc == null) {
            website_config = new BucketWebsiteConfiguration(index_doc);
        } else {
            website_config = new BucketWebsiteConfiguration(index_doc, error_doc);
        }

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
        try {
            s3.setBucketWebsiteConfiguration(bucket_name, website_config);
        } catch (AmazonServiceException e) {
            System.out.format(
                    "Failed to set website configuration for bucket '%s'!\n",
                    bucket_name);
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        // snippet-end:[s3.java1.s3_set_website_config.main]
    }

    public static void main(String[] args) {
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
// snippet-end:[s3.java1.s3_set_website_config.complete]
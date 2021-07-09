//snippet-sourcedescription:[SetWebsiteConfiguration.java demonstrates how to set the website configuration for an Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/28/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3;

// snippet-start:[s3.java2.set_website_configuration.import]
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ErrorDocument;
import software.amazon.awssdk.services.s3.model.IndexDocument;
import software.amazon.awssdk.services.s3.model.PutBucketWebsiteRequest;
import software.amazon.awssdk.services.s3.model.WebsiteConfiguration;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.regions.Region;
// snippet-end:[s3.java2.set_website_configuration.import]

/**
 * To run this AWS code example, ensure that you have setup your development environment, including your AWS credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class SetWebsiteConfiguration {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: SetWebsiteConfiguration <bucketName> [indexdoc] [errordoc]\n\n" +
                "Where:\n" +
                "   bucketName   - the Amazon S3 bucket to set the website configuration on. \n" +
                "   indexdoc - (optional) the index document, ex. 'index.html'\n" +
                "              If not specified, 'index.html' will be set.\n" +
                "   errordoc - (optional) the error document, ex. 'notfound.html'\n" +
                "              If not specified, no error doc will be set.\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucketName = args[0];
        String indexDoc = (args.length > 1) ? args[1] : "index.html";
        String errorDoc = (args.length > 2) ? args[2] : null;

        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        setWebsiteConfig(s3, bucketName, indexDoc, errorDoc);
        s3.close();
    }

    // snippet-start:[s3.java2.set_website_configuration.main]
    public static void setWebsiteConfig( S3Client s3,
                                         String bucketName,
                                         String indexDoc,
                                         String errorDoc) {

        try {
            WebsiteConfiguration websiteConfig = WebsiteConfiguration.builder()
                .indexDocument(IndexDocument.builder().suffix(indexDoc).build())
                .errorDocument(ErrorDocument.builder().key(errorDoc).build())
                .build();

            PutBucketWebsiteRequest pubWebsiteReq = PutBucketWebsiteRequest.builder()
                .bucket(bucketName)
                .websiteConfiguration(websiteConfig)
                .build();

            s3.putBucketWebsite(pubWebsiteReq);

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
 }
// snippet-end:[s3.java2.set_website_configuration.main]

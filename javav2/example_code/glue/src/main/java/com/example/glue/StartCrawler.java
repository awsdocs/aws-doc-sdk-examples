//snippet-sourcedescription:[StartCrawler.java demonstrates how to start an AWS Glue crawler.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.glue;

//snippet-start:[glue.java2.start_crawler.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.GlueException;
import software.amazon.awssdk.services.glue.model.StartCrawlerRequest ;
//snippet-end:[glue.java2.start_crawler.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class StartCrawler {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <crawlerName>\n\n" +
                "Where:\n" +
                "    crawlerName - The name of the crawler. \n";

        if (args.length != 1) {
             System.out.println(usage);
              System.exit(1);
         }

        String crawlerName = args[0];
        Region region = Region.US_EAST_1;
        GlueClient glueClient = GlueClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        startSpecificCrawler(glueClient, crawlerName);
        glueClient.close();
    }

    //snippet-start:[glue.java2.start_crawler.main]
    public static void startSpecificCrawler(GlueClient glueClient, String crawlerName) {

        try {
            StartCrawlerRequest crawlerRequest = StartCrawlerRequest.builder()
                .name(crawlerName)
                .build();

            glueClient.startCrawler(crawlerRequest);

        } catch (GlueException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[glue.java2.start_crawler.main]
 }

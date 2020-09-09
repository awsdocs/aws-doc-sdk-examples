//snippet-sourcedescription:[DeleteCrawler.java demonstrates how to delete an AWS Glue crawler.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-service:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[9/3/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example.glue;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.DeleteCrawlerRequest;
import software.amazon.awssdk.services.glue.model.GlueException;

public class DeleteCrawler {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the name of the crawler to delete.  \n" +
                "\n" +
                "Ex: DeleteCrawler <crawlerName>\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String crawlerName = "crawl3"; // args[0] ;

        Region region = Region.US_EAST_1;
        GlueClient glueClient = GlueClient.builder()
                .region(region)
                .build();

        deleteSpecificCrawler(glueClient, crawlerName);
    }

    public static void deleteSpecificCrawler(GlueClient glueClient, String crawlerName) {

        try {
            DeleteCrawlerRequest deleteCrawlerRequest = DeleteCrawlerRequest.builder()
                    .name(crawlerName)
                    .build();

            // Delete the Crawler
            glueClient.deleteCrawler(deleteCrawlerRequest);
            System.out.println(crawlerName +" was deleted");
        } catch (GlueException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
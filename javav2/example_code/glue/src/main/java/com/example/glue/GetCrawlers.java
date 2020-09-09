//snippet-sourcedescription:[GetCrawlers.java demonstrates how to get AWS Glue crawlers.]
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
import software.amazon.awssdk.services.glue.model.*;
import java.util.List;

public class GetCrawlers {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        GlueClient glueClient = GlueClient.builder()
                .region(region)
                .build();

        getAllCrawlers(glueClient);
    }

    public static void getAllCrawlers(GlueClient glueClient){

        try {

        GetCrawlersRequest crawlersRequest = GetCrawlersRequest.builder()
                .maxResults(10)
                .build();

        GetCrawlersResponse response = glueClient.getCrawlers(crawlersRequest);
        List<Crawler> crawlers = response.crawlers();
        for (Crawler crawler: crawlers) {
            System.out.println("The crawler name is : "+crawler.name());
        }

        } catch (GlueException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}

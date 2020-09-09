//snippet-sourcedescription:[CreateCrawler.java demonstrates how to create an AWS Glue crawler.]
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

//snippet-start:[glue.java2.create_crawler.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.CreateCrawlerRequest;
import software.amazon.awssdk.services.glue.model.CrawlerTargets;
import software.amazon.awssdk.services.glue.model.GlueException;
import software.amazon.awssdk.services.glue.model.S3Target;
import java.util.ArrayList;
import java.util.List;
//snippet-end:[glue.java2.create_crawler.import]

public class CreateCrawler {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateCrawler <IAM><s3Path><cron><dbName><crawlerName>\n\n" +
                "Where:\n" +
                "    IAM - the ARN of the IAM role that has AWS Glue and S3 permissions \n" +
                "    s3Path   - the Amazon Simple Storage Service (Amazon S3) target that contains data (i.e., CSV data)\n" +
                "    cron   - a cron expression used to specify the schedule  (i.e., cron(15 12 * * ? *))\n" +
                "    dbName - the database name \n" +
                "    crawlerName   - the name of the crawler \n" ;

        if (args.length < 5) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String iam = args[0];
        String s3Path = args[1];
        String cron = args[2];
        String dbName = args[3];
        String crawlerName = args[4];

        Region region = Region.US_EAST_1;
        GlueClient glueClient = GlueClient.builder()
                .region(region)
                .build();

        createGlueCrawler(glueClient, iam, s3Path, cron,dbName, crawlerName);
    }

    //snippet-start:[glue.java2.create_crawler.main]
    public static void createGlueCrawler(GlueClient glueClient,
                                         String iam,
                                         String s3Path,
                                         String cron,
                                         String dbName,
                                         String crawlerName) {

      try {

        // Create a S3Target that contains data
        S3Target s3Target = S3Target.builder()
                .path(s3Path)
                .build();

        // Add the S3Target to a list
        List<S3Target> targetList = new ArrayList<S3Target>();
        targetList.add(s3Target);

        CrawlerTargets targets = CrawlerTargets.builder()
                .s3Targets(targetList)
                .build();

        CreateCrawlerRequest crawlerRequest = CreateCrawlerRequest.builder()
                .databaseName(dbName)
                .name(crawlerName)
                .description("Created by the AWS Glue Java API")
                .targets(targets)
                .role(iam)
                .schedule(cron)
                .build();

          glueClient.createCrawler(crawlerRequest);
          System.out.println(crawlerName +" was successfully created");

      } catch (GlueException e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }
   }
    //snippet-end:[glue.java2.create_crawler.main]
}

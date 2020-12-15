//snippet-sourcedescription:[DescribeTextTranslationJob.java demonstrates how to describe a translation job.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Translate]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/06/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.translate;

// snippet-start:[translate.java2._describe_jobs.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.DescribeTextTranslationJobRequest;
import software.amazon.awssdk.services.translate.model.DescribeTextTranslationJobResponse;
import software.amazon.awssdk.services.translate.model.TranslateException;
// snippet-end:[translate.java2._describe_jobs.import]

public class DescribeTextTranslationJob {

    public static void main(String[] args) {


        final String USAGE = "\n" +
                "Usage:\n" +
                "    DescribeTextTranslationJob <id> \n\n" +
                "Where:\n" +
                "    id - a translation job ID value. You can obtain this value from the BatchTranslation example.\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String id = args[0];
        Region region = Region.US_WEST_2;
        TranslateClient translateClient = TranslateClient.builder()
                .region(region)
                .build();

        describeTextTranslationJob(translateClient, id);
        translateClient.close();
    }

    // snippet-start:[translate.java2._describe_jobs.main]
    public static void describeTextTranslationJob(TranslateClient translateClient, String id) {

      try {

        DescribeTextTranslationJobRequest textTranslationJobRequest = DescribeTextTranslationJobRequest.builder()
                .jobId(id)
                 .build();

        DescribeTextTranslationJobResponse jobResponse = translateClient.describeTextTranslationJob(textTranslationJobRequest);
        System.out.println("The job status is "+jobResponse.textTranslationJobProperties().jobStatus() );

      } catch (TranslateException e) {
          System.err.println(e.getMessage());
          System.exit(1);
      }
        // snippet-end:[translate.java2._describe_jobs.main]
   }
}

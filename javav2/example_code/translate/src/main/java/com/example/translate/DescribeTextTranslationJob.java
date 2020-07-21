//snippet-sourcedescription:[DescribeTextTranslationJob.java demonstrates how to describe a translation job.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Translate]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/20/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
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

        if (args.length < 1) {
            System.out.println("Please specify a translation job id value");
            System.exit(1);
        }

        // Retrieve a translation job id value - you can obtain this value from the BatchTranslation example
        String id = args[0];

        Region region = Region.US_WEST_2;
        TranslateClient translateClient = TranslateClient.builder()
                .region(region)
                .build();

        describeTextTranslationJob(translateClient, id);
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

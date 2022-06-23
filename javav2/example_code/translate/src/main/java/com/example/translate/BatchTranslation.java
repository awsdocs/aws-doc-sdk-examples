//snippet-sourcedescription:[BatchTranslation.java demonstrates how to translate multiple text documents located in an Amazon S3 bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Translate]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.translate;

// snippet-start:[translate.java2._batch.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.StartTextTranslationJobRequest;
import software.amazon.awssdk.services.translate.model.InputDataConfig;
import software.amazon.awssdk.services.translate.model.OutputDataConfig;
import software.amazon.awssdk.services.translate.model.StartTextTranslationJobResponse;
import software.amazon.awssdk.services.translate.model.DescribeTextTranslationJobRequest;
import software.amazon.awssdk.services.translate.model.DescribeTextTranslationJobResponse;
import software.amazon.awssdk.services.translate.model.TranslateException;
// snippet-end:[translate.java2._batch.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class BatchTranslation {

    public static long sleepTime = 5;

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <s3Uri> <s3UriOut> <jobName> <dataAccessRoleArn> \n\n" +
                "Where:\n" +
                "    s3Uri - The URI of the Amazon S3 bucket where the documents to translate are located. \n" +
                "    s3UriOut - The URI of the Amazon S3 bucket where the translated documents are saved to.  \n" +
                "    jobName - The job name. \n" +
                "    dataAccessRoleArn - The Amazon Resource Name (ARN) value of the role required for translation jobs.\n";

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String s3Uri = args[0];
        String s3UriOut = args[1];
        String jobName = args[2];
        String dataAccessRoleArn = args[3];

        Region region = Region.US_WEST_2;
        TranslateClient translateClient = TranslateClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String id = translateDocuments(translateClient, s3Uri, s3UriOut, jobName, dataAccessRoleArn);
        System.out.println("Translation job "+id + " is completed");
        translateClient.close();
    }

    // snippet-start:[translate.java2._batch.main]
    public static String translateDocuments(TranslateClient translateClient,
                                          String s3Uri,
                                          String s3UriOut,
                                          String jobName,
                                          String dataAccessRoleArn) {

     try {
            InputDataConfig dataConfig = InputDataConfig.builder()
                .s3Uri(s3Uri)
                .contentType("text/plain")
                .build();

            OutputDataConfig outputDataConfig = OutputDataConfig.builder()
                .s3Uri(s3UriOut)
                .build();

            StartTextTranslationJobRequest textTranslationJobRequest = StartTextTranslationJobRequest.builder()
                .jobName(jobName)
                .dataAccessRoleArn(dataAccessRoleArn)
                .inputDataConfig(dataConfig)
                .outputDataConfig(outputDataConfig)
                .sourceLanguageCode("en")
                .targetLanguageCodes("fr")
                .build();

            StartTextTranslationJobResponse textTranslationJobResponse = translateClient.startTextTranslationJob(textTranslationJobRequest);

            //Keep checking until job is done
            boolean jobDone = false;
            String jobStatus = "" ;
            String jobId = textTranslationJobResponse.jobId();

            DescribeTextTranslationJobRequest jobRequest = DescribeTextTranslationJobRequest.builder()
                    .jobId(jobId)
                    .build();

            while (!jobDone) {

                //Check status on each loop
                DescribeTextTranslationJobResponse response = translateClient.describeTextTranslationJob(jobRequest);
                jobStatus = response.textTranslationJobProperties().jobStatusAsString();

                System.out.println(jobStatus);

                if (jobStatus.contains("COMPLETED"))
                    jobDone = true;
                else {
                    System.out.print(".");
                    Thread.sleep(sleepTime * 1000);
                }
            }
             return textTranslationJobResponse.jobId();

     } catch (TranslateException | InterruptedException e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }
     return "";
  }
    // snippet-end:[translate.java2._batch.main]
}

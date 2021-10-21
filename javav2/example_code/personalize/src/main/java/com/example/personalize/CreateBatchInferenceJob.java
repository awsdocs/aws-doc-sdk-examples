//snippet-sourcedescription:[CreateBatchInferenceJob.java demonstrates how to create
// an Amazon Personalize batch inference job.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[6/1/2021]
//snippet-sourceauthor:[seashman - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.create_batch_inference_job.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.*;

import java.time.Instant;
import java.util.HashMap;
//snippet-end:[personalize.java2.create_batch_inference_job.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 * <p>
 * For information, see this documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateBatchInferenceJob {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateDatasetImportJob <solutionVersionArn, jobName, roleArn, s3DataSource, s3DataDestination, explorationWeight," +
                "explorationItemAgeCutOff>\n\n" +
                "Where:\n" +
                "    solutionVersionArn - The Amazon Resource Name (ARN) of the solution version you want to use.\n" +
                "    jobName - The name for the batch inference job.\n" +
                "    s3InputDataSource - The path to the Amazon S3 bucket where your input list of users or items is stored.\n" +
                "    s3DataDestination - The path to the Amazon S3 bucket where Amazon Personalize will output the " +
                "batch recommendations.\n" +
                "    roleArn - The ARN of the IAM service-linked role that" + "" +
                "has permissions to add data to your output Amazon S3 bucket.\n" + "" +
                "   explorationWeight - A User-Personalization recipe specific field that specifies how " +
                "much to explore (how often to include new items in recommendations)\n" +
                "    explorationItemAgeCutOff - A User-Personalization recipe specific field that defines " +
                "what Amazon Personalize considers a new item in exploration.\n\n";

        // If omitting User-Personalization exploration fields, change from 7 to 5.
        if (args.length != 7) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String solutionVersionArn = args[0];
        String jobName = args[1];
        String s3InputDataSource = args[2];
        String s3DataDestination = args[3];
        String roleArn = args[4];
        String explorationWeight = args[5];
        String explorationItemAgeCutOff = args[6];

        // Change to the region where your Amazon Personalize resources are located
        Region region = Region.US_WEST_2;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        String batchInferenceJobArn = createPersonalizeBatchInferenceJob(personalizeClient, solutionVersionArn,
                jobName, s3InputDataSource, s3DataDestination, roleArn, explorationWeight, explorationItemAgeCutOff);
        System.out.println("Batch inference job ARN: " + batchInferenceJobArn);
        personalizeClient.close();
    }

    //snippet-start:[personalize.java2.create_batch_inference_job.main]
    public static String createPersonalizeBatchInferenceJob(PersonalizeClient personalizeClient,
                                                            String solutionVersionArn,
                                                            String jobName,
                                                            String s3InputDataSourcePath,
                                                            String s3DataDestinationPath,
                                                            String roleArn,
                                                            String explorationWeight,
                                                            String explorationItemAgeCutOff) {

        long waitInMilliseconds = 60 * 1000;
        String status;
        String batchInferenceJobArn;

        try {

            // Set up data input and output parameters.
            S3DataConfig inputSource = S3DataConfig.builder()
                    .path(s3InputDataSourcePath)
                    .build();
            S3DataConfig outputDestination = S3DataConfig.builder()
                    .path(s3DataDestinationPath)
                    .build();

            BatchInferenceJobInput jobInput = BatchInferenceJobInput.builder()
                    .s3DataSource(inputSource)
                    .build();
            BatchInferenceJobOutput jobOutputLocation = BatchInferenceJobOutput.builder()
                    .s3DataDestination(outputDestination)
                    .build();

            // Optional code to build the User-Personalization specific item exploration config.
            HashMap<String, String> explorationConfig = new HashMap<>();

            explorationConfig.put("explorationWeight", explorationWeight);
            explorationConfig.put("explorationItemAgeCutOff", explorationItemAgeCutOff);

            BatchInferenceJobConfig jobConfig = BatchInferenceJobConfig.builder()
                    .itemExplorationConfig(explorationConfig)
                    .build();
            // End optional User-Personalization recipe specific code.

            CreateBatchInferenceJobRequest createBatchInferenceJobRequest = CreateBatchInferenceJobRequest.builder()
                    .solutionVersionArn(solutionVersionArn)
                    .jobInput(jobInput)
                    .jobOutput(jobOutputLocation)
                    .jobName(jobName)
                    .roleArn(roleArn)
                    .batchInferenceJobConfig(jobConfig)   // Optional
                    .build();

            batchInferenceJobArn = personalizeClient.createBatchInferenceJob(createBatchInferenceJobRequest)
                    .batchInferenceJobArn();
            DescribeBatchInferenceJobRequest describeBatchInferenceJobRequest = DescribeBatchInferenceJobRequest.builder()
                    .batchInferenceJobArn(batchInferenceJobArn)
                    .build();

            long maxTime = Instant.now().getEpochSecond() + 3 * 60 * 60;

            while (Instant.now().getEpochSecond() < maxTime) {

                BatchInferenceJob batchInferenceJob = personalizeClient
                        .describeBatchInferenceJob(describeBatchInferenceJobRequest)
                        .batchInferenceJob();

                status = batchInferenceJob.status();
                System.out.println("Batch inference job status: " + status);

                if (status.equals("ACTIVE") || status.equals("CREATE FAILED")) {
                    break;
                }
                try {
                    Thread.sleep(waitInMilliseconds);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            return batchInferenceJobArn;

        } catch (PersonalizeException e) {
            System.out.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }//snippet-end:[personalize.java2.create_batch_inference_job.main]

}


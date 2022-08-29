//snippet-sourcedescription:[DescribeTrainingJob.java demonstrates how to obtain information about a training job.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon SageMaker]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sage;

//snippet-start:[sagemaker.java2.describe_train_job.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.DescribeTrainingJobRequest;
import software.amazon.awssdk.services.sagemaker.model.DescribeTrainingJobResponse;
import software.amazon.awssdk.services.sagemaker.model.SageMakerException;
//snippet-end:[sagemaker.java2.describe_train_job.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeTrainingJob {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <trainingJobName>\n\n" +
            "Where:\n" +
            "    trainingJobName - The name of the training job.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String trainingJobName = args[0];
        Region region = Region.US_WEST_2;
        SageMakerClient sageMakerClient = SageMakerClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        describeTrainJob(sageMakerClient, trainingJobName);
        sageMakerClient.close();
    }

    //snippet-start:[sagemaker.java2.describe_train_job.main]
    public static void describeTrainJob(SageMakerClient sageMakerClient, String trainingJobName) {

       try {
            DescribeTrainingJobRequest trainingJobRequest = DescribeTrainingJobRequest.builder()
                .trainingJobName(trainingJobName)
                .build();

            DescribeTrainingJobResponse jobResponse = sageMakerClient.describeTrainingJob(trainingJobRequest);
            System.out.println("The job status is "+ jobResponse.trainingJobStatusAsString());

       } catch (SageMakerException e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       }
    }
    //snippet-end:[sagemaker.java2.describe_train_job.main]
}

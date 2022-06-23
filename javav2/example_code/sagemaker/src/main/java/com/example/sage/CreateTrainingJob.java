//snippet-sourcedescription:[CreateTrainingJob.java demonstrates how to start a model training job for Amazon SageMaker.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon SageMaker]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sage;

//snippet-start:[sagemaker.java2.train_job.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.S3DataSource;
import software.amazon.awssdk.services.sagemaker.model.DataSource;
import software.amazon.awssdk.services.sagemaker.model.Channel;
import software.amazon.awssdk.services.sagemaker.model.ResourceConfig;
import software.amazon.awssdk.services.sagemaker.model.TrainingInstanceType;
import software.amazon.awssdk.services.sagemaker.model.CheckpointConfig;
import software.amazon.awssdk.services.sagemaker.model.OutputDataConfig;
import software.amazon.awssdk.services.sagemaker.model.StoppingCondition;
import software.amazon.awssdk.services.sagemaker.model.AlgorithmSpecification;
import software.amazon.awssdk.services.sagemaker.model.TrainingInputMode;
import software.amazon.awssdk.services.sagemaker.model.CreateTrainingJobRequest;
import software.amazon.awssdk.services.sagemaker.model.CreateTrainingJobResponse;
import software.amazon.awssdk.services.sagemaker.model.SageMakerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//snippet-end:[sagemaker.java2.train_job.import]

/**
 *  To set up the model data and other requirements to make this Java V2 example work, follow this AWS tutorial prior to running this Java code example.
 *  https://aws.amazon.com/getting-started/hands-on/build-train-deploy-machine-learning-model-sagemaker/
 *
 * Also, set up your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateTrainingJob {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <s3UriData> <s3Uri> <trainingJobName> <roleArn> <s3OutputPath> <channelName> <trainingImage>\n\n" +
                "Where:\n" +
                "    s3UriData - The location of the training data (for example, s3://trainbucket/train.csv).\n\n" +
                "    s3Uri - The Amazon S3 path where you want Amazon SageMaker to store checkpoints (for example, s3://trainbucket).\n\n" +
                "    trainingJobName - The name of the training job. \n\n" +
                "    roleArn - The Amazon Resource Name (ARN) of the IAM role that SageMaker uses.\n\n" +
                "    s3OutputPath - The output path located in an Amazon S3 bucket (for example, s3://trainbucket/sagemaker).\n\n" +
                "    channelName - The channel name (for example, s3://trainbucket/sagemaker).\n\n" +
                "    trainingImage - The training image (for example, 000007028032.bbb.zzz.us-west-2.amazonaws.com/xgboost:latest.\n\n";

        if (args.length != 7) {
            System.out.println(usage);
            System.exit(1);
         }

        String s3UriData = args[0];
        String s3Uri = args[1];
        String trainingJobName = args[2];
        String roleArn = args[3];
        String s3OutputPath = args[4];
        String channelName = args[5];
        String trainingImage = args[6];

        Region region = Region.US_WEST_2;
        SageMakerClient sageMakerClient = SageMakerClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        trainJob(sageMakerClient, s3UriData, s3Uri, trainingJobName, roleArn, s3OutputPath, channelName, trainingImage);
        sageMakerClient.close();
    }

    //snippet-start:[sagemaker.java2.train_job.main]
    public static void trainJob(SageMakerClient sageMakerClient,
                                String s3UriData,
                                String s3Uri,
                                String trainingJobName,
                                String roleArn,
                                String s3OutputPath,
                                String channelName,
                                String trainingImage) {

        try {
            S3DataSource s3DataSource = S3DataSource.builder()
                    .s3Uri(s3UriData)
                    .s3DataType("S3Prefix")
                    .s3DataDistributionType("FullyReplicated")
                    .build();

            DataSource dataSource = DataSource.builder()
                    .s3DataSource(s3DataSource)
                    .build();

            Channel channel = Channel.builder()
                    .channelName(channelName)
                    .contentType("csv")
                    .dataSource(dataSource)
                    .build();

            // Build a list of channels
            List<Channel> myChannel = new ArrayList<>();
            myChannel.add(channel);

            ResourceConfig resourceConfig = ResourceConfig.builder()
                    .instanceType(TrainingInstanceType.ML_M5_2_XLARGE) // ml.c5.2xlarge
                     .instanceCount(10)
                    .volumeSizeInGB(1)
                    .build();

            CheckpointConfig checkpointConfig = CheckpointConfig.builder()
                    .s3Uri(s3Uri)
                    .build();

            OutputDataConfig outputDataConfig = OutputDataConfig.builder()
                    .s3OutputPath(s3OutputPath)
                    .build();

            StoppingCondition stoppingCondition = StoppingCondition.builder()
                    .maxRuntimeInSeconds(1200)
                    .build();

            AlgorithmSpecification algorithmSpecification = AlgorithmSpecification.builder()
                   .trainingImage(trainingImage)
                    .trainingInputMode(TrainingInputMode.FILE)
                    .build();

            // Set hyper parameters.
            Map<String,String> hyperParameters = new HashMap<>();
            hyperParameters.put("num_round", "100");
            hyperParameters.put("eta", "0.2");
            hyperParameters.put("gamma", "4");
            hyperParameters.put("max_depth", "5");
            hyperParameters.put("min_child_weight", "6");
            hyperParameters.put("objective", "binary:logistic");
            hyperParameters.put("silent", "0");
            hyperParameters.put("subsample", "0.8");

            CreateTrainingJobRequest trainingJobRequest = CreateTrainingJobRequest.builder()
                    .trainingJobName(trainingJobName)
                    .algorithmSpecification(algorithmSpecification)
                    .roleArn(roleArn)
                    .resourceConfig(resourceConfig)
                    .checkpointConfig(checkpointConfig)
                    .inputDataConfig(myChannel)
                    .outputDataConfig(outputDataConfig)
                    .stoppingCondition(stoppingCondition)
                    .hyperParameters(hyperParameters)
                    .build();

           CreateTrainingJobResponse jobResponse = sageMakerClient.createTrainingJob(trainingJobRequest);
           System.out.println("The Amazon Resource Name (ARN) of the training job is "+ jobResponse.trainingJobArn());

        } catch (SageMakerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[sagemaker.java2.train_job.main]
}

//snippet-sourcedescription:[CreateModel.java demonstrates how to create a model in Amazon SageMaker.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon SageMaker]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sage;

//snippet-start:[sagemaker.java2.create_model.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.ContainerDefinition;
import software.amazon.awssdk.services.sagemaker.model.ContainerMode;
import software.amazon.awssdk.services.sagemaker.model.CreateModelRequest;
import software.amazon.awssdk.services.sagemaker.model.CreateModelResponse;
import software.amazon.awssdk.services.sagemaker.model.ImageConfig;
import software.amazon.awssdk.services.sagemaker.model.RepositoryAccessMode;
import software.amazon.awssdk.services.sagemaker.model.SageMakerException;
//snippet-end:[sagemaker.java2.create_model.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateModel {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <dataUrl> <image> <modelName> <executionRoleArn>\n\n" +
            "Where:\n" +
            "    dataUrl - The Amazon S3 path where the model artifacts, which result from model training, are stored.\n\n" +
            "    image - The Amazon EC2 Container Registry (Amazon ECR) path where inference code is stored (for example, xxxxx5047983.dkr.ecr.us-west-2.amazonaws.com/train).\n\n" +
            "    modelName - The name of the model.\n\n" +
            "    executionRoleArn - The Amazon Resource Name (ARN) of the IAM role that Amazon SageMaker can assume to access model artifacts (for example, arn:aws:iam::xxxxx5047983:role/service-role/AmazonSageMaker-ExecutionRole-20200627T12xxxx).\n\n";

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String dataUrl = args[0];
        String image = args[1];
        String modelName = args[2];
        String executionRoleArn = args[3];

        Region region = Region.US_WEST_2;
        SageMakerClient sageMakerClient = SageMakerClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        createSagemakerModel(sageMakerClient, dataUrl, image, modelName, executionRoleArn);
        sageMakerClient.close();
    }

    //snippet-start:[sagemaker.java2.create_model.main]
    public static void createSagemakerModel(SageMakerClient sageMakerClient,
                                            String dataUrl,
                                            String image,
                                            String modelName,
                                            String executionRoleArn) {
        try {
            ImageConfig config = ImageConfig.builder()
                .repositoryAccessMode(RepositoryAccessMode.PLATFORM)
                .build();

            ContainerDefinition containerDefinition = ContainerDefinition.builder()
                .modelDataUrl(dataUrl)
                .imageConfig(config)
                .image(image)
                .mode(ContainerMode.SINGLE_MODEL)
                .build();

            CreateModelRequest modelRequest = CreateModelRequest.builder()
                .modelName(modelName)
                .executionRoleArn(executionRoleArn)
                .primaryContainer(containerDefinition)
                .build() ;

            CreateModelResponse response = sageMakerClient.createModel(modelRequest);
            System.out.println("The ARN of the model is " +response.modelArn() );

        } catch (SageMakerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[sagemaker.java2.create_model.main]
}
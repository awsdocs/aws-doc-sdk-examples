//snippet-sourcedescription:[CreateModel.java demonstrates how to create a model in Amazon SageMaker.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon SageMaker]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2020]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sage;

//snippet-start:[sagemaker.java2.create_model.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.ContainerDefinition;
import software.amazon.awssdk.services.sagemaker.model.ContainerMode;
import software.amazon.awssdk.services.sagemaker.model.CreateModelRequest;
import software.amazon.awssdk.services.sagemaker.model.CreateModelResponse;
import software.amazon.awssdk.services.sagemaker.model.SageMakerException;
//snippet-end:[sagemaker.java2.create_model.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateModel {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateModel <dataUrl> <image> <modelName><executionRoleArn>\n\n" +
                "Where:\n" +
                "    dataUrl - the Amazon S3 path where the model artifacts, which result from model training, are stored.\n\n" +
                "    image - the Amazon EC2 Container Registry (Amazon ECR) path where inference code is stored (for example, xxxxx5047983.dkr.ecr.us-west-2.amazonaws.com/train).\n\n" +
                "    modelName - the name of the model.\n\n" +
                "    executionRoleArn - the Amazon Resource Name (ARN) of the IAM role that Amazon SageMaker can assume to access model artifacts (for example, arn:aws:iam::xxxxx5047983:role/service-role/AmazonSageMaker-ExecutionRole-20200627T12xxxx).\n\n";

        if (args.length != 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String dataUrl = args[0];
        String image = args[1];
        String modelName = args[2];
        String executionRoleArn = args[3];

        Region region = Region.US_WEST_2;
        SageMakerClient sageMakerClient = SageMakerClient.builder()
                .region(region)
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
            ContainerDefinition containerDefinition = ContainerDefinition.builder()
                    .modelDataUrl(dataUrl)
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
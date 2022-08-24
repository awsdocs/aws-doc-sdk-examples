//snippet-sourcedescription:[CreateTransformJob.java demonstrates how to start a transform job that uses a trained model to get inferences on a dataset.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon SageMaker]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sage;

//snippet-start:[sagemaker.java2.transform_job.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.TransformS3DataSource;
import software.amazon.awssdk.services.sagemaker.model.TransformDataSource;
import software.amazon.awssdk.services.sagemaker.model.TransformInput;
import software.amazon.awssdk.services.sagemaker.model.TransformOutput;
import software.amazon.awssdk.services.sagemaker.model.TransformResources;
import software.amazon.awssdk.services.sagemaker.model.CreateTransformJobRequest;
import software.amazon.awssdk.services.sagemaker.model.CreateTransformJobResponse;
import software.amazon.awssdk.services.sagemaker.model.SageMakerException;
//snippet-end:[sagemaker.java2.transform_job.import]


/**
 *  To set up the model data and other requirements to make this Java V2 example work, follow this AWS tutorial prior to running this Java code example.
 *  https://aws.amazon.com/blogs/machine-learning/predicting-customer-churn-with-amazon-machine-learning/
 *
 * Also, set up your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateTransformJob {

    public static void main(String[] args) {


        final String usage = "\n" +
            "Usage:\n" +
            "    <s3Uri> <s3OutputPath> <modelName> <transformJobName>\n\n" +
            "Where:\n" +
            "    s3Uri - Identifies the key name of an Amazon S3 object that contains the data (ie, s3://mybucket/churn.txt).\n\n" +
            "    s3OutputPath - The Amazon S3 location where the results are stored.\n\n" +
            "    modelName - The name of the model.\n\n" +
            "    transformJobName - The name of the transform job.\n\n";

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String s3Uri = args[0];
        String s3OutputPath = args[1];
        String modelName = args[2];
        String transformJobName = args[3];

        Region region = Region.US_WEST_2;
        SageMakerClient sageMakerClient = SageMakerClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        transformJob(sageMakerClient, s3Uri, s3OutputPath, modelName, transformJobName);
        sageMakerClient.close();
    }

    //snippet-start:[sagemaker.java2.transform_job.main]
    public static void transformJob(SageMakerClient sageMakerClient, String s3Uri, String s3OutputPath, String modelName, String transformJobName) {

        try{
            TransformS3DataSource s3DataSource = TransformS3DataSource.builder()
                .s3DataType("S3Prefix")
                .s3Uri(s3Uri)
                .build();

            TransformDataSource dataSource = TransformDataSource.builder()
                .s3DataSource(s3DataSource)
                .build();

            TransformInput input = TransformInput.builder()
                .dataSource(dataSource)
                .contentType("text/csv")
                .splitType("Line")
                .build();

            TransformOutput output = TransformOutput.builder()
                .s3OutputPath(s3OutputPath)
                .build();

            TransformResources resources = TransformResources.builder()
                .instanceCount(1)
                .instanceType("ml.m4.xlarge")
                .build();

            CreateTransformJobRequest jobRequest = CreateTransformJobRequest.builder()
                .transformJobName(transformJobName)
                .modelName(modelName)
                .transformInput(input)
                .transformOutput(output)
                .transformResources(resources)
                .build();

            CreateTransformJobResponse jobResponse = sageMakerClient.createTransformJob(jobRequest) ;
            System.out.println("Response "+jobResponse.transformJobArn());

        } catch (SageMakerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[sagemaker.java2.transform_job.main]
}

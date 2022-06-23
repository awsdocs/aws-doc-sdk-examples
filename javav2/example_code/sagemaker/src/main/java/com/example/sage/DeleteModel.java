//snippet-sourcedescription:[DeleteModel.java demonstrates how to delete a model in Amazon SageMaker.]
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

//snippet-start:[sagemaker.java2.delete_model.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemaker.model.DeleteModelRequest;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.SageMakerException;
//snippet-end:[sagemaker.java2.delete_model.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteModel {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <modelName>\n\n" +
                "Where:\n" +
                "    modelName - The name of the model.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String modelName = args[0];
        Region region = Region.US_WEST_2;
        SageMakerClient sageMakerClient = SageMakerClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        deleteSagemakerModel(sageMakerClient, modelName);
        sageMakerClient.close();
    }

    //snippet-start:[sagemaker.java2.delete_model.main]
    public static void deleteSagemakerModel(SageMakerClient sageMakerClient, String modelName) {

       try {
            DeleteModelRequest deleteModelRequest = DeleteModelRequest.builder()
                .modelName(modelName)
                .build();

            sageMakerClient.deleteModel(deleteModelRequest);

       } catch (SageMakerException e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       }
    }
    //snippet-end:[sagemaker.java2.delete_model.main]
}

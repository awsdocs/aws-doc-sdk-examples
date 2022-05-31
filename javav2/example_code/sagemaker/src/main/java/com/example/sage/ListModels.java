//snippet-sourcedescription:[ListModels.java demonstrates how to retrieve a list of models.]
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

//snippet-start:[sagemaker.java2.list_models.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.ListModelsRequest;
import software.amazon.awssdk.services.sagemaker.model.ListModelsResponse;
import software.amazon.awssdk.services.sagemaker.model.ModelSummary;
import software.amazon.awssdk.services.sagemaker.model.SageMakerException;
import java.util.List;
//snippet-end:[sagemaker.java2.list_models.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ListModels {

    public static void main(String[] args) {

            Region region = Region.US_WEST_2;
            SageMakerClient sageMakerClient = SageMakerClient.builder()
                    .region(region)
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .build();

            listAllModels(sageMakerClient);
            sageMakerClient.close();
        }

    //snippet-start:[sagemaker.java2.list_models.main]
       public static void listAllModels(SageMakerClient sageMakerClient) {

            try {
                ListModelsRequest modelsRequest = ListModelsRequest.builder()
                        .maxResults(15)
                        .build();

                ListModelsResponse modelResponse = sageMakerClient.listModels(modelsRequest);
                List<ModelSummary> items = modelResponse.models();
                for (ModelSummary item : items) {
                    System.out.println("Model name is: " + item.modelName());
                }

            } catch (SageMakerException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
            }
        }
      //snippet-end:[sagemaker.java2.list_models.main]
    }

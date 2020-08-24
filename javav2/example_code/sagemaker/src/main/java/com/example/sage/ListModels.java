//snippet-sourcedescription:[ListModels.java demonstrates how to retrieve a list of models.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon SageMaker]
//snippet-service:[SageMaker]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[8/18/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */


package com.example.sage;

//snippet-start:[sagemaker.java2.list_models.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.ListModelsRequest;
import software.amazon.awssdk.services.sagemaker.model.ListModelsResponse;
import software.amazon.awssdk.services.sagemaker.model.ModelSummary;
import software.amazon.awssdk.services.sagemaker.model.SageMakerException;
//snippet-end:[sagemaker.java2.list_models.import]

import java.util.List;

public class ListModels {

    public static void main(String[] args) {

            Region region = Region.US_WEST_2;
            SageMakerClient sageMakerClient = SageMakerClient.builder()
                    .region(region)
                    .build();

            listAllModels(sageMakerClient);
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

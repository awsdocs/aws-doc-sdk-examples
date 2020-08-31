//snippet-sourcedescription:[DeleteModel.java demonstrates how to delete a model in Amazon SageMaker.]
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

//snippet-start:[sagemaker.java2.delete_model.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemaker.model.DeleteModelRequest;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.SageMakerException;
//snippet-end:[sagemaker.java2.delete_model.import]

public class DeleteModel {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteModel <modelName>\n\n" +
                "Where:\n" +
                "    modelName  - The name of the model.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String modelName = args[0];

        Region region = Region.US_WEST_2;
        SageMakerClient sageMakerClient = SageMakerClient.builder()
                .region(region)
                .build();

        deleteSagemakerModel(sageMakerClient, modelName);
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

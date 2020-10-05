//snippet-sourcedescription:[ListNotebooks.java demonstrates how to list notebooks.]
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

//snippet-start:[sagemaker.java2.list_books.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.ListNotebookInstancesResponse;
import software.amazon.awssdk.services.sagemaker.model.NotebookInstanceSummary;
import software.amazon.awssdk.services.sagemaker.model.SageMakerException;
//snippet-end:[sagemaker.java2.list_books.import]

import java.util.List;

public class ListNotebooks {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        SageMakerClient sageMakerClient = SageMakerClient.builder()
                .region(region)
                .build();

        listBooks(sageMakerClient);
    }

    //snippet-start:[sagemaker.java2.list_books.main]
    public static void listBooks(SageMakerClient sageMakerClient) {

       try {
            // Get a list of notebooks
            ListNotebookInstancesResponse notebookInstancesResponse = sageMakerClient.listNotebookInstances();
            List<NotebookInstanceSummary> items = notebookInstancesResponse.notebookInstances();

            for (NotebookInstanceSummary item: items) {
                System.out.println("The notebook name is: "+item.notebookInstanceName());
            }
       } catch (SageMakerException e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       }
    }
    //snippet-end:[sagemaker.java2.list_books.main]
}

//snippet-sourcedescription:[ListWorkflows.java demonstrates how to list workflows.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-service:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[9/3/2020]
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

package com.example.glue;
//snippet-start:[glue.java2.list_wfs.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.GlueException;
import software.amazon.awssdk.services.glue.model.ListWorkflowsRequest;
import software.amazon.awssdk.services.glue.model.ListWorkflowsResponse;
import java.util.List;
//snippet-end:[glue.java2.list_wfs.import]

public class ListWorkflows {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        GlueClient glueClient = GlueClient.builder()
                .region(region)
                .build();

        listAllWorkflows(glueClient);
    }

    //snippet-start:[glue.java2.list_wfs.main]
    public static void listAllWorkflows( GlueClient glueClient) {

        try {
             ListWorkflowsRequest workflowsRequest = ListWorkflowsRequest.builder()
                .maxResults(10)
                .build();

            ListWorkflowsResponse workflowsResponse = glueClient.listWorkflows(workflowsRequest);
            List<String> workflows = workflowsResponse.workflows();

            for (String workflow: workflows) {
                System.out.println("Workflow name is: "+workflow);
             }

        } catch (GlueException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
   }
    //snippet-end:[glue.java2.list_wfs.main]
}

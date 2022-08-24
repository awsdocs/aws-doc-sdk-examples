//snippet-sourcedescription:[ListWorkflows.java demonstrates how to list workflows.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS Glue]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.glue;
//snippet-start:[glue.java2.list_wfs.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.GlueException;
import software.amazon.awssdk.services.glue.model.ListWorkflowsRequest;
import software.amazon.awssdk.services.glue.model.ListWorkflowsResponse;
import java.util.List;
//snippet-end:[glue.java2.list_wfs.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListWorkflows {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        GlueClient glueClient = GlueClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        listAllWorkflows(glueClient);
        glueClient.close();
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

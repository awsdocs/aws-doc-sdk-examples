//snippet-sourcedescription:[ListInstances.java demonstrates how to list Amazon Connect instances.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Connect]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.connect;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.connect.ConnectClient;
import software.amazon.awssdk.services.connect.model.ConnectException;
import software.amazon.awssdk.services.connect.model.InstanceSummary;
import software.amazon.awssdk.services.connect.model.ListInstancesRequest;
import software.amazon.awssdk.services.connect.model.ListInstancesResponse;
import java.util.List;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ListInstances{
    public static void main(String[] args) {
        Region region = Region.US_EAST_1;
        ConnectClient connectClient = ConnectClient.builder()
            .region(region)
            .build();

        listAllInstances(connectClient);
    }

    // snippet-start:[connect.java2.list.instances.main]
    public static void listAllInstances(ConnectClient connectClient) {
        try {
            ListInstancesRequest instancesRequest = ListInstancesRequest.builder()
                .maxResults(10)
                .build() ;

            ListInstancesResponse response = connectClient.listInstances(instancesRequest);
            List<InstanceSummary> instances = response.instanceSummaryList();
            for (InstanceSummary instance: instances) {
               System.out.println("The identifier of the instance is "+instance.id());
               System.out.println("The instance alias of the instance is "+instance.instanceAlias());
               System.out.println("The ARN  of the instance is "+instance.arn());
           }

        } catch (ConnectException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[connect.java2.list.instances.main]
}

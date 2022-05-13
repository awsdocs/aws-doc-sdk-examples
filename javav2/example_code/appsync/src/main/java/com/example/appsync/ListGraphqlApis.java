// snippet-sourcedescription:[ListGraphqlApis.java demonstrates how to list your AWS AppSync API operations.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS AppSync]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[04-01-2022]
// snippet-sourceauthor:[scmacdon - AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.appsync;

//snippet-start:[appsync.java2.get_apis.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.appsync.AppSyncClient;
import software.amazon.awssdk.services.appsync.model.AppSyncException;
import software.amazon.awssdk.services.appsync.model.GraphqlApi;
import software.amazon.awssdk.services.appsync.model.ListGraphqlApisResponse;
import java.util.List;
//snippet-end:[appsync.java2.get_apis.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListGraphqlApis {

public static void main(String[] args) {

    Region region = Region.US_EAST_1;
    AppSyncClient appSyncClient = AppSyncClient.builder()
            .region(region)
            .build();

    getApis(appSyncClient);
}

//snippet-start:[appsync.java2.get_apis.main]
public static void getApis(AppSyncClient appSyncClient) {

    try {
        ListGraphqlApisResponse apisResponse = appSyncClient.listGraphqlApis();
        List<GraphqlApi> graphList = apisResponse.graphqlApis();
        for (GraphqlApi graph: graphList) {
            System.out.println("The name of the graph api is "+graph.name());
            System.out.println("The API Id is "+graph.apiId());
            System.out.println("The API URI is "+graph.uris());
        }

    } catch (AppSyncException e) {
        System.out.println(e.getMessage());
        System.exit(1);
    }
  }
  //snippet-end:[appsync.java2.get_apis.main]
}

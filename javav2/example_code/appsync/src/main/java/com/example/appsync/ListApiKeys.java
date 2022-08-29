// snippet-sourcedescription:[ListApiKeys.java demonstrates how to get API keys.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS AppSync]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.appsync;

//snippet-start:[appsync.java.get_keys.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.appsync.AppSyncClient;
import software.amazon.awssdk.services.appsync.model.ApiKey;
import software.amazon.awssdk.services.appsync.model.AppSyncException;
import software.amazon.awssdk.services.appsync.model.ListApiKeysRequest;
import software.amazon.awssdk.services.appsync.model.ListApiKeysResponse;
import java.util.List;
//snippet-end:[appsync.java.get_keys.import]

public class ListApiKeys {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <apiId> \n\n" +
                "Where:\n" +
                "   apiId - The id of the API (You can get this value from the AWS Management Console). \n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String apiId = args[0];
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        AppSyncClient appSyncClient = AppSyncClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        getKeys(appSyncClient, apiId);
    }

    //snippet-start:[appsync.java.get_keys.main]
    public static void getKeys(AppSyncClient appSyncClient, String apiId) {

        try {
            ListApiKeysRequest request = ListApiKeysRequest.builder()
                    .apiId(apiId)
                    .build();

            ListApiKeysResponse response = appSyncClient.listApiKeys(request);
            List<ApiKey> keys = response.apiKeys();
            for (ApiKey key: keys) {
                System.out.println("The key Id is : "+key.id());
            }

        } catch (AppSyncException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
//snippet-end:[appsync.java.get_keys.main]
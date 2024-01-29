// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.appsync;

// snippet-start:[appsync.java2.del_key.main]
// snippet-start:[appsync.java2.del_key.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.appsync.AppSyncClient;
import software.amazon.awssdk.services.appsync.model.AppSyncException;
import software.amazon.awssdk.services.appsync.model.DeleteApiKeyRequest;
// snippet-end:[appsync.java2.del_key.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteApiKey {

    public static void main(String[] args) {
        final String usage = """

                Usage:    <apiId> <keyId>\s

                Where:
                   apiId - the id of the API (You can get this value from the AWS Management Console).\s
                   keyId - The Id of the key to delete.
                """;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String apiId = args[0];
        String keyId = args[1];
        AppSyncClient appSyncClient = AppSyncClient.builder()
                .region(Region.US_EAST_1)
                .build();
        deleteKey(appSyncClient, keyId, apiId);
    }

    public static void deleteKey(AppSyncClient appSyncClient, String keyId, String apiId) {
        try {
            DeleteApiKeyRequest apiKeyRequest = DeleteApiKeyRequest.builder()
                    .apiId(apiId)
                    .id(keyId)
                    .build();

            appSyncClient.deleteApiKey(apiKeyRequest);
            System.out.println("The API key was deleted.");

        } catch (AppSyncException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[appsync.java2.del_key.main]

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.dynamodb;

// snippet-start:[dynamodb.java2.update_table.main]
// snippet-start:[dynamodb.java2.update_table.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

// snippet-end:[dynamodb.java2.update_table.import]
/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class UpdateTable {
    public static void main(String[] args) {
        final String usage = """
                Usage:
                    <tableName>
                
                Where:
                    tableName - The Amazon DynamoDB table to update (for example, Music3).
                
                Example:
                    UpdateTable Music
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String tableName = args[0];
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        updateDynamoDBTable(ddb, tableName);
        ddb.close();
    }

    public static void updateDynamoDBTable(DynamoDbClient ddb, String tableName) {
        System.out.format("Updating %s with new stream settings...\n", tableName);

        UpdateTableRequest request = UpdateTableRequest.builder()
                .tableName(tableName)
                .streamSpecification(StreamSpecification.builder()
                        .streamEnabled(true)
                        .streamViewType(StreamViewType.NEW_AND_OLD_IMAGES)
                        .build())
                .build();

        try {
            ddb.updateTable(request);
            System.out.println("Table updated successfully!");
        } catch (DynamoDbException e) {
            System.err.println("Failed to update table: " + e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[dynamodb.java2.update_table.main]

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.dynamodb;

// snippet-start:[dynamodb.java2.update_table.main]
// snippet-start:[dynamodb.java2.update_table.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.UpdateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

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
                    <tableName> <readCapacity> <writeCapacity>

                Where:
                    tableName - The Amazon DynamoDB table to update (for example, Music3).
                    readCapacity  - The new read capacity of the table (for example, 16).
                    writeCapacity - The new write capacity of the table (for example, 10).

                Example:
                    UpdateTable Music3 16 10
                """;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String tableName = args[0];
        Long readCapacity = Long.parseLong(args[1]);
        Long writeCapacity = Long.parseLong(args[2]);
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        updateDynamoDBTable(ddb, tableName, readCapacity, writeCapacity);
        ddb.close();
    }

    public static void updateDynamoDBTable(DynamoDbClient ddb, String tableName, Long readCapacity,
            Long writeCapacity) {
        System.out.format("Updating %s with new provisioned throughput values\n", tableName);
        System.out.format("Read capacity : %d\n", readCapacity);
        System.out.format("Write capacity : %d\n", writeCapacity);

        ProvisionedThroughput tableThroughput = ProvisionedThroughput.builder()
                .readCapacityUnits(readCapacity)
                .writeCapacityUnits(writeCapacity)
                .build();

        UpdateTableRequest request = UpdateTableRequest.builder()
                .provisionedThroughput(tableThroughput)
                .tableName(tableName)
                .build();

        try {
            ddb.updateTable(request);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}
// snippet-end:[dynamodb.java2.update_table.main]

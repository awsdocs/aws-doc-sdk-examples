//snippet-sourcedescription:[UpdateTable.java demonstrates how to update an Amazon DynamoDB table]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/5/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package com.example.dynamodb;

// snippet-start:[dynamodb.java2.update_table.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.UpdateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
// snippet-end:[dynamodb.java2.update_table.import]

/**
 * Updates an AWS DynamoDB table (change provisioned throughput).
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class UpdateTable {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    UpdateTable <table> <read> <write>\n\n" +
                "Where:\n" +
                "    table - the table to put the item in (i.e., Music3)\n" +
                "    read  - the new read capacity of the table (i.e., 16)\n" +
                "    write - the new write capacity of the table (i.e., 10)\n\n" +
                "Example:\n" +
                "    UpdateTable Music3 16 10\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String tableName = args[0];
        Long readCapacity = Long.parseLong(args[1]);
        Long writeCapacity = Long.parseLong(args[2]);

        // Create the DynamoDbClient object
        Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();

        updateDynamoDBTable(ddb, tableName, readCapacity, writeCapacity);
    }

    // snippet-start:[dynamodb.java2.update_table.main]
    public static void updateDynamoDBTable(DynamoDbClient ddb, String tableName,  Long readCapacity, Long writeCapacity  ) {

        System.out.format(
                "Updating %s with new provisioned throughput values\n",
                tableName);
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

        // snippet-end:[dynamodb.java2.update_table.main]
        System.out.println("Done!");
    }
}

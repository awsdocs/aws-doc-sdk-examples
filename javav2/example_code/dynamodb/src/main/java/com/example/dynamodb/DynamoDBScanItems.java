// snippet-sourcedescription:[DynamoDBScanItems demonstrates how to return items from an Amazon DynamoDB table.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon DynamoDB]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.dynamodb;

// snippet-start:[dynamodb.java2.dynamoDB_scan.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import java.util.Map;
import java.util.Set;
// snippet-end:[dynamodb.java2.dynamoDB_scan.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * To scan items from an Amazon DynamoDB table using the AWS SDK for Java V2, its better practice to use the
 * Enhanced Client, See the EnhancedScanRecords example.
 */

public class DynamoDBScanItems {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <tableName>\n\n" +
            "Where:\n" +
            "    tableName - The Amazon DynamoDB table to get information from (for example, Music3).\n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String tableName = args[0];
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
            .credentialsProvider(credentialsProvider)
            .region(region)
            .build();

        scanItems(ddb,tableName);
        ddb.close();
    }

    // snippet-start:[dynamodb.java2.dynamoDB_scan.main]
    public static void scanItems( DynamoDbClient ddb,String tableName ) {

        try {
            ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();

            ScanResponse response = ddb.scan(scanRequest);
            for (Map<String, AttributeValue> item : response.items()) {
                Set<String> keys = item.keySet();
                for (String key : keys) {
                    System.out.println ("The key name is "+key +"\n" );
                    System.out.println("The value is "+item.get(key).s());
                }
            }

        } catch (DynamoDbException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    // snippet-end:[dynamodb.java2.dynamoDB_scan.main]
}

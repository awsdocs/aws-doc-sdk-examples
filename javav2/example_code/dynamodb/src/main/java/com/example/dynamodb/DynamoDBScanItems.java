/**
 * Copyright 2018-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

// snippet-sourcedescription:[DynamoDBScanItems demonstrates how to return one or more items and item attributes by accessing every item in a table.]
// snippet-service:[dynamodb]
// snippet-keyword:[Code Sample]]
// snippet-sourcesyntax:[java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-01-27]
// snippet-sourceauthor:[AWS-scmacdon]

// snippet-start:[dynamodb.java2.dynamoDB_scan.complete]

package com.example.dynamodb;

// snippet-start:[dynamodb.java2.dynamoDB_scan.import]
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
 * Scans information from an AWS DynamoDB table.
 *
 * Takes the name of the table as input.
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */

public class DynamoDBScanItems {

    public static void main(String[] args) {

       final String USAGE = "\n" +
                    "Usage:\n" +
                    "    DynamoDBScanItems <table>\n\n" +
                    "Where:\n" +
                    "    table - the table to get information from (i.e., Music3)\n\n" +
                    "Example:\n" +
                    "    DescribeTable Music3\n";

            if (args.length < 1) {
                System.out.println(USAGE);
                System.exit(1);
            }

        // snippet-start:[dynamodb.java2.dynamoDB_scan.main]
        String tableName = args[0];

        // Create the DynamoDbClient object
        Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();

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
        }

        // snippet-end:[dynamodb.java2.dynamoDB_scan.main]
    }
}
// snippet-end:[dynamodb.java2.dynamoDB_scan.complete]

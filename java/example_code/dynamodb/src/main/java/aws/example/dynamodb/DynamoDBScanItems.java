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

// snippet-start:[dynamodb.java.dynamoDB_scan.complete]
package aws.example.dynamodb;

// snippet-start:[dynamodb.java.dynamoDB_scan.import]
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import java.util.Map;
import java.util.Set;
// snippet-end:[dynamodb.java.dynamoDB_scan.import]


public class DynamoDBScanItems {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Please specify a table name");
            System.exit(1);
        }

        // snippet-start:[dynamodb.java.dynamoDB_scan.main]
        String tableName = args[0];
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

        try {

            ScanRequest scanRequest = new ScanRequest()
                    .withTableName(tableName);

            ScanResult result = client.scan(scanRequest);

            for (Map<String, AttributeValue> item : result.getItems()) {
                Set<String> keys = item.keySet();

                for (String key : keys) {

                    System.out.println ("The key name is "+key +"\n" );
                    System.out.println("The value is "+item.get(key).getS());

                }
            }


        } catch (AmazonDynamoDBException e) {
            e.getStackTrace();
        }

        // snippet-end:[dynamodb.java.dynamoDB_scan.main]
    }
}
// snippet-end:[dynamodb.java.dynamoDB_scan.complete]

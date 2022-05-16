//snippet-sourcedescription:[EnhancedScanRecords.java demonstrates how to scan an Amazon DynamoDB table by using the enhanced client.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/16/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.dynamodb;

// snippet-start:[dynamodb.java2.mapping.scan.import]
import java.util.Iterator;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
// snippet-end:[dynamodb.java2.mapping.scan.import]

/*
 * Before running this code example, create an Amazon DynamoDB table named Customer with these columns:
 *   - id - the id of the record that is the key
 *   - custName - the customer name
 *   - email - the email value
 *   - registrationDate - an instant value when the item was added to the table
 *
 * Also, ensure that you have set up your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class EnhancedScanRecords {

   public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

       DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

        scan(enhancedClient);
        ddb.close();
    }

    // snippet-start:[dynamodb.java2.mapping.scan.main]
    public static void scan( DynamoDbEnhancedClient enhancedClient) {

        try{
            DynamoDbTable<Customer> custTable = enhancedClient.table("Customer", TableSchema.fromBean(Customer.class));
            Iterator<Customer> results = custTable.scan().items().iterator();
            while (results.hasNext()) {

                Customer rec = results.next();
                System.out.println("The record id is "+rec.getId());
                System.out.println("The name is " +rec.getCustName());
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }
    // snippet-end:[dynamodb.java2.mapping.scan.main]
}

//snippet-sourcedescription:[EnhancedModifyItem.java demonstrates how to modify an item located in an Amazon DynamoDB table by using the enhanced client.]
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

// snippet-start:[dynamodb.java2.mapping.moditem.import]
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
// snippet-end:[dynamodb.java2.mapping.moditem.import]

/*
 * Before running this code example, create an Amazon DynamoDB table named Customer with these columns:
 *   - id - the id of the record that is the key
 *   - custName - the customer name
 *   - email - the email value
 *   - registrationDate - an instant value when the item was added to the table

 *  Also, set up your development environment, including your credentials.
 *
 *  For information, see this documentation topic:
 *
 *  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class EnhancedModifyItem {

    public static void main(String[] args) {
        String usage = "Usage:\n" +
                "    <key> <email> \n\n" +
                "Where:\n" +
                "    key - the name of the key in the table (id120).\n" +
                "    email - the value of the modified email column.\n" ;

       if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
       }

        String key = args[0];
        String email = args[1];
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

        String updatedValue = modifyItem(enhancedClient,key,email);
        System.out.println("The updated name value is "+updatedValue);
        ddb.close();
    }

    // snippet-start:[dynamodb.java2.mapping.moditem.main]
    public static String modifyItem(DynamoDbEnhancedClient enhancedClient, String keyVal, String email) {

        try {

            DynamoDbTable<Customer> mappedTable = enhancedClient.table("Customer", TableSchema.fromBean(Customer.class));
            Key key = Key.builder()
                    .partitionValue(keyVal)
                    .build();

            // Get the item by using the key and update the email value.
            Customer customerRec = mappedTable.getItem(r->r.key(key));
            customerRec.setEmail(email);
            mappedTable.updateItem(customerRec);
            return customerRec.getEmail();

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[dynamodb.java2.mapping.moditem.main]
}



//snippet-sourcedescription:[EnhancedScanRecords.java demonstrates how to scan an Amazon DynamoDB table by using the enhanced client.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/30/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.dynamodb;

// snippet-start:[dynamodb.java2.mapping.scan.import]
import java.time.Instant;
import java.util.Iterator;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
// snippet-end:[dynamodb.java2.mapping.scan.import]

/*
    Prior to running this code example, create an Amazon DynamoDB table named Customer with a key named id and populate it with data.
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
            // Create a DynamoDbTable object
            DynamoDbTable<Customer> custTable = enhancedClient.table("Customer", TableSchema.fromBean(Customer.class));
            Iterator<Customer> results = custTable.scan().items().iterator();
            while (results.hasNext()) {

                Customer rec = results.next();
                System.out.println("The record id is "+rec.getId());
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }
    // snippet-end:[dynamodb.java2.mapping.scan.main]

    @DynamoDbBean
    public static class Customer {

        private String id;
        private String name;
        private String email;
        private Instant regDate;

        @DynamoDbPartitionKey
        public String getId() {
            return this.id;
        };

        public void setId(String id) {

            this.id = id;
        }

        @DynamoDbSortKey
        public String getCustName() {
            return this.name;

        }

        public void setCustName(String name) {

            this.name = name;
        }

        public String getEmail() {
            return this.email;
        }

        public void setEmail(String email) {

            this.email = email;
        }

        public Instant getRegistrationDate() {
            return regDate;
        }
        public void setRegistrationDate(Instant registrationDate) {

            this.regDate = registrationDate;
        }
    }
}

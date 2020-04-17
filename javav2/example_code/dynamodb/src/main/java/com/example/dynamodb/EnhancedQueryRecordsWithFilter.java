//snippet-sourcedescription:[EnhancedQueryRecordsWithFilter.java demonstrates how to query an Amazon DynamoDB table with a filter and by using the enhanced client.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[3/15/2020]
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

// snippet-start:[dynamodb.java2.mapping.queryfilter.import]
import java.time.Instant;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
// snippet-end:[dynamodb.java2.mapping.queryfilter.import]

/*
    Before running this code example, create a table named Customer and populate it with data
 */
public class EnhancedQueryRecordsWithFilter {

    // Query the Customer table using a filter
    public static void main(String[] args) {

        // Create a DynamoDbClient object
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        // Create a DynamoDbEnhancedClient and use the DynamoDbClient object
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

        queryTableFilter(enhancedClient);
    }

    // snippet-start:[dynamodb.java2.mapping.queryfilter.main]
    public static void queryTableFilter(DynamoDbEnhancedClient enhancedClient) {

        try{
            // Create a DynamoDbTable object
            DynamoDbTable<EnhancedQueryRecords.Customer> mappedTable = enhancedClient.table("Customer", TableSchema.fromBean(EnhancedQueryRecords.Customer.class));

            // Get the row where email is sblue@noserver.com
            AttributeValue att = AttributeValue.builder()
                    .s("sblue@noserver.com")
                    .build();

            Map<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":value", att);

            Expression expression = Expression.builder()
                    .expression("email = :value")
                    .expressionValues(expressionValues)
                    .build();

            // Create a QueryConditional object that's used in the query operation
            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder().partitionValue("id103")
                            .build());

            // Get items in the Customer table and write out the ID value
            Iterator<EnhancedQueryRecords.Customer> results = mappedTable.query(r -> r.queryConditional(queryConditional).filterExpression(expression)).items().iterator();

            while (results.hasNext()) {

                EnhancedQueryRecords.Customer rec = results.next();
                System.out.println("The record id is "+rec.getId());
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done");
        // snippet-end:[dynamodb.java2.mapping.queryfilter.main]
    }

    // Create the Customer table
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

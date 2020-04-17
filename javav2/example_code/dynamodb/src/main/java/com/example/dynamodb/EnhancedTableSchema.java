//snippet-sourcedescription:[EnhancedTableSchema.java demonstrates how to use the Amazon DynamoDB enhanced client and a TableSchema to put an item into a table.]
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

// snippet-start:[dynamodb.java2.mapping.tableschema.import]
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primarySortKey;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.secondaryPartitionKey;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.secondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
// snippet-end:[dynamodb.java2.mapping.tableschema.import]


/*
    Before running this code example, create a table named Record with a PK named id
 */
// snippet-start:[dynamodb.java2.mapping.tableschema.main]
public class EnhancedTableSchema {

    private static final TableSchema<Record> TABLE_SCHEMA =
            StaticTableSchema.builder(Record.class)
                    .newItemSupplier(Record::new)
                    .addAttribute(String.class, a -> a.name("id")
                            .getter(Record::getId)
                            .setter(Record::setId)
                            .tags(primaryPartitionKey()))
                    .addAttribute(String.class, a -> a.name("sort")
                            .getter(Record::getSort)
                            .setter(Record::setSort)
                            .tags(primarySortKey()))
                    .addAttribute(String.class, a -> a.name("attribute")
                            .getter(Record::getAttribute)
                            .setter(Record::setAttribute))
                    .addAttribute(String.class, a -> a.name("attribute2*")
                            .getter(Record::getAttribute2)
                            .setter(Record::setAttribute2)
                            .tags(secondaryPartitionKey("gsi_1")))
                    .addAttribute(String.class, a -> a.name("attribute3")
                            .getter(Record::getAttribute3)
                            .setter(Record::setAttribute3)
                            .tags(secondarySortKey("gsi_1")))
                    .build();

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

        putRecord(enhancedClient);
    }


    // Put an item into a DynamoDB table
    public static void putRecord(DynamoDbEnhancedClient enhancedClient){

        try {
            // Create a DynamoDbTable object
            DynamoDbTable<Record> mappedTable = enhancedClient.table("Record", TABLE_SCHEMA);

            // Populate the table
            Record record = new Record()
                    .setId("id-value")
                    .setSort("sort-value")
                    .setAttribute("one")
                    .setAttribute2("two")
                    .setAttribute3("three");

            mappedTable.putItem(record);

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("done");
    }
    // snippet-end:[dynamodb.java2.mapping.tableschema.main]

    // Define the Record class that's used to map to the DynamoDB table
    private static class Record {
        private String id;
        private String sort;
        private String attribute;
        private String attribute2;
        private String attribute3;

        private String getId() {
            return id;
        }

        private Record setId(String id) {
            this.id = id;
            return this;
        }

        private String getSort() {
            return sort;
        }

        private Record setSort(String sort) {
            this.sort = sort;
            return this;
        }

        private String getAttribute() {
            return attribute;
        }

        private Record setAttribute(String attribute) {
            this.attribute = attribute;
            return this;
        }

        private String getAttribute2() {
            return attribute2;
        }

        private Record setAttribute2(String attribute2) {
            this.attribute2 = attribute2;
            return this;
        }

        private String getAttribute3() {
            return attribute3;
        }

        private Record setAttribute3(String attribute3) {
            this.attribute3 = attribute3;
            return this;
        }
    }
}

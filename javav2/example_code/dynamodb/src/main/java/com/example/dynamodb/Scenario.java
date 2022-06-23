//snippet-sourcedescription:[Scenario.java demonstrates how to perform various Amazon DynamoDB operations.]
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

// snippet-start:[dynamodb.java2.scenario.import]
import com.fasterxml.jackson.databind.JsonNode;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
// snippet-end:[dynamodb.java2.scenario.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 *  This Java example performs these tasks:
 *
 * 1. Create the Amazon DynamoDB Movie table with partition and sort key.
 * 2. Put data into the Amazon DynamoDB table from a JSON document using the Enhanced client.
 * 3. Add a new item.
 * 4. Get an item by the composite key (the Partition key and Sort key).
 * 5. Update an item.
 * 6. Use a Scan to query items using the Enhanced client.
 * 7. Query all items where the year is 2013 using the Enhanced Client.
 * 8. Delete the table.
 */

// snippet-start:[dynamodb.java2.scenario.main]
public class Scenario {

    public static void main(String[] args) throws IOException {

        final String usage = "\n" +
                "Usage:\n" +
                "    <fileName>\n\n" +
                "Where:\n" +
                "    fileName - The path to the moviedata.json file that you can download from the Amazon DynamoDB Developer Guide.\n" ;

        if (args.length != 1) {
             System.out.println(usage);
             System.exit(1);
        }

        String tableName = "Movies";
        String fileName = args[0];
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        System.out.println("******* Creating an Amazon DynamoDB table named Movies with a key named year and a sort key named title.");
        createTable(ddb, tableName);

        System.out.println("******* Loading data into the Amazon DynamoDB table.");
        loadData(ddb, tableName, fileName);

        System.out.println("******* Getting data from the Movie table.");
        getItem(ddb) ;

        System.out.println("******* Putting a record into the Amazon DynamoDB table.");
        putRecord(ddb);

        System.out.println("******* Updating a record.");
        updateTableItem(ddb, tableName);

        System.out.println("******* Scanning the Amazon DynamoDB table.");
        scanMovies(ddb, tableName);

        System.out.println("******* Querying the Movies released in 2013.");
        queryTable(ddb);

        System.out.println("******* Deleting the Amazon DynamoDB table.");
        deleteDynamoDBTable(ddb, tableName);
        ddb.close();
    }

    // snippet-start:[dynamodb.java2.scenario.create_table.main]
    // Create a table with a Sort key.
    public static void createTable(DynamoDbClient ddb, String tableName) {

        DynamoDbWaiter dbWaiter = ddb.waiter();
        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();

        // Define attributes.
        attributeDefinitions.add(AttributeDefinition.builder()
                .attributeName("year")
                .attributeType("N")
                .build());

        attributeDefinitions.add(AttributeDefinition.builder()
                .attributeName("title")
                .attributeType("S")
                .build());

        ArrayList<KeySchemaElement> tableKey = new ArrayList<>();
        KeySchemaElement key = KeySchemaElement.builder()
                .attributeName("year")
                .keyType(KeyType.HASH)
                .build();

        KeySchemaElement key2 = KeySchemaElement.builder()
                .attributeName("title")
                .keyType(KeyType.RANGE)
                .build();

        // Add KeySchemaElement objects to the list.
        tableKey.add(key);
        tableKey.add(key2);

        CreateTableRequest request = CreateTableRequest.builder()
                .keySchema(tableKey)
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(new Long(10))
                        .writeCapacityUnits(new Long(10))
                        .build())
                .attributeDefinitions(attributeDefinitions)
                .tableName(tableName)
                .build();

        try {
            CreateTableResponse response = ddb.createTable(request);
            DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();

            // Wait until the Amazon DynamoDB table is created.
            WaiterResponse<DescribeTableResponse> waiterResponse = dbWaiter.waitUntilTableExists(tableRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            String newTable = response.tableDescription().tableName();
            System.out.println("The " +newTable + " was successfully created.");

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[dynamodb.java2.scenario.create_table.main]

    // snippet-start:[dynamodb.java2.scenario.query.main]
    // Query the table.
    public static void queryTable(DynamoDbClient ddb) {
            try {

                DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                        .dynamoDbClient(ddb)
                        .build();

                DynamoDbTable<Movies> custTable = enhancedClient.table("Movies", TableSchema.fromBean(Movies.class));
                QueryConditional queryConditional = QueryConditional
                      .keyEqualTo(Key.builder()
                      .partitionValue(2013)
                      .build());

                // Get items in the table and write out the ID value.
                Iterator<Movies> results = custTable.query(queryConditional).items().iterator();
                String result="";

                while (results.hasNext()) {
                      Movies rec = results.next();
                      System.out.println("The title of the movie is "+rec.getTitle());
                      System.out.println("The movie information  is "+rec.getInfo());
                }

                } catch (DynamoDbException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
        }
        // snippet-end:[dynamodb.java2.scenario.query.main]

        // snippet-start:[dynamodb.java2.scenario.scan.main]
        // Scan the table.
        public static void scanMovies(DynamoDbClient ddb, String tableName) {

            System.out.println("******* Scanning all movies.\n");

            try{
                DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                        .dynamoDbClient(ddb)
                        .build();

                DynamoDbTable<Movies> custTable = enhancedClient.table("Movies", TableSchema.fromBean(Movies.class));
                Iterator<Movies> results = custTable.scan().items().iterator();
                while (results.hasNext()) {

                    Movies rec = results.next();
                    System.out.println("The movie title is "+rec.getTitle());
                    System.out.println("The movie year is " +rec.getYear());
                }

            } catch (DynamoDbException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        // snippet-end:[dynamodb.java2.scenario.scan.main]

        // snippet-start:[dynamodb.java2.scenario.populate_table.main]
        // Load data into the table.
        public static void loadData(DynamoDbClient ddb, String tableName, String fileName) throws IOException {

            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(ddb)
                    .build();
            DynamoDbTable<Movies> mappedTable = enhancedClient.table("Movies", TableSchema.fromBean(Movies.class));

            JsonParser parser = new JsonFactory().createParser(new File(fileName));
            com.fasterxml.jackson.databind.JsonNode rootNode = new ObjectMapper().readTree(parser);
            Iterator<JsonNode> iter = rootNode.iterator();
            ObjectNode currentNode;
            int t = 0 ;
            while (iter.hasNext()) {

                // Only add 200 Movies to the table.
                if (t == 200)
                    break ;
                currentNode = (ObjectNode) iter.next();

                int year = currentNode.path("year").asInt();
                String title = currentNode.path("title").asText();
                String info = currentNode.path("info").toString();

                Movies movies = new Movies();
                movies.setYear(year);
                movies.setTitle(title);
                movies.setInfo(info);

                // Put the data into the Amazon DynamoDB Movie table.
                mappedTable.putItem(movies);
                t++;
            }
       }
    // snippet-end:[dynamodb.java2.scenario.populate_table.main]

    // Update the record to include show only directors.
    public static void updateTableItem(DynamoDbClient ddb, String tableName){

        HashMap<String,AttributeValue> itemKey = new HashMap<>();
        itemKey.put("year", AttributeValue.builder().n("1933").build());
        itemKey.put("title", AttributeValue.builder().s("King Kong").build());

        HashMap<String,AttributeValueUpdate> updatedValues = new HashMap<>();
        updatedValues.put("info", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s("{\"directors\":[\"Merian C. Cooper\",\"Ernest B. Schoedsack\"]").build())
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(itemKey)
                .attributeUpdates(updatedValues)
                .build();

        try {
            ddb.updateItem(request);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Item was updated!");
    }

    public static void deleteDynamoDBTable(DynamoDbClient ddb, String tableName) {

        DeleteTableRequest request = DeleteTableRequest.builder()
                .tableName(tableName)
                .build();
        try {
            ddb.deleteTable(request);

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println(tableName +" was successfully deleted!");
    }

    public static void putRecord(DynamoDbClient ddb) {

        try {
            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(ddb)
                    .build();
            DynamoDbTable<Movies> table = enhancedClient.table("Movies", TableSchema.fromBean(Movies.class));

            // Populate the Table.
            Movies record = new Movies();
            record.setYear(2020);
            record.setTitle("My Movie2");
            record.setInfo("no info");
            table.putItem(record);

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Added a new movie to the table.");
    }

    // snippet-start:[dynamodb.java2.scenario.get_item.main]
    public static void getItem(DynamoDbClient ddb) {

            HashMap<String,AttributeValue> keyToGet = new HashMap<>();
            keyToGet.put("year", AttributeValue.builder()
                    .n("1933")
                    .build());

            keyToGet.put("title", AttributeValue.builder()
                    .s("King Kong")
                    .build());

            GetItemRequest request = GetItemRequest.builder()
                    .key(keyToGet)
                    .tableName("Movies")
                    .build();

            try {
                Map<String,AttributeValue> returnedItem = ddb.getItem(request).item();

                if (returnedItem != null) {
                    Set<String> keys = returnedItem.keySet();
                    System.out.println("Amazon DynamoDB table attributes: \n");

                    for (String key1 : keys) {
                        System.out.format("%s: %s\n", key1, returnedItem.get(key1).toString());
                    }
                } else {
                    System.out.format("No item found with the key %s!\n", "year");
                }
            } catch (DynamoDbException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
    }
    // snippet-end:[dynamodb.java2.scenario.get_item.main]
}
// snippet-end:[dynamodb.java2.scenario.main]


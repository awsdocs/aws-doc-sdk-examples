import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSDynamoServiceIntegrationTest {

    private static DynamoDbClient ddb;

    // Define the data members required for the test
    private static String tableName = "";
    private static String itemVal = "";
    private static String updatedVal = "";
    private static String key = "";
    private static String keyVal = "";

    private static String albumTitle = "";
    private static String albumTitleValue = "";
    private static String awards = "";
    private static String awardVal = "";

    private static String songTitle = "";
    private static String songTitleVal = "";


    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        Region region = Region.US_WEST_2;
        ddb = DynamoDbClient.builder().region(region).build();
        try (InputStream input = AWSDynamoServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            tableName = prop.getProperty("tableName");
            key = prop.getProperty("key");
            keyVal = prop.getProperty("keyValue");
            ;
            albumTitle = prop.getProperty("albumTitle");
            albumTitleValue = prop.getProperty("AlbumTitleValue");
            awards = prop.getProperty("Awards");
            awardVal = prop.getProperty("AwardVal");
            songTitle = prop.getProperty("SongTitle");
            songTitleVal = prop.getProperty("SongTitleVal");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSS3Service_thenNotNull() {
        assertNotNull(ddb);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateTable() {


        try {
           CreateTableRequest request = CreateTableRequest.builder()
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName(key)
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName(key)
                            .keyType(KeyType.HASH)
                            .build())
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(new Long(10))
                            .writeCapacityUnits(new Long(10))
                            .build())
                    .tableName(tableName)
                    .build();



            // Check to determine if the table exists
            Boolean ans = DoesTableExist(tableName);
            if (ans == false) {
                CreateTableResponse response = ddb.createTable(request);
                System.out.println("The following table was created " + response.tableDescription().tableName());

            } else
                System.out.println("The table " + tableName + " already exists");

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeTable() {

        DescribeTableRequest request = DescribeTableRequest.builder()
                .tableName(tableName)
                .build();

        try {
            TableDescription tableInfo = ddb.describeTable(request).table();

            if (tableInfo != null) {
                System.out.format("Table name  : %s\n",
                        tableInfo.tableName());
                System.out.format("Table ARN   : %s\n",
                        tableInfo.tableArn());
                System.out.format("Status      : %s\n",
                        tableInfo.tableStatus());
                System.out.format("Item count  : %d\n",
                        tableInfo.itemCount().longValue());
                System.out.format("Size (bytes): %d\n",
                        tableInfo.tableSizeBytes().longValue());

                ProvisionedThroughputDescription throughput_info =
                        tableInfo.provisionedThroughput();
                System.out.println("Throughput");
                System.out.format("  Read Capacity : %d\n",
                        throughput_info.readCapacityUnits().longValue());
                System.out.format("  Write Capacity: %d\n",
                        throughput_info.writeCapacityUnits().longValue());

                List<AttributeDefinition> attributes =
                        tableInfo.attributeDefinitions();
                System.out.println("Attributes");
                for (AttributeDefinition a : attributes) {
                    System.out.format("  %s (%s)\n",
                            a.attributeName(), a.attributeType());
                }
            }
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("\n Test 3 passed");

    }

    @Test
    @Order(4)
    public void PutItem() {

        HashMap<String, AttributeValue> item_values = new HashMap<String, AttributeValue>();

        // Add more content to the table
        item_values.put(key, AttributeValue.builder().s(keyVal).build());
        item_values.put(songTitle, AttributeValue.builder().s(songTitleVal).build());
        item_values.put(albumTitle, AttributeValue.builder().s(albumTitleValue).build());
        item_values.put(awards, AttributeValue.builder().s(awardVal).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item_values)
                .build();

        try {
            //Lets wait 13 secs for table to complete
            TimeUnit.SECONDS.sleep(13);
            ddb.putItem(request);

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\n Test 4 passed");
    }


    @Test
    @Order(5)
    public void ListTables() {

        boolean more_tables = true;
        String last_name = null;

        while (more_tables) {
            try {
                ListTablesResponse response = null;
                if (last_name == null) {
                    ListTablesRequest request = ListTablesRequest.builder().build();
                    response = ddb.listTables(request);
                } else {
                    ListTablesRequest request = ListTablesRequest.builder()
                            .exclusiveStartTableName(last_name).build();
                    response = ddb.listTables(request);
                }

                List<String> table_names = response.tableNames();

                if (table_names.size() > 0) {
                    for (String cur_name : table_names) {
                        System.out.format("Tables: * %s\n", cur_name);
                    }
                } else {
                    System.out.println("No tables found!");
                    System.exit(0);
                }

                last_name = response.lastEvaluatedTableName();
                if (last_name == null) {
                    more_tables = false;
                }
            } catch (DynamoDbException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        System.out.println("\n Test 5 passed");
    }

    @Test
    @Order(6)
    public void QueryTable() {

        String partition_alias = "#a";
        String partition_key_name = key;
        String partition_key_val = keyVal;
        //set up an alias for the partition key name in case it's a reserved word
        HashMap<String, String> attrNameAlias = new HashMap<String, String>();
        attrNameAlias.put(partition_alias, partition_key_name);

        //set up mapping of the partition name with the value
        HashMap<String, AttributeValue> attrValues =
                new HashMap<String, AttributeValue>();
        attrValues.put(":" + partition_key_name, AttributeValue.builder().s(partition_key_val).build());

        QueryRequest queryReq = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression(partition_alias + " = :" + partition_key_name)
                .expressionAttributeNames(attrNameAlias)
                .expressionAttributeValues(attrValues)
                .build();


        try {
            QueryResponse response = ddb.query(queryReq);
            System.out.println(response.count());
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 6 passed");
    }

    @Test
    @Order(7)
    public void updateItem() {

        HashMap<String, AttributeValue> item_key = new HashMap<String, AttributeValue>();

        item_key.put(key, AttributeValue.builder().s(keyVal).build());

        HashMap<String, AttributeValueUpdate> updated_values =
                new HashMap<String, AttributeValueUpdate>();

        updated_values.put("Awards", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s("14").build())
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(item_key)
                .attributeUpdates(updated_values)
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
        System.out.println("\n Test 7 passed");
    }

    @Test
    @Order(8)
    public void getItem() {
        HashMap<String, AttributeValue> key_to_get =
                new HashMap<String, AttributeValue>();

        key_to_get.put(key, AttributeValue.builder()
                .s(keyVal).build());

        GetItemRequest request = GetItemRequest.builder()
                .key(key_to_get)
                .tableName(tableName)
                .build();

        try {
            Map<String, AttributeValue> returned_item = ddb.getItem(request).item();

            if (returned_item != null) {
                Set<String> keys = returned_item.keySet();
                System.out.println("Table Attributes: \n");
                for (String key : keys) {
                    System.out.format("%s: %s\n",
                            key, returned_item.get(key).toString());
                }
            } else {
                System.out.format("No item found with the key %s!\n", key);
            }
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 8 passed");
    }


    @Test
    @Order(9)
    public void DeleteItem() {

        HashMap<String, AttributeValue> key_to_get =
                new HashMap<String, AttributeValue>();

        key_to_get.put(key, AttributeValue.builder()
                .s(keyVal)
                .build());

        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(key_to_get)
                .build();


        try {
            ddb.deleteItem(deleteReq);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 9 passed");
    }

    @Test
    @Order(10)
    public void DeleteTable() {

        DeleteTableRequest request = DeleteTableRequest.builder()
                .tableName(tableName)
                .build();

        try {
            ddb.deleteTable(request);
            System.out.println(tableName + " was successfully deleted!");
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("\n Test 10 passed");
    }


    public Boolean DoesTableExist(String tableName) {

        try {
            ListTablesResponse res = ddb.listTables();
            Iterator<String> allTables = res.tableNames().iterator();

            while (allTables.hasNext()) {

                String name = allTables.next();
                if (name.equals(tableName))
                    return true;
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
           }

        return false;
    }

}

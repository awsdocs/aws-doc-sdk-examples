import com.example.dynamodbasync.DynamoDBAsyncCreateTable;
import com.example.dynamodbasync.DynamoDBAsyncGetItem;
import com.example.dynamodbasync.DynamoDBAsyncListTables;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DynamoDBAsyncTest {

     // Define the data members required for the test
    private static String tableName = "";
    private static String newTableName = "";
    private static String newKey = "";
    private static String key = "";
    private static String keyVal = "";


    @BeforeAll
    public static void setUp() throws IOException {

       try (InputStream input = DynamoDBAsyncTest.class.getClassLoader().getResourceAsStream("config.properties")) {

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
            keyVal = prop.getProperty("keyVal");
            newTableName= prop.getProperty("newTableName");
            newKey= prop.getProperty("newKey");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void DynamoDBAsyncCreateTable() {
        Region region = Region.US_WEST_2;
        DynamoDbAsyncClient client = DynamoDbAsyncClient.builder()
                .region(region)
                .build();

        DynamoDBAsyncCreateTable.createTable(client, newTableName, newKey);
        System.out.println("Test 2 passed");
    }


    @Test
    @Order(2)
    public void DynamoDBAsyncGetItem() {
        Region region = Region.US_WEST_2;
        DynamoDbAsyncClient client = DynamoDbAsyncClient.builder()
                .region(region)
                .build();
        DynamoDBAsyncGetItem.getItem(client, tableName, key, keyVal);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void DynamoDBAsyncListTables() {
        Region region = Region.US_WEST_2;
        DynamoDbAsyncClient client = DynamoDbAsyncClient.builder()
                .region(region)
                .build();
        DynamoDBAsyncListTables.listTables(client);
        System.out.println("Test 3 passed");
    }
}

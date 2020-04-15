import com.example.dynamodb.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

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
        Region region = Region.US_EAST_1;
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
            String result = CreateTable.CreateTable(ddb, tableName, key);
            assertTrue(!result.isEmpty());
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeTable() {

       try {
           DescribeTable.describeDymamoDBTable(ddb,tableName);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 3 passed");
    }

    @Test
    @Order(4)
    public void PutItem() {

          try {
            //Wait 15 secs for table to complete
            TimeUnit.SECONDS.sleep(15);
            PutItem.putItemInTable(ddb,
                     tableName,
                     key,
                     keyVal,
                     albumTitle,
                     albumTitleValue,
                     awards,
                     awardVal,
                     songTitle,
                     songTitleVal);

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

            try {
                ListTables.listAllTables(ddb);
            } catch (DynamoDbException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
      System.out.println("\n Test 5 passed");
    }

    @Test
    @Order(6)
    public void QueryTable() {

        try {
            // A pass returns only 1 record
            int response = Query.queryTable(ddb,tableName, key,keyVal,"#a" );
            assertEquals(response, 1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 6 passed");
    }

    @Test
    @Order(7)
    public void updateItem() {

        try {
            //Update the Awards value to 40
            UpdateItem.updateTableItem(ddb,tableName, key, keyVal, awards, "40");
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

        try {
            GetItem.getDynamoDBItem(ddb, tableName,key,keyVal );
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 8 passed");
    }

    @Test
    @Order(9)
    public void scanItems() {

        try {
            DynamoDBScanItems.scanItems(ddb, tableName);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 9 passed");
        }

    @Test
    @Order(10)
    public void DeleteItem() {
        try {
            DeleteItem.deleteDymamoDBItem(ddb,tableName,key,keyVal);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 10 passed");
    }


    @Test
    @Order(11)
   public void SycnPagination(){

        try {
            SyncPagination.manualPagination(ddb);
            SyncPagination.autoPagination(ddb);
            SyncPagination.autoPaginationWithResume(ddb);;
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
       System.out.println("\n Test 11 passed");
   }

    @Test
    @Order(12)
   public void updateTable(){

        try {
            Long readCapacity = Long.parseLong("16");
            Long writeCapacity = Long.parseLong("10");
            UpdateTable.updateDynamoDBTable(ddb, tableName, readCapacity, writeCapacity);

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 12 passed");
    }

    @Test
    @Order(13)
    public void DeleteTable() {

        try {
            //Wait 15 secs for table to update based on test 12
            TimeUnit.SECONDS.sleep(15);
            DeleteTable.deleteDynamoDBTable(ddb,tableName);
        } catch (DynamoDbException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 13 passed");
    }
}



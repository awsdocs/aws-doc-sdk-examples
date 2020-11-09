import com.example.dynamodb.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DynamoDBTest {

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
        try (InputStream input = DynamoDBTest.class.getClassLoader().getResourceAsStream("config.properties")) {

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

       String result = CreateTable.createTable(ddb, tableName, key);
       assertTrue(!result.isEmpty());
       System.out.println("\n Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeTable() {
       DescribeTable.describeDymamoDBTable(ddb,tableName);
       System.out.println("\n Test 3 passed");
    }

    @Test
    @Order(4)
    public void PutItem() {

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

        System.out.println("\n Test 4 passed");
    }


    @Test
    @Order(5)
    public void ListTables() {

       ListTables.listAllTables(ddb);
       System.out.println("\n Test 5 passed");
    }

    @Test
    @Order(6)
    public void QueryTable() {

      // A pass returns only 1 record
       int response = Query.queryTable(ddb,tableName, key,keyVal,"#a" );
       assertEquals(response, 1);
       System.out.println("\n Test 6 passed");
    }

    @Test
    @Order(7)
    public void updateItem() {

         UpdateItem.updateTableItem(ddb,tableName, key, keyVal, awards, "40");
         System.out.println("\n Test 7 passed");
    }

    @Test
    @Order(8)
    public void getItem() {

        GetItem.getDynamoDBItem(ddb, tableName,key,keyVal );
        System.out.println("\n Test 8 passed");
    }

    @Test
    @Order(9)
    public void scanItems() {

      DynamoDBScanItems.scanItems(ddb, tableName);
      System.out.println("\n Test 9 passed");
     }

    @Test
    @Order(10)
    public void DeleteItem() {

        DeleteItem.deleteDymamoDBItem(ddb,tableName,key,keyVal);
        System.out.println("\n Test 10 passed");
    }


    @Test
    @Order(11)
   public void SycnPagination(){

        SyncPagination.manualPagination(ddb);
        SyncPagination.autoPagination(ddb);
        SyncPagination.autoPaginationWithResume(ddb);;
        System.out.println("\n Test 11 passed");
   }

    @Test
    @Order(12)
   public void updateTable(){

       Long readCapacity = Long.parseLong("16");
       Long writeCapacity = Long.parseLong("10");
       UpdateTable.updateDynamoDBTable(ddb, tableName, readCapacity, writeCapacity);
       System.out.println("\n Test 12 passed");
    }

    @Test
    @Order(13)
    public void DeleteTable() {

        try {
            //Wait 15 secs for table to update based on test 10
            TimeUnit.SECONDS.sleep(15);
            DeleteTable.deleteDynamoDBTable(ddb,tableName);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 13 passed");
    }
}



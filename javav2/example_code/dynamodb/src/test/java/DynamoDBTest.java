import com.example.dynamodb.CreateTable;
import com.example.dynamodb.DeleteItem;
import com.example.dynamodb.DeleteTable;
import com.example.dynamodb.DescribeTable;
import com.example.dynamodb.DynamoDBScanItems;
import com.example.dynamodb.GetItem;
import com.example.dynamodb.ListTables;
import com.example.dynamodb.PutItem;
import com.example.dynamodb.Query;
import com.example.dynamodb.Scenario;
import com.example.dynamodb.ScenarioPartiQ;
import com.example.dynamodb.ScenarioPartiQLBatch;
import com.example.dynamodb.SyncPagination;
import com.example.dynamodb.UpdateItem;
import com.example.dynamodb.UpdateTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    private static String tableName2 = "";
    private static String fileName = "";
    private static String songTitle = "";
    private static String songTitleVal = "";

    @BeforeAll
    public static void setUp() {

        // Run tests on Real AWS Resources
        Region region = Region.US_EAST_1;
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
            fileName = prop.getProperty("fileName");
            key = prop.getProperty("key");
            keyVal = prop.getProperty("keyValue");
            albumTitle = prop.getProperty("albumTitle");
            albumTitleValue = prop.getProperty("AlbumTitleValue");
            awards = prop.getProperty("Awards");
            awardVal = prop.getProperty("AwardVal");
            songTitle = prop.getProperty("SongTitle");
            songTitleVal = prop.getProperty("SongTitleVal");
            tableName2 = "Movies";

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
       assertFalse(result.isEmpty());
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

        DeleteItem.deleteDynamoDBItem(ddb,tableName,key,keyVal);
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

    @Test
    @Order(14)
    public void TestScenario() throws IOException {
         Scenario.createTable(ddb, tableName2);
        Scenario.loadData(ddb, tableName2, fileName);
        Scenario.getItem(ddb) ;
        Scenario.putRecord(ddb);
        Scenario.updateTableItem(ddb, tableName2)  ;
        Scenario.scanMovies(ddb, tableName2);
        Scenario.queryTable(ddb);
        Scenario.deleteDynamoDBTable(ddb, tableName2);
        System.out.println("\n Test 15 passed");
    }

    @Test
    @Order(15)
    public void TestScenarioPartiQL() throws IOException {
        System.out.println("******* Creating an Amazon DynamoDB table named Movies with a key named year and a sort key named title.");
        ScenarioPartiQ.createTable(ddb, "MoviesPartiQ");

        System.out.println("******* Loading data into the Amazon DynamoDB table.");
        ScenarioPartiQ.loadData(ddb, fileName);

        System.out.println("******* Getting data from the Movie table.");
        ScenarioPartiQ.getItem(ddb);

        System.out.println("******* Putting a record into the Amazon DynamoDB table.");
        ScenarioPartiQ.putRecord(ddb);

        System.out.println("******* Updating a record.");
        ScenarioPartiQ.updateTableItem(ddb);

        System.out.println("******* Querying the Movies released in 2013.");
        ScenarioPartiQ.queryTable(ddb);

        System.out.println("******* Deleting the Amazon DynamoDB table.");
        ScenarioPartiQ.deleteDynamoDBTable(ddb, "MoviesPartiQ");
    }

    @Test
    @Order(16)
    public void TestScenarioPartiQLBatch() {

        String tableName = "MoviesPartiQBatch";
        System.out.println("******* Creating an Amazon DynamoDB table named "+tableName +" with a key named year and a sort key named title.");
        ScenarioPartiQLBatch.createTable(ddb, tableName);

        System.out.println("******* Adding multiple records into the "+ tableName +" table using a batch command.");
        ScenarioPartiQLBatch.putRecordBatch(ddb);

        System.out.println("******* Updating multiple records using a batch command.");
        ScenarioPartiQLBatch.updateTableItemBatch(ddb);

        System.out.println("******* Deleting multiple records using a batch command.");
        ScenarioPartiQLBatch.deleteItemBatch(ddb);

        System.out.println("******* Deleting the Amazon DynamoDB table.");
        ScenarioPartiQLBatch.deleteDynamoDBTable(ddb, tableName);
      }
}



/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.timestream.write.*;
import com.timestream.query.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.timestreamquery.TimestreamQueryClient;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TimestreamTest {

    private static  TimestreamWriteClient timestreamWriteClient;
    private static TimestreamQueryClient queryClient;
    private static String dbName = "";
    private static String newTable = "";

    // TODO" Change database name
    private static String queryString = "SELECT\n" +
            "    truck_id,\n" +
            "    fleet,\n" +
            "    fuel_capacity,\n" +
            "    model,\n" +
            "    load_capacity,\n" +
            "    make,\n" +
            "    measure_name\n" +
            "FROM \"ScottTimeDB\".IoTMulti" ;

    @BeforeAll
    public static void setUp() throws IOException {

        timestreamWriteClient = TimestreamWriteClient.builder()
                .region(Region.US_EAST_1)
                .build();

        queryClient = TimestreamQueryClient.builder()
                .region(Region.US_EAST_1)
                .build();

        try (InputStream input = TimestreamTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            dbName = prop.getProperty("dbName");
            newTable = prop.getProperty("newTable");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(timestreamWriteClient);
        assertNotNull(queryClient);
        assertNotNull(dbName);
        assertNotNull(newTable);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateDatabase() {
        CreateDatabase.createNewDatabase(timestreamWriteClient, dbName);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateTable() {
        CreateTable.createNewTable(timestreamWriteClient, dbName, newTable);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DescribeDatabase() {
        DescribeDatabase.DescribeSingleDatabases(timestreamWriteClient, dbName);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void DescribeTable() {
        DescribeTable.describeSingleTable(timestreamWriteClient, dbName, newTable);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void ListDatabases() {
        ListDatabases.listAllDatabases(timestreamWriteClient);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void ListTables() {
        ListTables.listAllTables(timestreamWriteClient, dbName);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void UpdateTable() {
        UpdateTable.updateTable(timestreamWriteClient, dbName, newTable);
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void WriteData(){
        WriteData.writeRecords(timestreamWriteClient, dbName, newTable);
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(10)
    public void DeleteTable() throws InterruptedException {
        DeleteTable.deleteSpecificTable(timestreamWriteClient, dbName, newTable);
        System.out.println("Test 10 passed");
    }

    @Test
    @Order(11)
    public void DeleteDatabase() {
        DeleteDatabase.delDatabase(timestreamWriteClient, dbName);
        System.out.println("Test 11 passed");
    }

    @Test
    @Order(12)
    public void QueryDatabase() {
        QueryDatabase.runQuery(queryClient, queryString);
        System.out.println("Test 12 passed");
    }
   }

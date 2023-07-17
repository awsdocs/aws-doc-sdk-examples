/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.google.gson.Gson;
import com.timestream.write.*;
import com.timestream.query.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.timestreamquery.TimestreamQueryClient;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import java.io.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TimestreamTest {

    private static  TimestreamWriteClient timestreamWriteClient;
    private static TimestreamQueryClient queryClient;
    private static String dbName = "";
    private static String newTable = "";

    // TODO Change database name in this string.
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
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        queryClient = TimestreamQueryClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        dbName = values.getDbName()+ java.util.UUID.randomUUID();
        newTable = values.getNewTable()+ java.util.UUID.randomUUID();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = TimestreamTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            dbName = prop.getProperty("dbName")+ java.util.UUID.randomUUID();;
            newTable = prop.getProperty("newTable")+ java.util.UUID.randomUUID();;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateDatabase() {
        assertDoesNotThrow(() ->CreateDatabase.createNewDatabase(timestreamWriteClient, dbName));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void CreateTable() {
        assertDoesNotThrow(() ->CreateTable.createNewTable(timestreamWriteClient, dbName, newTable));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DescribeDatabase() {
        assertDoesNotThrow(() ->DescribeDatabase.DescribeSingleDatabases(timestreamWriteClient, dbName));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void DescribeTable() {
        assertDoesNotThrow(() ->DescribeTable.describeSingleTable(timestreamWriteClient, dbName, newTable));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void ListDatabases() {
        assertDoesNotThrow(() ->ListDatabases.listAllDatabases(timestreamWriteClient));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void ListTables() {
        assertDoesNotThrow(() ->ListTables.listAllTables(timestreamWriteClient, dbName));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void UpdateTable() {
        assertDoesNotThrow(() ->UpdateTable.updateTable(timestreamWriteClient, dbName, newTable));
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void WriteData(){
        assertDoesNotThrow(() ->WriteData.writeRecords(timestreamWriteClient, dbName, newTable));
        System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void DeleteTable() {
        assertDoesNotThrow(() ->DeleteTable.deleteSpecificTable(timestreamWriteClient, dbName, newTable));
        System.out.println("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void DeleteDatabase() {
        assertDoesNotThrow(() ->DeleteDatabase.delDatabase(timestreamWriteClient, dbName));
        System.out.println("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void QueryDatabase() {
        assertDoesNotThrow(() ->QueryDatabase.runQuery(queryClient, queryString));
        System.out.println("Test 11 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/timestream";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/timestream (an AWS Secrets Manager secret)")
    class SecretValues {
        private String dbName;
        private String newTable;

        public String getDbName() {
            return dbName;
        }

        public String getNewTable() {
            return newTable;
        }
    }
}


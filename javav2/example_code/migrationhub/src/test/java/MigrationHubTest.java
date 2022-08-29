/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


import com.example.migrationhub.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.*;
import java.util.Properties;
import  software.amazon.awssdk.regions.Region;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MigrationHubTest {

    private static MigrationHubClient migrationClient;
    private static String appId="";
    private static String migrationtask ="";
    private static String progress ="";

    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_WEST_2;
        migrationClient = MigrationHubClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = MigrationHubTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);
            appId = prop.getProperty("appId");
            migrationtask = prop.getProperty("migrationtask");
            progress = prop.getProperty("progress");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSMigrationHub_thenNotNull() {
        assertNotNull(migrationClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void ImportMigrationTask() {

        ImportMigrationTask.importMigrTask(migrationClient, migrationtask, progress);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeAppState() {

        DescribeAppState.describeApplicationState(migrationClient, appId);
        System.out.println("Test 3 passed");
    }


    @Test
    @Order(4)
    public void DescribeMigrationTask() {
        DescribeMigrationTask.describeMigTask(migrationClient, migrationtask, progress);
        System.out.println("Test 4 passed");
   }

    @Test
    @Order(5)
   public void ListApplications() {
       ListApplications.listApps(migrationClient);
        System.out.println("Test 5 passed");
   }

    @Test
    @Order(6)
   public void ListMigrationTasks() {

       ListMigrationTasks.listMigrTasks(migrationClient);
        System.out.println("Test 6 passed");
   }

    @Test
    @Order(7)
   public void DeleteProgressStream() {

       DeleteProgressStream.deleteStream(migrationClient, progress);
        System.out.println("Test 7 passed");
   }

}
